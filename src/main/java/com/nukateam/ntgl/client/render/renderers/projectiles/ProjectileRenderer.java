package com.nukateam.ntgl.client.render.renderers.projectiles;

import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nukateam.ntgl.client.helpers.MuzzleMatrixHelper;
import com.nukateam.ntgl.client.util.helpers.render.ModelRenderUtil;
import com.nukateam.ntgl.common.foundation.entity.ProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
public class ProjectileRenderer extends LegacyEntityRenderer<ProjectileEntity> {
    public ProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(ProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       SubmitNodeCollector renderTypeBuffer, CameraRenderState cameraState, int light) {

        if (!entity.isVisible() || entity.tickCount <= 1)
            return;

        if (entity.tickCount < 3) {
            var shooter = entity.getShooter();
            int shooterId = shooter != null ? shooter.getId() : -1;
            if (shooterId >= 0) {
                Vec3 muzzlePos = MuzzleMatrixHelper.getMuzzleWorldPosForEntity(shooterId, partialTicks);
                if (muzzlePos != null) {
                    double entityX = Mth.lerp((double) partialTicks, entity.xOld, entity.getX());
                    double entityY = Mth.lerp((double) partialTicks, entity.yOld, entity.getY());
                    double entityZ = Mth.lerp((double) partialTicks, entity.zOld, entity.getZ());

                    float progress = (entity.tickCount + partialTicks) / 3.0f;
                    progress = Mth.clamp(progress, 0.0f, 1.0f);
                    float multiplier = 1.0f - progress;

                    poseStack.translate(
                            (muzzlePos.x() - entityX) * multiplier,
                            (muzzlePos.y() - entityY) * multiplier,
                            (muzzlePos.z() - entityZ) * multiplier);
                }
            }
        }

        poseStack.pushPose();

        var item = entity.getItem();

        if (!resolveItem(item, ItemDisplayContext.GROUND, entity).usesBlockLight()) {
            poseStack.mulPose(cameraState.orientation);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            renderItem(item, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, entity);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
            poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
            renderItem(item, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, entity);
        }

        poseStack.popPose();
    }
}
