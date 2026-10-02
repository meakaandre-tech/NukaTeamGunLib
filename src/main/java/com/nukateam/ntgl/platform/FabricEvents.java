package com.nukateam.ntgl.platform;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.platform.event.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Raises the game events NTGL's handlers listen to (formerly NeoForge events) from Fabric callbacks.
 * Player and living entity ticks come from mixins (PlayerTickMixin, LivingEntityTickMixin).
 */
public final class FabricEvents {
    private FabricEvents() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(PlatformHelper::setServer);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlatformHelper.setServer(server);
            Ntgl.EVENT_BUS.post(new ServerStartedEvent(server));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Ntgl.EVENT_BUS.post(new ServerStoppingEvent(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            Ntgl.EVENT_BUS.post(new ServerStoppedEvent(server));
            PlatformHelper.setServer(null);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> Ntgl.EVENT_BUS.post(new ServerTickEvent.Pre(server)));
        ServerTickEvents.END_SERVER_TICK.register(server -> Ntgl.EVENT_BUS.post(new ServerTickEvent.Post(server)));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> Ntgl.EVENT_BUS.post(new LivingDeathEvent(entity, source)));
        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, from, to) ->
                Ntgl.EVENT_BUS.post(new LivingEquipmentChangeEvent(entity, slot, from, to)));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                Ntgl.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                Ntgl.EVENT_BUS.post(new PlayerEvent.PlayerLoggedOutEvent(handler.player)));
    }
}
