package com.nukateam.ntgl.mixin.ntgl.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.foundation.item.interfaces.IWeapon;
import com.nukateam.ntgl.platform.event.client.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 * <p>
 * Fabric port: also raises InputEvent.InteractionKeyMappingTriggered for the attack and use keys
 * (NeoForge fired it from the same places), which is how the gun handlers swallow the vanilla
 * attack/use while a weapon is held.
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public LocalPlayer player;
    @Shadow @Final public Options options;

    @Inject(method = "startAttack()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true)
    private void ntgl$onAttackKey(CallbackInfoReturnable<Boolean> cir) {
        var event = Ntgl.EVENT_BUS.post(new InputEvent.InteractionKeyMappingTriggered(0, this.options.keyAttack, InteractionHand.MAIN_HAND));
        if (event.isCanceled()) {
            if (event.shouldSwingHand()) {
                this.player.swing(InteractionHand.MAIN_HAND);
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack(Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"),
            cancellable = true)
    private void ntgl$onContinueAttack(boolean down, CallbackInfo ci) {
        var event = Ntgl.EVENT_BUS.post(new InputEvent.InteractionKeyMappingTriggered(0, this.options.keyAttack, InteractionHand.MAIN_HAND));
        if (event.isCanceled()) {
            if (event.shouldSwingHand()) {
                this.player.swing(InteractionHand.MAIN_HAND);
            }
            ci.cancel();
        }
    }

    @Inject(method = "startUseItem()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true)
    private void ntgl$onUseKey(CallbackInfo ci, @Local InteractionHand hand) {
        var event = Ntgl.EVENT_BUS.post(new InputEvent.InteractionKeyMappingTriggered(1, this.options.keyUse, hand));
        if (event.isCanceled()) {
            if (event.shouldSwingHand()) {
                this.player.swing(hand);
            }
            ci.cancel();
        }
    }

    /** Using a block with a weapon in hand must not restart the weapon's equip animation. */
    @WrapWithCondition(method = "startUseItem()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;itemUsed(Lnet/minecraft/world/InteractionHand;)V", ordinal = 0))
    private boolean ntgl$beforeItemUsed(ItemInHandRenderer renderer, InteractionHand hand) {
        return this.player == null || !(this.player.getItemInHand(hand).getItem() instanceof IWeapon);
    }
}
