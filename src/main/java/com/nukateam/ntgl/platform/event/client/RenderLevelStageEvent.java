package com.nukateam.ntgl.platform.event.client;

import com.nukateam.ntgl.platform.Event;

/** Fired once per frame while the level is rendered (NTGL only uses it as a per-frame hook). */
public class RenderLevelStageEvent extends Event {
    private final float partialTick;
    private final int renderTick;

    public RenderLevelStageEvent(float partialTick, int renderTick) {
        this.partialTick = partialTick;
        this.renderTick = renderTick;
    }

    public float getPartialTick() {
        return partialTick;
    }

    /** Number of ticks the client has been rendering the level, as NeoForge's event reported it. */
    public int getRenderTick() {
        return renderTick;
    }
}
