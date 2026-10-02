package com.nukateam.ntgl.platform.event;

import com.nukateam.ntgl.platform.Event;
import net.minecraft.world.entity.Entity;

/** Fired at the start of LivingEntity#tick on both sides (NTGL only listens for living entities). */
public abstract class EntityTickEvent extends Event {
    private final Entity entity;

    protected EntityTickEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Pre extends EntityTickEvent {
        public Pre(Entity entity) {
            super(entity);
        }
    }
}
