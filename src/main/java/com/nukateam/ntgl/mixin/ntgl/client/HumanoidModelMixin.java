package com.nukateam.ntgl.mixin.ntgl.client;

import com.nukateam.ntgl.client.render.NtglRenderData;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.foundation.item.interfaces.IWeapon;
import com.nukateam.ntgl.common.util.util.WeaponModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * I eventually want to get rid of this.
 * <p>
 * Author: MrCrayfish
 * <p>
 * Fabric port (was LivingEntityModelMixin): poses the arms of an entity holding a weapon after the
 * vanilla animation. Sleeves and hat are children of the arms and head in 26.x, so they follow.
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {
    @SuppressWarnings({"ConstantConditions", "rawtypes"})
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At(value = "TAIL"))
    private void setupAnimTail(HumanoidRenderState state, CallbackInfo ci) {
        LivingEntity entity = NtglRenderData.getEntity(state);
        if (entity == null) return;
        var model = (HumanoidModel) (Object) this;
        setupForArm(entity, state.walkAnimationPos, model, InteractionHand.MAIN_HAND);
        setupForArm(entity, state.walkAnimationPos, model, InteractionHand.OFF_HAND);
    }

    @Unique
    @SuppressWarnings("rawtypes")
    private void setupForArm(LivingEntity entity, float animationPos, HumanoidModel model, InteractionHand interactionHand) {
        var heldItem = entity.getItemInHand(interactionHand);

        if (heldItem.getItem() instanceof IWeapon) {
            if (animationPos == 0.0F) {
                model.rightArm.xRot = 0;
                model.rightArm.yRot = 0;
                model.rightArm.zRot = 0;
                model.leftArm.xRot  = 0;
                model.leftArm.yRot  = 0;
                model.leftArm.zRot  = 0;
                return;
            }

            var aimProgress = AimingHandler.get().getAimProgress(entity, Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks());
            var gripType = WeaponModifierHelper.getGripType(new WeaponData(heldItem, entity));

            gripType.getHeldAnimation().applyHumanoidModelRotation(
                            entity, model.rightArm, model.leftArm, model.head,
                            interactionHand, aimProgress);
        }
    }
}
