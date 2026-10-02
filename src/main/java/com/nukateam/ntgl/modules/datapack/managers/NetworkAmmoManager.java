package com.nukateam.ntgl.modules.datapack.managers;

import com.google.common.collect.ImmutableMap;
import com.nukateam.ntgl.modules.constants.Paths;
import com.nukateam.ntgl.modules.datapack.ConfigSupplier;
import com.nukateam.ntgl.modules.datapack.ConfigUtils;
import com.nukateam.ntgl.common.data.config.weapon.ProjectileConfig;
import com.nukateam.ntgl.common.foundation.item.interfaces.IAmmo;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageUpdateAmmo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import com.nukateam.ntgl.Ntgl;
import org.apache.commons.lang3.Validate;
import javax.annotation.Nullable;
import java.util.*;

public class NetworkAmmoManager extends SimplePreparableReloadListener<Map<IAmmo, ProjectileConfig>> {
    private static NetworkAmmoManager instance;

    private Map<Identifier, ProjectileConfig> registeredAmmo = new HashMap<>();


    public static void register() {
        NetworkAmmoManager manager = new NetworkAmmoManager();
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ntgl.ntglResource("ammo"), manager);
        NetworkAmmoManager.instance = manager;
    }

    public static void onServerStopped() {
        // the reload listener is registered once on Fabric and lives for the whole game session
    }

    @Override
    protected Map<IAmmo, ProjectileConfig> prepare(ResourceManager manager, ProfilerFiller profiler) {
        return ConfigUtils.getConfigMap(manager, BuiltInRegistries.ITEM, (v) -> v instanceof IAmmo, ProjectileConfig.class, Paths.AMMO);
    }

    @Override
    protected void apply(Map<IAmmo, ProjectileConfig> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        var builder = ImmutableMap.<Identifier, ProjectileConfig>builder();

        objects.forEach((item, ammo) -> {
            Validate.notNull(BuiltInRegistries.ITEM.getKey((Item)item));
            builder.put(BuiltInRegistries.ITEM.getKey((Item)item), ammo);
            item.setConfig(new ConfigSupplier<>(ammo));
        });

        this.registeredAmmo = builder.build();
    }

    /**
     * Writes all registered projectile into the provided packet buffer
     *
     * @param buffer a packet buffer get
     */
    public void writeRegisteredAmmo(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.registeredAmmo.size());
        this.registeredAmmo.forEach((id, ammo) -> {
            buffer.writeIdentifier(id);
            buffer.writeNbt(ammo.serializeNBT(null));
        });
    }

    /**
     * Reads all registered projectile from the provided packet buffer
     *
     * @param buffer a packet buffer get
     * @return a map of registered projectile from the server
     */
    public static ImmutableMap<Identifier, ProjectileConfig> readRegisteredAmmo(FriendlyByteBuf buffer) {
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

    public static boolean updateRegisteredAmmo(S2CMessageUpdateAmmo message) {
        return updateRegisteredAmmo(message.getRegisteredAmmo());
    }

    /**
     * Updates registered projectile from data provided by the server
     *
     * @return true if all registered projectile were able to update their corresponding projectile item
     */
    private static boolean updateRegisteredAmmo(Map<Identifier, ProjectileConfig> registeredAmmo) {
        if (registeredAmmo != null) {
            for (var entry : registeredAmmo.entrySet()) {
                Item item = BuiltInRegistries.ITEM.getValue(entry.getKey());
                if (!(item instanceof IAmmo)) {
                    return false;
                }
                ((IAmmo) item).setConfig(new ConfigSupplier<>(entry.getValue()));
            }
            return true;
        }
        return false;
    }
    /**
     * Gets the network projectile manager. This will be null if the client isn't running an integrated
     * server or the client is connected to a dedicated server.
     *
     * @return the network projectile manager
     */
    @Nullable
    public static NetworkAmmoManager get() {
        return instance;
    }

    /**
     * A simple wrapper for a projectile object to pass to IAmmo. This is to indicate to developers that
     * Projectile instances shouldn't be changed on GunItems as they are controlled by NetworkAmmoManager.
     * Changes to projectile properties should be made through the JSON file.
     */
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
