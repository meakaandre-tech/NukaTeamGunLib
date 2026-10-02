package com.nukateam.ntgl.client.model.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.Entity;

public abstract class ModelGibs {
    public abstract void render(Entity entity, int part, PoseStack poseStack, RenderType rendertype,
                                SubmitNodeCollector collector, int packedLight, int packedOverlay, int colour);

    public abstract int getNumGibs();
}
