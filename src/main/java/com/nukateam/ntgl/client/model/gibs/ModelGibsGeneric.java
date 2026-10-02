package com.nukateam.ntgl.client.model.gibs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Gibs made of the model parts of a vanilla style entity model.
 * (26.x: every entity model exposes its root part, so this also covers what ModelGibsAgeable did.)
 */
public class ModelGibsGeneric extends ModelGibs {
    private final List<ModelPart> gibs;

    public ModelGibsGeneric(Model<?> model) {
        gibs = model.root().getAllParts();
    }

    @Override
    public void render(Entity entity, int part, PoseStack poseStack, RenderType rendertype,
                       SubmitNodeCollector collector, int packedLight, int packedOverlay, int rgba) {
        if (part != 0 && part < gibs.size()) {
            var gib = gibs.get(part);
            collector.submitCustomGeometry(poseStack, rendertype, (pose, vertexConsumer) -> {
                var partPose = new PoseStack();
                partPose.last().set(pose);
                gib.render(partPose, vertexConsumer, packedLight, packedOverlay, rgba);
            });
        }
    }

    public int getNumGibs() {
        return gibs.size();
    }
}
