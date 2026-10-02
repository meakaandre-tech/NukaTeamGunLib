package com.nukateam.ntgl.platform.event.client;

import com.nukateam.ntgl.platform.Event;

/** Fired once per frame while the level is rendered (NTGL only uses it as a per-frame hook). */
public class RenderLevelStageEvent extends Event {
    private final float partialTick;

    public RenderLevelStageEvent(float partialTick) {
        this.partialTick = partialTick;
    }

    public float getPartialTick() {
        return partialTick;
    }
}
