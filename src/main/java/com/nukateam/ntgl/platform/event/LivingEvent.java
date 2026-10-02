package com.nukateam.ntgl.platform.event;

import com.nukateam.ntgl.platform.Event;
import net.minecraft.world.entity.LivingEntity;

/** Base of the events about a living entity. Stand-in for NeoForge's LivingEvent. */
public abstract class LivingEvent extends Event {
    private final LivingEntity entity;

    protected LivingEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
