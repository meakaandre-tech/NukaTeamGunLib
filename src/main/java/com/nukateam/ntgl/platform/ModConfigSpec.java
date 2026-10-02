package com.nukateam.ntgl.platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Small JSON-backed stand-in for NeoForge's ModConfigSpec: same builder calls and the same
 * {@code value.get()} API, stored as config/&lt;file&gt;.json.
 */
public class ModConfigSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger("ntgl-config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<ConfigValue<?>> values;
    private String fileName;

    private ModConfigSpec(List<ConfigValue<?>> values) {
        this.values = values;
    }

    /** Reads the file (if present), applies the values and writes the complete file back. */
    public void load(String fileName) {
        this.fileName = fileName;
        Path path = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json != null) {
                    for (var value : values) {
                        value.read(json);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Could not read {}, using defaults", fileName, e);
            }
        }
        save();
    }

    public void save() {
        if (fileName == null) return;
        JsonObject json = new JsonObject();
        for (var value : values) {
            value.write(json);
        }
        Path path = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(json, writer);
        } catch (Exception e) {
            LOGGER.error("Could not write {}", fileName, e);
        }
    }

    public static class Builder {
        private final Deque<String> path = new ArrayDeque<>();
        private final List<ConfigValue<?>> values = new ArrayList<>();
        private String comment;

        public Builder comment(String... comment) {
            this.comment = String.join("\n", comment);
            return this;
        }

        public Builder push(String name) {
            path.addLast(name);
            comment = null;
            return this;
        }

        public Builder pop() {
            path.removeLast();
            return this;
        }

        private <V extends ConfigValue<?>> V add(V value) {
            values.add(value);
            comment = null;
            return value;
        }

        private List<String> pathOf(String name) {
            var list = new ArrayList<>(path);
            list.add(name);
            return list;
        }

        public BooleanValue define(String name, boolean defaultValue) {
            return add(new BooleanValue(pathOf(name), defaultValue));
        }

        public ConfigValue<String> define(String name, String defaultValue) {
            return add(new ConfigValue<>(pathOf(name), defaultValue, JsonElement::getAsString, v -> GSON.toJsonTree(v)));
        }

        public IntValue defineInRange(String name, int defaultValue, int min, int max) {
            return add(new IntValue(pathOf(name), defaultValue, Math.min(min, max), Math.max(min, max)));
        }

        public DoubleValue defineInRange(String name, double defaultValue, double min, double max) {
            return add(new DoubleValue(pathOf(name), defaultValue, min, max));
        }

        public <E extends Enum<E>> EnumValue<E> defineEnum(String name, E defaultValue) {
            return add(new EnumValue<>(pathOf(name), defaultValue));
        }

        public <T> ConfigValue<List<? extends T>> defineList(String name, List<? extends T> defaultValue, Predicate<Object> validator) {
            return add(new ConfigValue<>(pathOf(name), defaultValue, json -> {
                var list = new ArrayList<T>();
                for (JsonElement element : json.getAsJsonArray()) {
                    @SuppressWarnings("unchecked")
                    T item = (T) element.getAsString();
                    list.add(item);
                }
                return list;
            }, list -> {
                var array = new JsonArray();
                for (var item : list) {
                    array.add(String.valueOf(item));
                }
                return array;
            }));
        }

        public <T> Pair<T, ModConfigSpec> configure(Function<Builder, T> consumer) {
            T result = consumer.apply(this);
            return Pair.of(result, new ModConfigSpec(values));
        }

        public ModConfigSpec build() {
            return new ModConfigSpec(values);
        }
    }

    public static class ConfigValue<T> implements Supplier<T> {
        private final List<String> path;
        private final T defaultValue;
        private final Function<JsonElement, T> reader;
        private final Function<T, JsonElement> writer;
        private T value;

        ConfigValue(List<String> path, T defaultValue, Function<JsonElement, T> reader, Function<T, JsonElement> writer) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        public T get() {
            return value;
        }

        public T getDefault() {
            return defaultValue;
        }

        public void set(T value) {
            this.value = value;
        }

        public List<String> getPath() {
            return path;
        }

        protected T validate(T value) {
            return value;
        }

        void read(JsonObject root) {
            JsonObject object = root;
            for (int i = 0; i < path.size() - 1; i++) {
                var child = object.get(path.get(i));
                if (child == null || !child.isJsonObject()) return;
                object = child.getAsJsonObject();
            }
            var element = object.get(path.get(path.size() - 1));
            if (element == null || element.isJsonNull()) return;
            try {
                value = validate(reader.apply(element));
            } catch (Exception e) {
                LOGGER.warn("Invalid config value for {}, using default", String.join(".", path));
                value = defaultValue;
            }
        }

        void write(JsonObject root) {
            JsonObject object = root;
            for (int i = 0; i < path.size() - 1; i++) {
                var child = object.get(path.get(i));
                if (child == null || !child.isJsonObject()) {
                    child = new JsonObject();
                    object.add(path.get(i), child);
                }
                object = child.getAsJsonObject();
            }
            object.add(path.get(path.size() - 1), writer.apply(value));
        }
    }

    public static class BooleanValue extends ConfigValue<Boolean> {
        BooleanValue(List<String> path, boolean defaultValue) {
            super(path, defaultValue, JsonElement::getAsBoolean, v -> GSON.toJsonTree(v));
        }
    }

    public static class IntValue extends ConfigValue<Integer> {
        private final int min;
        private final int max;

        IntValue(List<String> path, int defaultValue, int min, int max) {
            super(path, defaultValue, JsonElement::getAsInt, v -> GSON.toJsonTree(v));
            this.min = min;
            this.max = max;
        }

        @Override
        protected Integer validate(Integer value) {
            return Math.max(min, Math.min(max, value));
        }
    }

    public static class DoubleValue extends ConfigValue<Double> {
        private final double min;
        private final double max;

        DoubleValue(List<String> path, double defaultValue, double min, double max) {
            super(path, defaultValue, JsonElement::getAsDouble, v -> GSON.toJsonTree(v));
            this.min = min;
            this.max = max;
        }

        @Override
        protected Double validate(Double value) {
            return Math.max(min, Math.min(max, value));
        }
    }

    public static class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
        EnumValue(List<String> path, E defaultValue) {
            super(path, defaultValue,
                    json -> Enum.valueOf(defaultValue.getDeclaringClass(), json.getAsString()),
                    v -> GSON.toJsonTree(v.name()));
        }
    }
}
