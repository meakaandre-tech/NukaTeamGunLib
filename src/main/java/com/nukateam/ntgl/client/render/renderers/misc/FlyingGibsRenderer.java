package com.nukateam.ntgl.client.render.renderers.misc;

import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nukateam.ntgl.ClientProxy;
import com.nukateam.ntgl.common.util.data.Rgba;
import com.nukateam.ntgl.common.foundation.entity.FlyingGib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;

import static com.nukateam.ntgl.client.render.renderers.misc.DeathFxRenderer.setupGoreData;
import static com.nukateam.ntgl.common.foundation.entity.projectile.DeathEffect.getGoreData;
public class FlyingGibsRenderer extends LegacyEntityRenderer<FlyingGib> {
    public static final int MAX_DEATH_TIME = 20;

    public FlyingGibsRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }
    @Override
    public void render(FlyingGib flyingGib, float pEntityYaw, float pPartialTick, PoseStack poseStack,
                       SubmitNodeCollector buffer, CameraRenderState cameraState, int packedLight) {
        var entity = flyingGib.getLocalEntity();
        if(entity == null) return;

        var data = flyingGib.getData(); //getGoreData(entity);
//        setupGoreData(entity, data);

        var isGeoModel = false;

        if (data.model != null) {
            poseStack.pushPose();
            {
                var render = ClientProxy.getEntityRenderer(entity);
                if (render instanceof LivingEntityRenderer livingRenderer) {
                    try {
                        if (data.texture == null) {
                            var entityState = (LivingEntityRenderState) livingRenderer.createRenderState(entity, pPartialTick);
                            data.texture = livingRenderer.getTextureLocation(entityState);
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    poseStack.rotate(Axis.ZP.rotationDegrees(180));
                }

                if (data.texture == null) {
                    poseStack.popPose();
                    return;
                }

                var partialTickTime = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

                if (flyingGib.onGround()) {
                    if (flyingGib.timeToLive <= 20) {
                        float offsetY = ((20 - flyingGib.timeToLive) + partialTickTime) * -0.05f;

                        if(isGeoModel)
                            poseStack.translate(0.0f, offsetY, 0.0f);
                        else poseStack.translate(0.0f, -offsetY, 0.0f);
                    }
                }

                poseStack.translate(0,-entity.getType().getHeight() / 2,0);

                var partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
                var texture = data.texture;
                var rendertype = RenderTypes.entityTranslucent(texture);
                var prog = (entity.deathTime + partialTicks - 1.0F) / MAX_DEATH_TIME;
                var reverseProg = 1.0f - prog;
                var scale = 1.0f + prog / 2;
                var reverseScale = 1.0f - prog / 4;
                var rgba = Rgba.DEFAULT;


                switch (flyingGib.getData().deathType){
                    case LASER:
                        poseStack.scale(scale, scale, scale);

                        if(isGeoModel)
                            poseStack.translate(0, (-scale / 2) / 16D, 0);
                        else poseStack.translate(0, -scale / 2, 0);

                        rgba = rgba.setAlpha(reverseProg);
                        break;
                    case FIRE :
                        poseStack.scale(reverseScale, 1, reverseScale);

                        if(isGeoModel)
                            poseStack.translate(0, (-reverseScale / 2) / 16D, 0);
                        else poseStack.translate(0, -reverseScale / 2, 0);

//                        if (entity.deathTime > 0) {
//                            var partialTicks = Minecraft.getInstance().getFrameTime();
//                            var rotProg = ((float)entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
//                            rotProg = Mth.sqrt(rotProg);
//                            if (rotProg > 1.0F) rotProg = 1.0F;
//                            poseStack.mulPose(Axis.ZP.rotationDegrees(rotProg *  90.0F));
//                        }

//                        rgba = rgba.setAlpha(reverseProg);
                        break;

                    case GORE:
                        poseStack.rotate(Axis.XP.rotationDegrees(prog * (float) flyingGib.rotationAxis.x));
                        poseStack.rotate(Axis.YP.rotationDegrees(prog * (float) flyingGib.rotationAxis.y));
                        poseStack.rotate(Axis.ZP.rotationDegrees(prog * (float) flyingGib.rotationAxis.z));
                        break;
                }

                data.model.render(entity, flyingGib.getPartId(), poseStack, rendertype, buffer,
                        packedLight, 0xFFFFFF, rgba.getIntColor());
            }
            poseStack.popPose();
        }
    }
}
