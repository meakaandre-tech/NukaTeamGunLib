package com.nukateam.ntgl.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.Ntgl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Diagnostic logging for the render path, enabled with <code>-Dntgl.debugRender=true</code>.
 * Each key is logged at most once per second.
 */
public final class RenderDebug {
    public static final boolean ENABLED = Boolean.getBoolean("ntgl.debugRender");
    private static final Map<String, Long> LAST = new HashMap<>();

    private RenderDebug() {}

    public static void log(String key, Supplier<String> message) {
        if (!ENABLED) return;

        long now = System.currentTimeMillis();
        Long last = LAST.get(key);
        if (last != null && now - last < 1000) return;
        LAST.put(key, now);

        try {
            Ntgl.LOGGER.info("[render-debug] {}: {}", key, message.get());
        } catch (RuntimeException e) {
            Ntgl.LOGGER.info("[render-debug] {}: failed {}", key, e.toString());
        }
    }

    public static String pose(PoseStack poseStack) {
        var m = poseStack.last().pose();
        return String.format("t=(%.3f, %.3f, %.3f) x=(%.2f, %.2f, %.2f) y=(%.2f, %.2f, %.2f) z=(%.2f, %.2f, %.2f)",
                m.m30(), m.m31(), m.m32(), m.m00(), m.m01(), m.m02(), m.m10(), m.m11(), m.m12(), m.m20(), m.m21(), m.m22());
    }
}
