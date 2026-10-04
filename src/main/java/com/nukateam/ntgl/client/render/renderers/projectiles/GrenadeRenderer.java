package com.nukateam.ntgl.client.render.renderers.projectiles;

import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.nukateam.ntgl.common.foundation.entity.GrenadeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Author: MrCrayfish
 */
public class GrenadeRenderer extends LegacyEntityRenderer<GrenadeEntity> {
    public GrenadeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(GrenadeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector renderTypeBuffer, CameraRenderState cameraState, int light) {
        if (!entity.isVisible() || entity.tickCount <= 1) return;

        poseStack.pushPose();
        poseStack.rotate(Axis.YP.rotationDegrees(180F));
        poseStack.rotate(Axis.YP.rotationDegrees(entityYaw));
        poseStack.rotate(Axis.XP.rotationDegrees(entity.getXRot()));

        /* Offsets to the center of the grenade before applying rotation */
        float rotation = entity.tickCount + partialTicks;
        poseStack.translate(0, 0.15, 0);
        poseStack.rotate(Axis.XN.rotationDegrees(rotation * 20));
        poseStack.translate(0, -0.15, 0);

        poseStack.translate(0.0, 0.5, 0.0);

        renderItem(entity.getItem(), ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, entity);

        poseStack.popPose();
    }
}
