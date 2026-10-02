package com.nukateam.ntgl.client.render.renderers.projectiles;

import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.nukateam.ntgl.common.foundation.entity.FlameProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
public class FlameRenderer extends LegacyEntityRenderer<FlameProjectile> {
    public FlameRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(FlameProjectile entity, float entityYaw, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState, int light) {
    }

//    @Override
//    public void render(ProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector renderTypeBuffer, CameraRenderState cameraState, int light) {
//
//    }
}
