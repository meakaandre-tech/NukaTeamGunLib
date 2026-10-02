package com.nukateam.ntgl.client.event;

import com.nukateam.ntgl.client.render.hud.cache.GunHudCache;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.InteractionHand;
import com.nukateam.ntgl.platform.Event;
import com.nukateam.ntgl.platform.ICancellableEvent;

public class GunHudEvent extends Event implements ICancellableEvent {
    private final InteractionHand hand;
    private final GuiGraphicsExtractor graphics;
    private final GunHudCache cache;
    private final GunHudEvent.Phase phase;


    public GunHudEvent(InteractionHand hand, GuiGraphicsExtractor graphics, GunHudCache cache, Phase phase) {
        this.hand = hand;
        this.graphics = graphics;
        this.cache = cache;
        this.phase = phase;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public GunHudCache getCache() {
        return cache;
    }

    public GuiGraphicsExtractor getGraphics() {
        return graphics;
    }

    public Phase getRenderPhase() {
        return phase;
    }

    public enum Phase {
        START, END;
    }
}
