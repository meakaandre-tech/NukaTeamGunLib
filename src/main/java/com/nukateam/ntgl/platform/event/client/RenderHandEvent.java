package com.nukateam.ntgl.platform.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.platform.Event;
import com.nukateam.ntgl.platform.ICancellableEvent;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Fired before a first-person hand (with its item) is submitted; cancel to skip the vanilla hand.
 * Stand-in for NeoForge's RenderHandEvent, raised from ItemInHandRendererMixin. The buffer source
 * of 1.21 is now the frame's SubmitNodeCollector.
 */
public class RenderHandEvent extends Event implements ICancellableEvent {
    private final InteractionHand hand;
    private final PoseStack poseStack;
    private final SubmitNodeCollector collector;
    private final int packedLight;
    private final float partialTick;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    private final ItemStack stack;

    public RenderHandEvent(InteractionHand hand, PoseStack poseStack, SubmitNodeCollector collector, int packedLight,
                           float partialTick, float interpolatedPitch, float swingProgress, float equipProgress, ItemStack stack) {
        this.hand = hand;
        this.poseStack = poseStack;
        this.collector = collector;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public SubmitNodeCollector getSubmitNodeCollector() {
        return collector;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public float getInterpolatedPitch() {
        return interpolatedPitch;
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    public float getEquipProgress() {
        return equipProgress;
    }

    public ItemStack getItemStack() {
        return stack;
    }
}
