package com.nukateam.ntgl.mixin.ntgl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nukateam.ntgl.client.render.NtglRenderData;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import com.nukateam.ntgl.client.util.handler.WeaponRenderingHandler;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.foundation.item.interfaces.IWeapon;
import com.nukateam.ntgl.common.util.util.WeaponModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 * <p>
 * Third person: weapons are drawn by NTGL (hand pose of the grip type + GeckoLib weapon renderer)
 * instead of the vanilla held item. The entity comes from the render state (NtglRenderData.ENTITY).
 */
@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
    @Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void renderArmWithItem(ArmedEntityRenderState state, ItemStackRenderState itemState, ItemStack stack,
                                   HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        LivingEntity entity = NtglRenderData.getEntity(state);
        if (com.nukateam.ntgl.client.util.RenderDebug.ENABLED) com.nukateam.ntgl.client.util.RenderDebug.log("layer." + arm, () -> stack.getItem() + " entity=" + entity + " pose " + com.nukateam.ntgl.client.util.RenderDebug.pose(poseStack));
        if (entity == null) return;

        var hand = state.mainArm == arm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        var heldStack = entity.getItemInHand(hand);
        if (stack.getItem() != heldStack.getItem()) return;

        var oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        var oppositeStack = entity.getItemInHand(oppositeHand);

        if (hand == InteractionHand.OFF_HAND) {
            if (!WeaponModifierHelper.isOneHanded(new WeaponData(heldStack, entity)) || !WeaponModifierHelper.isOneHanded(new WeaponData(oppositeStack, entity))) {
                ci.cancel();
                return;
            }
        }

        if (heldStack.getItem() instanceof IWeapon) {
            ci.cancel();
            var layer = (ItemInHandLayer) (Object) this;
            var transformType = arm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

            //Third person render
            poseStack.pushPose();
            {
                ((ArmedModel) layer.getParentModel()).translateToHand(state, arm, poseStack);
                poseStack.rotate(Axis.XP.rotationDegrees(-90F));
                poseStack.rotate(Axis.YP.rotationDegrees(180F));
                WeaponRenderingHandler.get().applyWeaponScale(heldStack, poseStack);
                var gripType = WeaponModifierHelper.getGripType(new WeaponData(heldStack, entity));
                var deltaTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
                var aimProgress = AimingHandler.get().getAimProgress(entity, deltaTicks);
                gripType.getHeldAnimation().applyHeldItemTransforms(entity, hand, aimProgress, poseStack);
                WeaponRenderingHandler.get().renderWeapon(entity, heldStack, transformType, poseStack, collector, light);
            }
            poseStack.popPose();
        }
    }
}
