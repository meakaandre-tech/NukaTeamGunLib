package com.nukateam.ntgl.client.render.renderers.projectiles;

import com.nukateam.ntgl.common.foundation.entity.FlameProjectile;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
public class FlameRenderer extends EntityRenderer<FlameProjectile> {
    public FlameRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(FlameProjectile entity) {
        return null;
    }

//    @Override
//    public void render(ProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light) {
//
//    }
}
