package com.nukateam.ntgl.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.FuelValues;

import java.nio.file.Path;

/**
 * Loader helpers replacing the NeoForge utilities NTGL used (FMLEnvironment, ModList, FMLPaths,
 * item burn time, current server).
 */
public final class PlatformHelper {
    private static MinecraftServer server;
    private static FuelValues fuelValues;

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
        if (current != null) {
            fuelValues = current.fuelValues();
        }
    }

    /** Called on the client when a level is joined, so burn times are known on remote servers too. */
    public static void setFuelValues(FuelValues values) {
        if (values != null) {
            fuelValues = values;
        }
    }

    /** Furnace burn time of the stack in ticks; 0 while no world is loaded. Replaces ItemStack#getBurnTime. */
    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty() || fuelValues == null) return 0;
        return fuelValues.burnDuration(stack);
    }
}
