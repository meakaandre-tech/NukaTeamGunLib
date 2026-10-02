package com.nukateam.ntgl.client.render.renderers.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.geo.render.DynamicGeoItemRenderer;
import com.nukateam.geo.render.ItemAnimator;
import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.client.handlers.ClientTickHandler;
import com.nukateam.ntgl.client.render.layers.GlowingLayer;
import com.nukateam.ntgl.client.util.helpers.TransformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static com.nukateam.ntgl.Ntgl.irisLoaded;
import static com.nukateam.ntgl.client.render.GeoRenderUtils.*;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class ArmedModelRenderer<Animator extends ItemAnimator> extends DynamicGeoItemRenderer<Animator> {
    public static final String RIGHT_ARM = "right_arm";
    public static final String LEFT_ARM = "left_arm";
    public static final String RIGHT_ARM_ANIM = "right_arm_anim";
    public static final String LEFT_ARM_ANIM = "left_arm_anim";

    private ItemDisplayContext transformType;

    public ArmedModelRenderer(GeoModel<Animator> model) {
        super(model);
        withRenderLayer(new GlowingLayer<>(this));
        ClientTickHandler.addTicker(this, this::tick);
    }

    protected void tick(){}

    @Override
    public void render(LivingEntity entity, ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                       SubmitNodeCollector collector, int packedLight) {
        this.transformType = transformType;
        this.currentEntity = entity;

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, -50 / 10d / 16d);
            super.render(entity, stack, transformType, poseStack, collector, packedLight);
        }
        poseStack.popPose();
    }

    @Override
    protected void updateBone(RenderPassInfo<GeoRenderState> renderPassInfo, GeoBone bone, BoneSnapshots snapshots) {
        switch (bone.name()) {
            case LEFT_ARM, RIGHT_ARM -> snapshots.get(bone).skipRender(true).skipChildrenRender(false);
            case LEFT_ARM_ANIM, RIGHT_ARM_ANIM -> setHidden(snapshots, bone, !TransformUtils.isFirstPerson(transformType));
        }
    }

    @Override
    protected void addBoneRenders(RenderPassInfo<GeoRenderState> renderPassInfo) {
        if (irisLoaded || !Config.CLIENT.display.renderHands.get()) return;
        if (!TransformUtils.isFirstPerson(this.transformType)) return;

        for (var name : new String[]{LEFT_ARM, RIGHT_ARM}) {
            renderPassInfo.model().getBone(name).ifPresent(bone ->
                    renderPassInfo.addPerBoneRender(bone, (pass, armBone, collector) -> {
                        var poseStack = pass.poseStack();
                        // GeckoLib 5 hands the pose over at the bone pivot; the arm offsets expect the model origin
                        armBone.translateAwayFromPivotPoint(poseStack);
                        renderArms(poseStack, armBone, pass.packedLight(), pass.packedOverlay(), collector);
                    }));
        }
    }

    protected void renderArms(PoseStack poseStack, GeoBone bone, int packedLight, int packedOverlay, SubmitNodeCollector collector) {
        var client = Minecraft.getInstance();
        if(client.player == null) return;

        var isRightHand = this.transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        var isLeftHand = this.transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (isRightHand || isLeftHand) {
            var snapshot = bone.frameSnapshot;

            poseStack.pushPose();
            {
                poseStack.translate(0.01, -0.27, 0.05);
                if (snapshot != null)
                    poseStack.scale(snapshot.getScaleX(), snapshot.getScaleY(), snapshot.getScaleZ());

                if (isRightHand) {
                    if (bone.name().equals(LEFT_ARM)) {
                        poseStack.translate(-65 / 10d / 16d, 0 / 10d / 16d, 0 / 10d / 16d);
                        renderArm(poseStack, bone, packedLight, collector, false);
                    } else if (bone.name().equals(RIGHT_ARM)) {
                        poseStack.translate(50 / 10d / 16d, -20 / 10d / 16d, 0 / 10d / 16d);
                        renderArm(poseStack, bone, packedLight, collector, true);
                    }
                } else {
                    if (bone.name().equals(LEFT_ARM)) {
                        poseStack.translate(50 / 10d / 16d, -20 / 10d / 16d, 0 / 10d / 16d);
                        renderArm(poseStack, bone, packedLight, collector, true);
                    } else if (bone.name().equals(RIGHT_ARM)) {
                        poseStack.translate(-65 / 10d / 16d, 0 / 10d / 16d, 0 / 10d / 16d);
                        renderArm(poseStack, bone, packedLight, collector, false);
                    }
                }
            }
            poseStack.popPose();
        }
    }
}
