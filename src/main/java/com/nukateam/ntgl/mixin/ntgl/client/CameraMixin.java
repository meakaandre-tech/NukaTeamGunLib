package com.nukateam.ntgl.mixin.ntgl.client;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.util.handler.WeaponRenderingHandler;
import com.nukateam.ntgl.platform.event.client.ViewportEvent;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Raises ViewportEvent.ComputeFov and ViewportEvent.ComputeCameraAngles (NeoForge hooks) so the aim
 * zoom, the camera recoil and the strafing camera roll keep working.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;
    @Shadow private int matrixPropertiesDirty;

    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "calculateFov(F)F", at = @At("RETURN"), cancellable = true)
    private void ntgl$computeFov(float partialTick, CallbackInfoReturnable<Float> cir) {
        WeaponRenderingHandler.get().setUsedConfiguredFov(true);
        var event = Ntgl.EVENT_BUS.post(new ViewportEvent.ComputeFov(partialTick, cir.getReturnValueF(), true));
        if (event.getFOV() != cir.getReturnValueF()) {
            cir.setReturnValue(event.getFOV());
        }
    }

    @Inject(method = "calculateHudFov(F)F", at = @At("RETURN"), cancellable = true)
    private void ntgl$computeHudFov(float partialTick, CallbackInfoReturnable<Float> cir) {
        WeaponRenderingHandler.get().setUsedConfiguredFov(false);
        var event = Ntgl.EVENT_BUS.post(new ViewportEvent.ComputeFov(partialTick, cir.getReturnValueF(), false));
        WeaponRenderingHandler.get().setUsedConfiguredFov(true);
        if (event.getFOV() != cir.getReturnValueF()) {
            cir.setReturnValue(event.getFOV());
        }
    }

    @Inject(method = "alignWithEntity(F)V", at = @At("TAIL"))
    private void ntgl$computeCameraAngles(float partialTick, CallbackInfo ci) {
        float yaw = this.yRot;
        float pitch = this.xRot;
        var event = Ntgl.EVENT_BUS.post(new ViewportEvent.ComputeCameraAngles(partialTick, yaw, pitch, 0F));
        if (event.getYaw() != yaw || event.getPitch() != pitch) {
            this.setRotation(event.getYaw(), event.getPitch());
        }
        if (event.getRoll() != 0F) {
            // roll the view around the look axis; the up and left vectors follow
            this.rotation.rotateZ(-event.getRoll() * Mth.DEG_TO_RAD);
            this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
            this.left.set(-1.0F, 0.0F, 0.0F).rotate(this.rotation);
            this.matrixPropertiesDirty |= 3;
        }
    }
}
