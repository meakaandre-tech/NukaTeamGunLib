package com.nukateam.ntgl.mixin.ntgl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.platform.event.client.RenderHandEvent;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Raises RenderHandEvent before each first-person hand is submitted (NeoForge fired it from
 * renderHandsWithItems; 26.3: ItemInHandRenderer became FirstPersonHandsAndItemsRenderer). NTGL cancels it for weapons and draws the GeckoLib weapon itself.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {
    @Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            at = @At("HEAD"), cancellable = true)
    private void ntgl$onRenderHand(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState handsState, float partialTick, float pitch, InteractionHand hand,
                                   float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack,
                                   SubmitNodeCollector collector, int packedLight, CallbackInfo ci) {
        var event = new RenderHandEvent(hand, poseStack, collector, packedLight, partialTick, pitch, swingProgress, equipProgress, stack);
        if (Ntgl.EVENT_BUS.post(event).isCanceled()) {
            ci.cancel();
        }
    }
}
