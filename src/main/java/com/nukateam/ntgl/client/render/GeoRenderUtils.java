package com.nukateam.ntgl.client.render;

import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class GeoRenderUtils {
    private static ModelPart playerModel;

    /**
     * Renders the arm (and sleeve) of the client player at the current pose.
     * Fabric 26.2 port: the chest armor that used to be drawn over the arm is not rendered.
     */
    public static void renderArm(PoseStack poseStack, GeoBone bone, int packedLight,
                                 SubmitNodeCollector collector, boolean right) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (playerModel == null)
            playerModel = mc.getEntityModels().bakeLayer(ModelLayers.PLAYER);

        applyBoneTransform(poseStack, bone);

        var playerSkin = mc.player.getSkin().body().texturePath();
        // sleeves are children of the arm parts since 1.21.9
        renderHand(playerModel.getChild(right ? "right_arm" : "left_arm"), playerSkin, poseStack, collector, packedLight);
    }

    private static void applyBoneTransform(PoseStack poseStack, GeoBone bone) {
        var snapshot = bone.frameSnapshot;

        poseStack.translate(bone.pivotX() / 16f, bone.pivotY() / 16f, bone.pivotZ() / 16f);

        if (snapshot != null) {
            poseStack.rotate(Axis.XP.rotationDegrees(snapshot.getRotX()));
            poseStack.rotate(Axis.YP.rotationDegrees(snapshot.getRotY()));
            poseStack.rotate(Axis.ZP.rotationDegrees(snapshot.getRotZ()));
        }
    }

    private static void renderHand(ModelPart handPart, Identifier texture, PoseStack poseStack,
                                   SubmitNodeCollector collector, int packedLight) {
        handPart.resetPose();
        handPart.xRot = 0f;
        handPart.yRot = 0f;
        handPart.zRot = 0f;
        collector.submitModelPart(handPart, poseStack, RenderTypes.entityTranslucent(texture),
                packedLight, OverlayTexture.NO_OVERLAY, null);
    }
}
