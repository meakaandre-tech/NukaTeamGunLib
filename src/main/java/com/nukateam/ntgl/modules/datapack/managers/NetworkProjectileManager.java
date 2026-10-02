package com.nukateam.ntgl.modules.datapack.managers;

import com.google.common.collect.ImmutableMap;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.data.config.weapon.ProjectileConfig;
import com.nukateam.ntgl.common.data.json.JsonDeserializers;
import com.nukateam.ntgl.modules.constants.Paths;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import com.nukateam.ntgl.Ntgl;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkProjectileManager extends SimplePreparableReloadListener<Map<Identifier, ProjectileConfig>> {
    private static NetworkProjectileManager instance;
    private Map<Identifier, ProjectileConfig> PROGECTILE_CONFIGS = new HashMap<>();

    @Nullable
    public static NetworkProjectileManager get() {
        return instance;
    }

    public static void register() {
        NetworkProjectileManager manager = new NetworkProjectileManager();
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ntgl.ntglResource("projectiles"), manager);
        NetworkProjectileManager.instance = manager;
    }

    public static void onServerStopped() {
        // the reload listener is registered once on Fabric and lives for the whole game session
    }

    @NotNull
    private static Map<Identifier, Resource> getJsonResources(ResourceManager manager, String path) {
        return manager.listResources(path, (fileName) -> fileName.getPath().endsWith(".json"));
    }

    @Override
    protected Map<Identifier, ProjectileConfig> prepare(ResourceManager manager, ProfilerFiller profiler) {
        var map = new HashMap<Identifier, ProjectileConfig>();
        var resources = new ArrayList<>(getJsonResources(manager, Paths.PROJECTILES).keySet());

        resources.sort((r1, r2) -> {
            if (r1.getNamespace().equals(r2.getNamespace())) return 0;
            return r2.getNamespace().equals(Ntgl.MOD_ID) ? 1 : -1;
        });

        resources.forEach(resourceLocation ->
        {
            manager.getResource(resourceLocation).ifPresent(resource ->
            {
                try (var reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                    var gun = GsonHelper.fromJson(JsonDeserializers.GSON_INSTANCE, reader, ProjectileConfig.class);
                    map.put(resourceLocation, gun);

                }
                catch (InvalidObjectException e) {
                    Ntgl.LOGGER.error("Missing required properties for {}", resourceLocation);
                    e.printStackTrace();
                }
                catch (IOException e) {
                    Ntgl.LOGGER.error("Couldn't parse data file {}", resourceLocation);
                }
            });
        });

        return map;
    }

    @Override
    protected void apply(Map<Identifier, ProjectileConfig> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        var builder = ImmutableMap.<Identifier, ProjectileConfig>builder();
        builder.putAll(objects);
        PROGECTILE_CONFIGS = builder.build();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(PROGECTILE_CONFIGS.size());
        PROGECTILE_CONFIGS.forEach((id, ammo) -> {
            buffer.writeIdentifier(id);
            buffer.writeNbt(ammo.serializeNBT(null));
        });
    }

    public ProjectileConfig getConfig(Identifier id) {
        return PROGECTILE_CONFIGS.get(id);
    }

    public static ImmutableMap<Identifier, ProjectileConfig> read(FriendlyByteBuf buffer) {
        var size = buffer.readVarInt();

        if (size > 0) {
            var builder = ImmutableMap.<Identifier, ProjectileConfig>builder();

            for (int i = 0; i < size; i++) {
                var id = buffer.readIdentifier();
                var ammo = ProjectileConfig.create(buffer.readNbt());
                builder.put(id, ammo);
            }
            return builder.build();
        }
        return ImmutableMap.of();
    }

    /**
     * Updates registered projectile from data provided by the server
     */
    public void update(Map<Identifier, ProjectileConfig> registeredAmmo) {
        if (registeredAmmo != null) {
            PROGECTILE_CONFIGS = registeredAmmo;
        }
    }

    public static class Supplier {
        private ProjectileConfig projectile;

        private Supplier(ProjectileConfig projectile) {
            this.projectile = projectile;
        }

        public ProjectileConfig getAmmo() {
            return this.projectile;
        }
    }
}
