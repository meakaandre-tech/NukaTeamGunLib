package com.nukateam.ntgl.client.render.renderers.misc;

import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nukateam.geo.render.AnimatableGeoModel;
import com.nukateam.ntgl.client.model.misc.AshPileModel;
import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import com.nukateam.ntgl.common.foundation.entity.misc.AshPile;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/**
 * Ash pile left by entities killed with fire/laser. It shrinks and fades out at the end of its life.
 */
public class AshPileRenderer extends LegacyEntityRenderer<AshPile> {
    private final GeoRenderer geoRenderer = new GeoRenderer();

    public AshPileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    private static float getAlpha(AshPile entity) {
        var prog = ((float) entity.getLife() / (float) entity.getMaxLife());

        if (prog <= 0.2) {
            var maxAlpha = ((entity.getMaxLife() * 0.2f));
            return ((float) entity.getLife() / maxAlpha);
        }

        return 1.0F;
    }

    @Override
    public void render(AshPile entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState, int light) {
        var alpha = getAlpha(entity);
        if (alpha <= 0) return;

        poseStack.rotate(Axis.YP.rotationDegrees(180f - entityYaw));
        poseStack.scale(alpha, alpha, alpha);
        geoRenderer.performRenderPass(entity, null, poseStack, collector, cameraState, light, partialTicks);
    }

    private static class GeoRenderer extends GeoObjectRenderer<AshPile, Void, GeoRenderState> {
        private final AshPileModel ashModel;

        private GeoRenderer() {
            this(new AshPileModel());
        }

        private GeoRenderer(AshPileModel model) {
            super(model);
            this.ashModel = model;
        }

        @Override
        public int getRenderColor(AshPile animatable, Void relatedObject, float partialTick) {
            return ARGB.white(Math.clamp(getAlpha(animatable), 0f, 1f));
        }

        @Override
        public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
            return ashModel.getRenderType(null, texture);
        }

        @Override
        public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
            // entities are rendered from their feet, not from the centre of a block
        }
    }
}
