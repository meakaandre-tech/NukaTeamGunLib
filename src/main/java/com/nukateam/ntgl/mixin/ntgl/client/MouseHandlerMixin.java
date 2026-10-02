package com.nukateam.ntgl.mixin.ntgl.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.settings.NtglOptions;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import com.nukateam.ntgl.common.foundation.init.ModSyncedDataKeys;
import com.nukateam.ntgl.common.foundation.item.interfaces.IWeapon;
import com.nukateam.ntgl.common.util.util.WeaponStateHelper;
import com.nukateam.ntgl.modules.wheel.ActionWheelManager;
import com.nukateam.ntgl.platform.event.client.InputEvent;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish, Jetug
 */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(method = "turnPlayer(D)V", at = @At("HEAD"), cancellable = true)
    private void onTurnPlayer(double movementTime, CallbackInfo ci) {
        if (ActionWheelManager.getInstance().isWheelActive()) {
            this.accumulatedDX = 0;
            this.accumulatedDY = 0;
            ci.cancel();
        }
    }

    /**
     * Aim-down-sight sensitivity. The 1.21 mixin patched a local of turnPlayer by ordinal; here the
     * final turn deltas are scaled instead, which is the intended effect and not tied to local order.
     */
    @WrapOperation(method = "turnPlayer(D)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void sensitivity(LocalPlayer player, double xo, double yo, Operation<Void> original) {
        var additionalAdsSensitivity = 1.0F;
        var mc = Minecraft.getInstance();
        if (mc.player != null && !mc.player.getMainHandItem().isEmpty() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            var heldItem = mc.player.getMainHandItem();
            if (heldItem.getItem() instanceof IWeapon) {
                var aimHandler = AimingHandler.get();
                if (aimHandler.isAiming() && !ModSyncedDataKeys.RELOADING_RIGHT.getValue(mc.player)) {
                    float modifier = WeaponStateHelper.getFovModifier(aimHandler.getWeaponData());
                    additionalAdsSensitivity = Mth.clamp(1.0F - (1.0F / modifier) / 10F, 0.0F, 1.0F);
                }
            }
        }
        var adsSensitivity = NtglOptions.getInstance().getAdsSensitivity();
        var factor = (1.0 - (1.0 - adsSensitivity) * AimingHandler.get().getNormalisedAdsProgress()) * additionalAdsSensitivity;
        original.call(player, xo * factor, yo * factor);
    }

    @Inject(method = "onButton(JLnet/minecraft/client/input/MouseButtonInfo;I)V", at = @At("TAIL"))
    private void ntgl$afterMouseButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        Ntgl.EVENT_BUS.post(new InputEvent.MouseButton.Post(info.button(), action, info.modifiers()));
    }
}
