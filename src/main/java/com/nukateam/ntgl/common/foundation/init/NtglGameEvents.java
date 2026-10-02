package com.nukateam.ntgl.common.foundation.init;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class NtglGameEvents {
    public static final ResourceKey<GameEvent> GUNSHOT_EVENT =
            ResourceKey.create(Registries.GAME_EVENT, Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "gunshot_event"));

    private static Holder.Reference<GameEvent> gunshot;

    public static void register() {
        gunshot = Registry.registerForHolder(BuiltInRegistries.GAME_EVENT, GUNSHOT_EVENT, new GameEvent(32));
    }

    public static void gunshotEvent(Level level, LivingEntity entity) {
        if (gunshot != null) {
            level.gameEvent(gunshot, entity.blockPosition(), GameEvent.Context.of(entity));
        }
    }
}
