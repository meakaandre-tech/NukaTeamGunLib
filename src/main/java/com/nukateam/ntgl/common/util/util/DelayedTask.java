package com.nukateam.ntgl.common.util.util;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.server.MinecraftServer;
import com.nukateam.ntgl.platform.PlatformHelper;
import com.nukateam.ntgl.platform.event.ServerStartedEvent;
import com.nukateam.ntgl.platform.event.ServerStoppingEvent;
import com.nukateam.ntgl.platform.SubscribeEvent;
import com.nukateam.ntgl.platform.event.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple system to run synchronized delayed tasks. See {@link #runAfter(int, Runnable)} to add
 * a delayed task.
 * <p>
 * Author: MrCrayfish
 */
public class DelayedTask {
    public static List<Impl> tasks = new ArrayList<>();

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        tasks.clear();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        tasks.clear();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = PlatformHelper.getServer();
        if (server == null) return;
        var it = tasks.iterator();
        while (it.hasNext()) {
            var impl = it.next();
            if (impl.executionTick <= server.getTickCount()) {
                impl.runnable.run();
                it.remove();
            }
        }
    }

    /**
     * Adds a new delayed task to the system.
     *
     * @param ticks the amount of ticks to delay the execution
     * @param run   a runnable get with the code to run
     */
    public static void runAfter(int ticks, Runnable run) {
        MinecraftServer server = PlatformHelper.getServer();
        if (!server.isSameThread()) {
            throw new IllegalStateException("Tried to add a delayed task off the main thread");
        }
        tasks.add(new Impl(server.getTickCount() + ticks, run));
    }

    private static class Impl {
        private int executionTick;
        private Runnable runnable;

        private Impl(int executionTick, Runnable runnable) {
            this.executionTick = executionTick;
            this.runnable = runnable;
        }
    }
}
