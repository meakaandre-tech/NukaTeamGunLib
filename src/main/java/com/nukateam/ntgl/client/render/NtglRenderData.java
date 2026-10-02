package com.nukateam.ntgl.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Extra data NTGL attaches to entity render states. Since 1.21.9 the renderers only see render
 * states, but the weapon poses, animators and configs are all looked up by entity, so the entity
 * itself rides along (extraction and submission happen on the render thread in the same frame).
 */
public final class NtglRenderData {
    /** The living entity a render state was extracted from. */
    public static final RenderStateDataKey<LivingEntity> ENTITY = RenderStateDataKey.create(() -> "ntgl:entity");
    /** Set when the entity must not be drawn because a death effect (gore, laser, fire) replaces it. */
    public static final RenderStateDataKey<Boolean> HIDDEN = RenderStateDataKey.create(() -> "ntgl:hidden");

    private NtglRenderData() {
    }

    @Nullable
    public static LivingEntity getEntity(EntityRenderState state) {
        return ((FabricRenderState) state).getData(ENTITY);
    }

    public static boolean isHidden(EntityRenderState state) {
        return Boolean.TRUE.equals(((FabricRenderState) state).getData(HIDDEN));
    }
}
