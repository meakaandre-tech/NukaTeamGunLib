package com.nukateam.ntgl.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nukateam.ntgl.client.model.IGlowingModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.texture.AutoGlowingTexture;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoRenderer;
import com.geckolib.renderer.layer.AutoGlowingGeoLayer;
import com.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.TorchBlock;

import static com.nukateam.ntgl.client.util.ClientDebug.*;

public class LayerBase <T extends GeoAnimatable> extends GeoRenderLayer<T> {
    public LayerBase(GeoRenderer<T> entityRenderer) {
        super(entityRenderer);
    }

    protected void renderLayer(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, MultiBufferSource bufferSource, float partialTick, int packedLight, Identifier texture) {
        var renderTypeNew = RenderType.eyes(texture);
//        var renderTypeNew = AutoGlowingTexture.getRenderType(texture);

        poseStack.pushPose();
        {
            poseStack.translate(-0.5, -0.5, -0.5);
            poseStack.translate(0, -16 / 100d / 16D, 0);
            poseStack.translate( X / 100d / 16d, Y / 100d / 16d, Z / 10d / 16d);
            this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable,
                    renderTypeNew, bufferSource.getBuffer(renderTypeNew),
                    partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                    0xFFFFFFFF);
        }
        poseStack.popPose();
    }
}
