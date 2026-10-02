package com.nukateam.ntgl.modules.gunpack.resource;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Wraps a gun pack so packs made for the 1.20/1.21 versions of NTGL keep working on 26.x:
 * <ul>
 *     <li>GeckoLib 5 reads models and animations from <code>geckolib/models</code> and
 *     <code>geckolib/animations</code>; the old <code>geo</code> and <code>animations</code> folders are served there too</li>
 *     <li>items need a definition in <code>items/</code>; one is generated for every <code>models/item</code> file without one</li>
 *     <li>the <code>builtin/entity</code> model parent no longer exists and is dropped from item models</li>
 *     <li>a missing or outdated <code>pack.mcmeta</code> does not prevent the pack from loading</li>
 *     <li><code>data/&lt;ns&gt;/recipes</code> is also read as <code>recipe</code></li>
 * </ul>
 * Files that already follow the new layout always win.
 */
public class LegacyGunPackResources implements PackResources {
    private static final String[][] CLIENT_FOLDERS = {{"geckolib/models", "geo"}, {"geckolib/animations", "animations"}};
    private static final String[][] SERVER_FOLDERS = {{"recipe", "recipes"}};
    private static final Pattern BUILTIN_PARENT = Pattern.compile("\"parent\"\\s*:\\s*\"(minecraft:)?builtin/entity\"\\s*,?");
    private static final Pattern PARENT = Pattern.compile("\"parent\"\\s*:\\s*\"([^\"]+)\"");
    private static final String ITEMS = "items";
    private static final String ITEM_MODELS = "models/item";

    private final PackResources delegate;
    private final PackType packType;

    public LegacyGunPackResources(PackResources delegate, PackType packType) {
        this.delegate = delegate;
        this.packType = packType;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... path) {
        return delegate.getRootResource(path);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, Identifier id) {
        var direct = delegate.getResource(type, id);
        if (direct != null)
            return patch(type, id, direct);

        try {
            var path = id.getPath();

            for (var folders : folders(type)) {
                if (path.startsWith(folders[0] + "/")) {
                    var legacy = delegate.getResource(type, id.withPath(folders[1] + path.substring(folders[0].length())));
                    if (legacy != null) return legacy;
                }
            }

            if (type == PackType.CLIENT_RESOURCES && path.startsWith(ITEMS + "/") && path.endsWith(".json")) {
                var name = path.substring(ITEMS.length() + 1, path.length() - ".json".length());
                return itemDefinition(id.getNamespace(), name);
            }
        } catch (RuntimeException e) {
            Ntgl.LOGGER.warn("Gun pack {}: could not map {}", packId(), id, e);
        }

        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        Set<Identifier> seen = new HashSet<>();

        delegate.listResources(type, namespace, path, (id, supplier) -> {
            seen.add(id);
            output.accept(id, patch(type, id, supplier));
        });

        try {
            for (var folders : folders(type)) {
                if (!path.equals(folders[0]) && !path.startsWith(folders[0] + "/")) continue;
                var legacyPath = folders[1] + path.substring(folders[0].length());

                delegate.listResources(type, namespace, legacyPath, (id, supplier) -> {
                    var mapped = id.withPath(folders[0] + id.getPath().substring(folders[1].length()));
                    if (seen.add(mapped))
                        output.accept(mapped, supplier);
                });
            }

            if (type == PackType.CLIENT_RESOURCES && path.equals(ITEMS)) {
                delegate.listResources(type, namespace, ITEM_MODELS, (id, supplier) -> {
                    var modelPath = id.getPath();
                    if (!modelPath.endsWith(".json")) return;
                    var name = modelPath.substring(ITEM_MODELS.length() + 1, modelPath.length() - ".json".length());
                    var definitionId = id.withPath(ITEMS + "/" + name + ".json");
                    if (seen.add(definitionId)) {
                        var definition = itemDefinition(namespace, name);
                        if (definition != null)
                            output.accept(definitionId, definition);
                    }
                });
            }
        } catch (RuntimeException e) {
            Ntgl.LOGGER.warn("Gun pack {}: could not list legacy files of {}:{}", packId(), namespace, path, e);
        }
    }

    private static String[][] folders(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? CLIENT_FOLDERS : SERVER_FOLDERS;
    }

    /** Item models: drops the "builtin/entity" parent that 1.21.4 removed. */
    private IoSupplier<InputStream> patch(PackType type, Identifier id, IoSupplier<InputStream> supplier) {
        if (type != PackType.CLIENT_RESOURCES || !id.getPath().startsWith(ITEM_MODELS + "/") || !id.getPath().endsWith(".json"))
            return supplier;

        return () -> {
            var text = readText(supplier);
            var patched = BUILTIN_PARENT.matcher(text).replaceAll("");
            return new ByteArrayInputStream(patched.getBytes(StandardCharsets.UTF_8));
        };
    }

    /** The item definition (26.x "items" folder) of an item that only has a classic item model. */
    @Nullable
    private IoSupplier<InputStream> itemDefinition(String namespace, String name) {
        if (delegate.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(namespace, ITEM_MODELS + "/" + name + ".json")) == null)
            return null;

        var special = usesEntityRenderer(namespace, name, 0);
        var json = special
                ? "{\"model\":{\"type\":\"minecraft:special\",\"base\":\"%s:item/%s\",\"model\":{\"type\":\"geckolib:geckolib\"}}}"
                : "{\"model\":{\"type\":\"minecraft:model\",\"model\":\"%s:item/%s\"}}";
        var bytes = json.formatted(namespace, name).getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    /** True when the item model (or one of its parents in this pack) was rendered by the item's own renderer. */
    private boolean usesEntityRenderer(String namespace, String name, int depth) {
        if (depth > 8) return false;
        var model = delegate.getResource(PackType.CLIENT_RESOURCES, Identifier.fromNamespaceAndPath(namespace, ITEM_MODELS + "/" + name + ".json"));
        if (model == null) return false;

        try {
            var text = readText(model);
            if (BUILTIN_PARENT.matcher(text).find()) return true;

            var parent = PARENT.matcher(text);
            if (parent.find()) {
                var parentId = Identifier.tryParse(parent.group(1));
                if (parentId != null && parentId.getPath().startsWith("item/"))
                    return usesEntityRenderer(parentId.getNamespace(), parentId.getPath().substring("item/".length()), depth + 1);
            }
        } catch (IOException e) {
            Ntgl.LOGGER.warn("Gun pack {}: could not read the item model {}:{}", packId(), namespace, name, e);
        }

        return false;
    }

    private static String readText(IoSupplier<InputStream> supplier) throws IOException {
        try (var stream = supplier.get()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return delegate.getNamespaces(type);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
        try {
            var section = delegate.getMetadataSection(type);
            if (section != null) return section;
        } catch (IOException | RuntimeException e) {
            Ntgl.LOGGER.debug("Gun pack {}: unreadable pack.mcmeta section {}", packId(), type.name(), e);
        }

        if (type.name().equals(PackMetadataSection.CLIENT_TYPE.name())) {
            var format = SharedConstants.getCurrentVersion().packVersion(packType);
            return (T) new PackMetadataSection(Component.literal("NTGL gun pack"), new InclusiveRange<>(format, format));
        }

        return null;
    }

    @Override
    public PackLocationInfo location() {
        return delegate.location();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
