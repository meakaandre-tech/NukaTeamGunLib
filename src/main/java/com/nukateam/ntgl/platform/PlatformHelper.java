package com.nukateam.ntgl.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

/**
 * Loader helpers replacing the NeoForge utilities NTGL used (FMLEnvironment, ModList, FMLPaths,
 * item burn time, current server).
 */
public final class PlatformHelper {
    private static MinecraftServer server;

    private PlatformHelper() {
    }

    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    /** The running (integrated or dedicated) server, or null. */
    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer current) {
        server = current;
    }

    /**
     * Furnace burn time of the stack in ticks. Replaces ItemStack#getBurnTime.
     * <p>
     * 26.3: burn times are the item's cooking_fuel component. Vanilla values are references into a
     * server registry, so they resolve to the real number wherever a server runs in this JVM
     * (dedicated server, singleplayer). A client on a remote server only knows that the item is a
     * fuel and gets {@link #UNKNOWN_BURN_TIME}.
     */
    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        var fuel = stack.get(net.minecraft.core.component.DataComponents.COOKING_FUEL);
        if (fuel == null) return 0;

        var current = server;
        if (current != null) {
            try {
                var level = current.overworld();
                if (level != null) {
                    var params = new net.minecraft.world.level.storage.loot.LootParams.Builder(level)
                            .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.EMPTY);
                    var context = new net.minecraft.world.level.storage.loot.LootContext.Builder(params).create(java.util.Optional.empty());
                    return Math.max(0, fuel.burnTime().get(context, 0));
                }
            } catch (RuntimeException e) {
                // registries not ready (server starting or stopping); fall through
            }
        }

        if (fuel.burnTime() instanceof net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(int value)) {
            return Math.max(0, value);
        }
        return UNKNOWN_BURN_TIME;
    }

    /** Burn time reported for a fuel whose real value only the server can compute (one smelted item). */
    public static final int UNKNOWN_BURN_TIME = 200;
}
