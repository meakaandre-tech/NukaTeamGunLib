package com.nukateam.ntgl.platform;

import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

/**
 * Stand-in for NeoForge's IPayloadContext, so the packet handlers keep their shape.
 */
public record IPayloadContext(Player player, Consumer<Runnable> executor) {
    public void enqueueWork(Runnable work) {
        executor.accept(work);
    }
}
