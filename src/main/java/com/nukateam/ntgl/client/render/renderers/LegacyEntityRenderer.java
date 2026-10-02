package com.nukateam.ntgl.client.render.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Base for the entity renderers of the mod on 26.x, where renderers work on an extracted render state
 * instead of the entity. The state simply carries the entity, so subclasses keep the
 * <code>render(entity, yaw, partialTicks, poseStack, ..., light)</code> shape they had on 1.21.
 */
public abstract class LegacyEntityRenderer<T extends Entity> extends EntityRenderer<T, LegacyEntityRenderer.State<T>> {
    protected final ItemModelResolver itemModelResolver;

    public static class State<T extends Entity> extends EntityRenderState {
        public T entity;
        public float entityYaw;
        public float partialTick;
    }

    protected LegacyEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public State<T> createRenderState() {
        return new State<>();
    }

    @Override
    public void extractRenderState(T entity, State<T> state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
        state.partialTick = partialTick;
        state.entityYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
    }

    @Override
    public void submit(State<T> state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.entity != null) {
            poseStack.pushPose();
            render(state.entity, state.entityYaw, state.partialTick, poseStack, collector, cameraState, state.lightCoords);
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, cameraState);
    }

    public abstract void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                                SubmitNodeCollector collector, CameraRenderState cameraState, int light);

    /** Replacement for <code>ItemRenderer.renderStatic</code>. */
    protected ItemStackRenderState renderItem(ItemStack stack, ItemDisplayContext displayContext, int light, int overlay,
                                              PoseStack poseStack, SubmitNodeCollector collector, Entity entity) {
        var itemState = resolveItem(stack, displayContext, entity);
        itemState.submit(poseStack, collector, light, overlay, 0);
        return itemState;
    }

    protected ItemStackRenderState resolveItem(ItemStack stack, ItemDisplayContext displayContext, Entity entity) {
        var itemState = new ItemStackRenderState();
        this.itemModelResolver.updateForNonLiving(itemState, stack, displayContext, entity);
        return itemState;
    }
}
