package com.nukateam.ntgl.client.util.pose;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.common.data.holders.GripType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.common.util.helpers.PlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

/**
 * Author: MrCrayfish
 */
public class BazookaPose extends WeaponPose {
    @Override
    protected AimPose getUpPose() {
        AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(35F).setItemRotation(new Vector3f(10F, 0F, 0F)).setRightArm(new LimbPose().setRotationAngleX(-170F).setRotationAngleY(-35F).setRotationAngleZ(0F).setRotationPointY(4).setRotationPointZ(-2)).setLeftArm(new LimbPose().setRotationAngleX(-130F).setRotationAngleY(65F).setRotationAngleZ(0F).setRotationPointX(3).setRotationPointY(2).setRotationPointZ(1));
        return pose;
    }

    @Override
    protected AimPose getForwardPose() {
        AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(35F)
                .setRightArm(new LimbPose()
                        .setRotationAngleX(-90F)
                        .setRotationAngleY(-35F)
                        .setRotationAngleZ(0F)

                        .setRotationPointY(2)
                        .setRotationPointZ(0))

                .setLeftArm(new LimbPose()
                        .setRotationAngleX(-91F)
                        .setRotationAngleY(35F)
                        .setRotationAngleZ(0F)

                        .setRotationPointX(4)
                        .setRotationPointY(2)
                        .setRotationPointZ(0));
        return pose;
    }

    @Override
    protected AimPose getDownPose() {
        AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(35F).setRightArm(new LimbPose().setRotationAngleX(-10F).setRotationAngleY(-35F).setRotationAngleZ(0F).setRotationPointY(2).setRotationPointZ(0)).setLeftArm(new LimbPose().setRotationAngleX(-10F).setRotationAngleY(15F).setRotationAngleZ(30F).setRotationPointX(4).setRotationPointY(2).setRotationPointZ(0));
        return pose;
    }

    @Override
    protected boolean hasAimPose() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void applyHumanoidModelRotation(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart head, InteractionHand hand, float aimProgress) {
        if(hand == InteractionHand.OFF_HAND) return;
        if (Config.CLIENT.display.oldAnimations.get()) {
            boolean right = PlayerHelper.isRight(hand);
            ModelPart mainArm = right ? rightArm : leftArm;
            ModelPart secondaryArm = right ? leftArm : rightArm;
            mainArm.xRot = (float) Math.toRadians(-90F);
            mainArm.yRot = (float) Math.toRadians(-35F) * (right ? 1F : -1F);
            mainArm.zRot = (float) Math.toRadians(0F);
            secondaryArm.xRot = (float) Math.toRadians(-91F);
            secondaryArm.yRot = (float) Math.toRadians(45F) * (right ? 1F : -1F);
            secondaryArm.zRot = (float) Math.toRadians(0F);
        } else {
            super.applyHumanoidModelRotation(entity, rightArm, leftArm, head, hand, aimProgress);
        }
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void applyEntityPreRender(LivingEntity entity, InteractionHand hand, float aimProgress) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            boolean right = Minecraft.getInstance().options.mainHand().get() == HumanoidArm.RIGHT ? hand == InteractionHand.MAIN_HAND : hand == InteractionHand.OFF_HAND;
            entity.yBodyRotO = entity.yRotO + 35F * (right ? 1F : -1F);
            entity.yBodyRot = entity.getYRot() + 35F * (right ? 1F : -1F);
        } else {
            super.applyEntityPreRender(entity, hand, aimProgress);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void applyHeldItemTransforms(LivingEntity entity, InteractionHand hand, float aimProgress, PoseStack poseStack) {
        if (!Config.CLIENT.display.oldAnimations.get()) {
            super.applyHeldItemTransforms(entity, hand, aimProgress, poseStack);
        }
//        poseStack.translate(-0.5, -0.45, -1);
//        poseStack.translate(X * 0.0625, Y * 0.0625, Z * 0.0625);
        poseStack.translate(-10 * 0.0625, -11 * 0.0625, -20 * 0.0625);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean applyOffhandTransforms(LivingEntity entity, HumanoidModel<?> model, ItemStack stack, PoseStack poseStack, float partialTicks) {
        return GripType.applyBackTransforms(entity, poseStack);
    }

    @Override
    public boolean canApplySprintingAnimation() {
        return false;
    }
}
