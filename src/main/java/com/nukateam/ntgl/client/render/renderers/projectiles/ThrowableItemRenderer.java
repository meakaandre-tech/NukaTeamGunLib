package com.nukateam.ntgl.client.render.renderers.projectiles;

import com.nukateam.ntgl.client.render.renderers.LegacyEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nukateam.ntgl.common.foundation.entity.throwable.ThrowableItemEntity;
import com.nukateam.ntgl.common.foundation.item.interfaces.IThrowable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
public class ThrowableItemRenderer extends LegacyEntityRenderer<ThrowableItemEntity> {
    public static final int MAX_SIZE_TICK = 5;

    public ThrowableItemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(ThrowableItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, SubmitNodeCollector renderTypeBuffer, CameraRenderState cameraState, int light) {
        poseStack.pushPose();
        {
            /* Makes the grenade face in the direction of travel */
            poseStack.rotate(Axis.YP.rotationDegrees(180F));
            poseStack.rotate(Axis.YP.rotationDegrees(entityYaw));

            /* Offsets to the center of the grenade before applying rotation */
            var rotation = entity.prevRotation + (entity.rotation - entity.prevRotation) * partialTicks;
            poseStack.translate(0, 0.15, 0);
            poseStack.rotate(Axis.XP.rotationDegrees(-rotation));
            poseStack.translate(0, -0.15, 0);

            var scale = 1f;

            if(entity.tickCount < MAX_SIZE_TICK) {
                scale = (entity.tickCount + partialTicks) / MAX_SIZE_TICK;
            }

            poseStack.scale(scale, scale, scale);
            poseStack.translate(0.0, 0.5, 0.0);

            var item = entity.getItem();

            renderItem(item, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, entity);
        }
        poseStack.popPose();
    }
}
