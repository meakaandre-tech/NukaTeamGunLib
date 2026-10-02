package com.nukateam.geo.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.nukateam.ntgl.client.registry.WeaponRegistry;
import com.nukateam.ntgl.client.util.RenderDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Renders a GeckoLib model for an item held by an entity, with one animator per (entity, display context).
 * <p>
 * GeckoLib 5 port notes: bones are immutable, so everything that used to be done in <code>renderRecursively</code>
 * is split in two hooks: {@link #updateBone} (hide / move bones for this frame through their snapshot) and
 * {@link #addBoneRenders} (extra geometry rendered at a bone).
 */
public class DynamicGeoItemRenderer<Animator extends ItemAnimator> extends GeoObjectRenderer<Animator, RenderContext, GeoRenderState> {
    public static final DataTicket<RenderContext> CONTEXT = DataTicket.create("ntgl_render_context", RenderContext.class);

    private final Map<Pair<LivingEntity, ItemDisplayContext>, ItemAnimator> animatorsByTransform = new HashMap<>();
    private BiFunction<ItemDisplayContext, DynamicGeoItemRenderer<?>, Animator> animatorFactory = null;
    protected ItemStack currentStack;
    protected ItemDisplayContext currentTransform;
    protected LivingEntity currentEntity;
    private LivingEntity buffEntity = null;

    public DynamicGeoItemRenderer(GeoModel<Animator> model) {
        super(model);
    }

    public DynamicGeoItemRenderer(GeoModel<Animator> model, BiFunction<ItemDisplayContext, DynamicGeoItemRenderer<?>, Animator> animatorFactory) {
        super(model);
        this.animatorFactory = animatorFactory;
    }

    public void render(LivingEntity entity, ItemStack stack, ItemDisplayContext transformType,
                       PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        this.currentStack = stack;
        this.currentTransform = transformType;
        this.currentEntity = entity;

        if (buffEntity != null) {
            currentEntity = buffEntity;
            buffEntity = null;
        }

        if (currentEntity == null || collector == null) return;

        var minecraft = Minecraft.getInstance();
        var partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        var cameraState = minecraft.levelRenderer.levelRenderState.cameraRenderState;
        var animator = getAnimator(currentEntity, transformType, stack);
        animator.setStack(stack);

        if (RenderDebug.ENABLED) RenderDebug.log("geo.render." + transformType, () -> stack.getItem() + " entity=" + currentEntity.getClass().getSimpleName()
                + " pose " + RenderDebug.pose(poseStack));

        performRenderPass(animator, new RenderContext(currentEntity, stack, transformType),
                poseStack, collector, cameraState, packedLight, partialTick);
    }

    @Override
    public void addRenderData(Animator animatable, RenderContext context, GeoRenderState renderState, float partialTick) {
        renderState.addGeckolibData(CONTEXT, context);
        renderState.addGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, context.transformType());
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        if (getGeoModel() instanceof AnimatableGeoModel model) {
            var animatable = model.getAnimatable(renderState);
            if (animatable != null)
                return model.getRenderType(animatable, texture);
        }
        return super.getRenderType(renderState, texture);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
        renderPassInfo.poseStack().translate(0.0F, 0.01F, 0.0F);
        super.adjustRenderPose(renderPassInfo);
        if (RenderDebug.ENABLED) RenderDebug.log("geo.adjusted." + currentTransform, () -> "missing=" + renderPassInfo.model().isMissingno()
                + " willRender=" + renderPassInfo.willRender() + " bones=" + renderPassInfo.model().boneLookup().get().size()
                + " pose " + RenderDebug.pose(renderPassInfo.poseStack()));
    }

    @Override
    public void preRenderPass(RenderPassInfo<GeoRenderState> renderPassInfo, SubmitNodeCollector collector) {
        super.preRenderPass(renderPassInfo, collector);
        addBoneRenders(renderPassInfo);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(renderPassInfo, snapshots);

        for (var bone : renderPassInfo.model().boneLookup().get().values())
            updateBone(renderPassInfo, bone, snapshots);
    }

    @Override
    public void submitRenderTasks(RenderPassInfo<GeoRenderState> renderPassInfo, OrderedSubmitNodeCollector collector, RenderType renderType) {
        // Bone updates are computed lazily by GeckoLib, possibly after this renderer was reused for another item.
        // Compute them now, while the per-render fields of the renderer are still valid.
        renderPassInfo.renderPosed(() -> {});
        super.submitRenderTasks(renderPassInfo, collector, renderType);
    }

    /**
     * Called once per bone and frame, after the animations were applied. Use <code>snapshots.get(bone)</code>
     * to hide or move the bone for this frame.
     */
    protected void updateBone(RenderPassInfo<GeoRenderState> renderPassInfo, GeoBone bone, BoneSnapshots snapshots) {}

    /**
     * Called before rendering; register extra per-bone geometry with
     * {@link RenderPassInfo#addPerBoneRender(GeoBone, com.geckolib.renderer.base.PerBoneRender)}.
     */
    protected void addBoneRenders(RenderPassInfo<GeoRenderState> renderPassInfo) {}

    protected static void setHidden(BoneSnapshots snapshots, GeoBone bone, boolean hidden) {
        snapshots.get(bone).skipRender(hidden).skipChildrenRender(hidden);
    }

    @SuppressWarnings("unchecked")
    public Animator getAnimator(LivingEntity entity, ItemDisplayContext transformType, ItemStack stack) {
        var key = Pair.of(entity, transformType);

        if (!animatorsByTransform.containsKey(key)) {
            if (animatorFactory == null) {
                animatorFactory = (BiFunction<ItemDisplayContext, DynamicGeoItemRenderer<?>, Animator>)
                        (Object) WeaponRegistry.getAnimator(stack.getItem());
            }
            var animator = animatorFactory.apply(transformType, this);
            animator.setStack(stack);
            animatorsByTransform.put(key, animator);
        }

        return (Animator) animatorsByTransform.get(key);
    }

    public LivingEntity getRenderEntity() {
        return currentEntity;
    }

    public ItemDisplayContext getTransformType() {
        return currentTransform;
    }

    public void setEntity(LivingEntity entity) {
        this.buffEntity = entity;
    }
}
