package com.nukateam.ntgl.client.util.enums;

import com.mojang.math.Axis;

/**
 * Author: MrCrayfish
 */
public enum SwayType {
    DIRECTIONAL(true),
    DRAG(false);

    // the axes are resolved lazily: this enum is also loaded by the config on dedicated servers
    private final boolean negative;

    SwayType(boolean negative) {
        this.negative = negative;
    }

    public Axis getPitchRotation() {
        return this.negative ? Axis.XN : Axis.XP;
    }

    public Axis getYawRotation() {
        return this.negative ? Axis.YN : Axis.YP;
    }
}
