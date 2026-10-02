package com.nukateam.ntgl.platform.event;

import com.nukateam.ntgl.platform.Event;
import net.minecraft.world.entity.player.Player;

/** Fired at the start and end of Player#tick on both sides. */
public abstract class PlayerTickEvent extends Event {
    private final Player player;

    protected PlayerTickEvent(Player player) {
        this.player = player;
    }

    public Player getEntity() {
        return player;
    }

    public static class Pre extends PlayerTickEvent {
        public Pre(Player player) {
            super(player);
        }
    }

    public static class Post extends PlayerTickEvent {
        public Post(Player player) {
            super(player);
        }
    }
}
