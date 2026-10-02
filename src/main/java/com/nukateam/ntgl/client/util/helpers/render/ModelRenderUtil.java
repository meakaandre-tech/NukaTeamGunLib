package com.nukateam.ntgl.client.util.helpers.render;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

import static net.minecraft.world.item.ItemDisplayContext.*;

public class ModelRenderUtil {
    public static void scissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int) mc.getWindow().getGuiScale();
        GL11.glScissor(
                x * scale,
                mc.getWindow().getScreenHeight() - y * scale - height * scale,
                Math.max(0, width * scale), Math.max(0, height * scale));
    }

    private static final ItemStackRenderState TRANSFORM_STATE = new ItemStackRenderState();

    /**
     * The display transform the item model defines for the given context
     * (replacement for <code>BakedModel.getTransforms()</code>).
     */
    public static ItemTransform getTransform(ItemStack stack, ItemDisplayContext transformType, @Nullable LivingEntity entity) {
        var minecraft = Minecraft.getInstance();
        var level = entity != null ? entity.level() : minecraft.level;

        try {
            TRANSFORM_STATE.clear();
            minecraft.getItemModelResolver().updateForTopItem(TRANSFORM_STATE, stack, transformType, level, entity, 0);
            if (TRANSFORM_STATE.isEmpty()) return ItemTransform.NO_TRANSFORM;
            var transform = TRANSFORM_STATE.firstLayer().itemTransform;
            return transform != null ? transform : ItemTransform.NO_TRANSFORM;
        } catch (RuntimeException e) {
            return ItemTransform.NO_TRANSFORM;
        }
    }

    public static void applyTransformType(ItemStack stack, PoseStack poseStack, ItemDisplayContext transformType, @Nullable LivingEntity entity) {
        var leftHanded = transformType == FIRST_PERSON_LEFT_HAND || transformType == THIRD_PERSON_LEFT_HAND;
        getTransform(stack, transformType, entity).apply(leftHanded, poseStack.last());
        // 26.x: ItemTransform.apply also moves to the corner of the model (-0.5), which used to be a separate step
        // of the vanilla item renderer. The weapon renderers expect the pose without it, as on 1.21.
        poseStack.translate(0.5F, 0.5F, 0.5F);

        /* Flips the model and normals if left handed. */
        if (leftHanded) {
            var scale = new Matrix4f().scale(-1, 1, 1);
            var normal = new Matrix3f(scale);
            poseStack.last().pose().mul(scale);
            poseStack.last().normal().mul(normal);
        }
    }

    public static boolean isMouseWithin(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}