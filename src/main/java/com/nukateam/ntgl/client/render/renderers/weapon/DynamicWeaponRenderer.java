package com.nukateam.ntgl.client.render.renderers.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.geo.render.ItemAnimator;
import com.nukateam.ntgl.client.animators.WeaponAnimator;
import com.nukateam.ntgl.client.util.ClientDebug;
import com.nukateam.ntgl.client.helpers.MuzzleMatrixHelper;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import com.nukateam.ntgl.client.util.helpers.TransformUtils;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.data.config.weapon.Modules;
import com.nukateam.ntgl.common.data.config.weapon.WeaponConfig;
import com.nukateam.ntgl.common.data.holders.AttachmentType;
import com.nukateam.ntgl.common.util.util.WeaponModifierHelper;
import com.nukateam.ntgl.common.foundation.item.attachment.BarrelItem;
import com.nukateam.ntgl.common.util.util.WeaponStateHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static com.nukateam.ntgl.client.util.ClientDebug.*;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class DynamicWeaponRenderer<Animator extends ItemAnimator> extends ArmedModelRenderer<Animator> {
    public static final String MUZZLE_FLASH = "muzzle_flash";
    protected ArrayList<ItemStack> gunAttachments;
    protected ArrayList<Modules.Attachment> configAttachments;
    protected ArrayList<String> hiddenBones = new ArrayList<>();
    protected BarrelItem barrelItem;
    protected WeaponConfig weaponConfig;
    protected ItemStack gunStack;
    private ItemDisplayContext transformType;

    public DynamicWeaponRenderer(GeoModel<Animator> model) {
        super(model);
    }

    @Override
    public void render(LivingEntity entity, ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                       SubmitNodeCollector collector, int packedLight) {
        this.transformType = transformType;
        var data = new WeaponData(stack, entity);
        this.weaponConfig = WeaponModifierHelper.getConfig(data);
        this.gunStack = stack;
        this.gunAttachments = WeaponStateHelper.getAttachmentItems(stack, entity.level().registryAccess());
        this.configAttachments = weaponConfig.getAttachmentConfigs(gunAttachments);
        this.currentEntity = entity;

        if (TransformUtils.isFirstPerson(transformType) && AimingHandler.isScoping(data))
            return;

        var barrelStack = WeaponStateHelper.getAttachmentItem(AttachmentType.BARREL, data);

        if(barrelStack.getItem() instanceof BarrelItem barrel) {
            this.barrelItem = barrel;
        }
        else this.barrelItem = null;

        prepareHiddenBones(transformType);

        poseStack.pushPose();
        {
            var offset = WeaponModifierHelper.getWeaponOffset(new WeaponData(stack, entity));
            poseStack.translate(offset.x / 16D, offset.y / 16D, offset.z / 16D);
            poseStack.translate(weaponX / 10d / 16D , weaponY / 10d / 16D, weaponZ / 10d / 16D);

            if(TransformUtils.isFirstPerson(transformType)){
                poseStack.translate(1.5 / 16D, -1.0 / 16D, 0.0 / 16D);
            }
            else if(TransformUtils.isThirdPerson(transformType)){
                poseStack.translate(0.0 / 16D, 3.5 / 16D, 2.5 / 16D);
            }
            else {
                var staticOffset = WeaponModifierHelper.getNonHandOffset(new WeaponData(stack, entity));
                poseStack.translate(staticOffset.x / 16D, staticOffset.y / 16D, staticOffset.z / 16D);
                poseStack.translate(0, -0.5 / 16D, 0 / 16d);
            }
            super.render(entity, stack, transformType, poseStack, collector, packedLight);
        }
        poseStack.popPose();
    }

    @Override
    protected void updateBone(RenderPassInfo<GeoRenderState> renderPassInfo, GeoBone bone, BoneSnapshots snapshots) {
        var boneName = bone.name();

        if (hiddenBones.contains(boneName))
            setHidden(snapshots, bone, true);

        if (boneName.equals(MUZZLE_FLASH) && barrelItem != null) {
            var snapshot = snapshots.get(bone);
            var length = barrelItem.getProperties().getLength();
            snapshot.setTranslateZ(snapshot.getTranslateZ() - (float) length);

            if (Ntgl.isDebugging()) {
                snapshot.setTranslateX(snapshot.getTranslateX() + (float) (muzzleFlashX / 10D));
                snapshot.setTranslateY(snapshot.getTranslateY() + (float) (muzzleFlashY / 10D));
                snapshot.setTranslateZ(snapshot.getTranslateZ() + (float) (muzzleFlashZ / 10D));
            }
        }

        if (boneName.startsWith(MUZZLE_FLASH) && !TransformUtils.isHandTransform(transformType))
            setHidden(snapshots, bone, true);

        super.updateBone(renderPassInfo, bone, snapshots);
    }

    @Override
    protected void addBoneRenders(RenderPassInfo<GeoRenderState> renderPassInfo) {
        super.addBoneRenders(renderPassInfo);

        if (!TransformUtils.isHandTransform(transformType)) return;

        var isFirstPerson = TransformUtils.isFirstPerson(transformType);
        var entity = this.getRenderEntity();
        if (entity == null) return;
        var entityId = entity.getId();

        for (var bone : renderPassInfo.model().boneLookup().get().values()) {
            if (!bone.name().startsWith(MUZZLE_FLASH)) continue;

            renderPassInfo.addPerBoneRender(bone, (pass, muzzleBone, collector) -> {
                var snapshot = muzzleBone.frameSnapshot;
                if (snapshot != null && snapshot.isHidden()) return;

                // the pose is already positioned at the pivot of the bone
                var mat = new Matrix4f(pass.poseStack().last().pose());

                if (isFirstPerson) {
                    MuzzleMatrixHelper.saveMuzzleMatrix(entityId, mat, true);
                    MuzzleMatrixHelper.lastMuzzleMatrix = mat;
                } else {
                    MuzzleMatrixHelper.saveMuzzleMatrix(entityId, mat, false);
                    MuzzleMatrixHelper.lastThirdPersonMuzzleMatrix = mat;
                }
            });
        }
    }

    protected void prepareHiddenBones(ItemDisplayContext transformType) {
        if(gunStack == null || gunStack.isEmpty()) return;

        hiddenBones.clear();
        var gunAttachments = this.weaponConfig.getModules().getAttachments();

        var visibleBones = new ArrayList<String>();
        var data = new WeaponData(gunStack, currentEntity);

        gunAttachments.forEach((type, typeAttachments) -> {
            var item = WeaponStateHelper.getAttachmentItem(type, data);

            for (var attachment : typeAttachments) {
                if (shouldRenderAttachment(attachment, item)) {
                    if (transformType != ItemDisplayContext.GUI) {
                        hiddenBones.addAll(attachment.getHidden());
                        visibleBones.add(attachment.getName());
                        visibleBones.addAll(attachment.getBones());
                    }
                } else {
                    hiddenBones.add(attachment.getName());
                    hiddenBones.addAll(attachment.getBones());
                }
            }
        });

        hiddenBones.removeAll(visibleBones);
    }

    protected boolean shouldRenderAttachment(Modules.Attachment attachment, ItemStack item) {
        if (transformType != ItemDisplayContext.GUI) {
            var itemId = BuiltInRegistries.ITEM.getKey(item.getItem());
            return !item.isEmpty() && attachment.getItemId().equals(itemId);
        }
        return false;
    }
}
