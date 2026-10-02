package com.nukateam.ntgl.mixin.ntgl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.client.render.NtglRenderData;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips entities hidden by a death effect, see EntityRendererMixin.
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("HEAD"), cancellable = true)
    private void ntgl$skipHidden(EntityRenderState state, CameraRenderState camera, double x, double y, double z,
                                 PoseStack poseStack, SubmitNodeCollector collector, CallbackInfo ci) {
        if (NtglRenderData.isHidden(state)) {
            ci.cancel();
        }
    }
}
