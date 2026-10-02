package com.nukateam.ntgl.mixin.ntgl.client;

import com.nukateam.ntgl.client.render.NtglRenderData;
import com.nukateam.ntgl.client.util.handler.EntityModelHandler;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the RenderLivingEvent.Pre hook: the body rotation of an armed entity is adjusted before
 * its render state is extracted, and the entity is attached to the state for the model and layer mixins.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("HEAD"))
    private void ntgl$beforeExtract(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        EntityModelHandler.onRenderEntityPre(entity, partialTick);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void ntgl$afterExtract(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        ((FabricRenderState) state).setData(NtglRenderData.ENTITY, entity);
    }
}
