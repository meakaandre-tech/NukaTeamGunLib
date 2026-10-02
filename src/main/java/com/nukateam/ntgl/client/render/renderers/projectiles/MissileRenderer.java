package com.nukateam.ntgl.client.render.renderers.projectiles;

import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.nukateam.ntgl.common.foundation.entity.MissileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Author: MrCrayfish
 */
public class MissileRenderer extends LegacyEntityRenderer<MissileEntity> {
    public MissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(MissileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector renderTypeBuffer, CameraRenderState cameraState, int light) {
        if (!entity.isVisible() || entity.tickCount <= 1) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot() - 90));

        renderItem(entity.getItem(), ItemDisplayContext.NONE, light, 15728880, poseStack, renderTypeBuffer, entity);

        poseStack.translate(0, -1, 0);
//        ModelRenderUtil.renderModel(SpecialModels.FLAME.getModel(), entity.getItem(), poseStack, renderTypeBuffer, 15728880, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
