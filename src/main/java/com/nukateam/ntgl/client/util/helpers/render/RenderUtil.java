package com.nukateam.ntgl.client.util.helpers.render;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.LightCoordsUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nukateam.ntgl.common.util.data.Rgba;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderUtil {
    public static final float BEAM_ALPHA = 0.7F;

    public static void renderBeam(PoseStack poseStack, SubmitNodeCollector collector, Identifier pBeamLocation,
                                  float pPartialTick, float pTextureScale, long gameTime, float pYOffset, float pHeight,
                                  Rgba colors, float pBeamRadius, float pGlowRadius) {
        var maxY = pYOffset + pHeight;
        float f = (float) Math.floorMod(gameTime, 40) + pPartialTick;
        float f1 = pHeight < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
        var minX = -pGlowRadius;
        var maxX = -pGlowRadius;
        var minZ = -pGlowRadius;
        var maxZ = -pBeamRadius;
        var v = -1.0F + f2;
        var u = pHeight * pTextureScale * (BEAM_ALPHA / pBeamRadius) + v;

        final float beamMaxZ = maxZ;
        final float beamU = u;
        final float beamV = v;
        final Rgba beamColor = new Rgba(colors.r(), colors.g(), colors.b(), 1.0F);
        final Rgba glowColor = new Rgba(colors.r(), colors.g(), colors.b(), BEAM_ALPHA);

        collector.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(pBeamLocation, false), (pose, vertexConsumer) ->
                RenderUtil.renderPart(pose, vertexConsumer, beamColor,
                        pYOffset, maxY,
                        0.0F, pBeamRadius,
                        pBeamRadius, 0.0F,
                        beamMaxZ, 0.0F,
                        0.0F, -pBeamRadius,
                        beamU, beamV));

        final float glowMaxZ = -pGlowRadius;
        final float glowV = -1.0F + f2;
        final float glowU = pHeight * pTextureScale + glowV;

        collector.submitCustomGeometry(poseStack, RenderTypes.beaconBeam(pBeamLocation, true), (pose, vertexConsumer) ->
                RenderUtil.renderPart(pose, vertexConsumer, glowColor,
                        pYOffset, maxY, minX, maxX, pGlowRadius, minZ, glowMaxZ,
                        pGlowRadius, pGlowRadius, pGlowRadius, glowU, glowV));
    }

    public static void renderPart(PoseStack.Pose pose, VertexConsumer consumer,
                                   Rgba colors,
                                   float pMinY, float pMaxY,
                                   float minX, float maxX,
                                   float minZ, float maxZ,
                                   float pX2, float pZ2,
                                   float pX3, float pZ3,
                                   float u, float v) {

        renderQuad(pose, consumer, colors, pMinY, pMaxY, minX, maxX, minZ, maxZ, u, v);
        renderQuad(pose, consumer, colors, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, u, v);
        renderQuad(pose, consumer, colors, pMinY, pMaxY, minZ, maxZ, pX3, pZ3, u, v);
        renderQuad(pose, consumer, colors, pMinY, pMaxY, pX2, pZ2, minX, maxX, u, v);
    }

    public static void renderQuad(PoseStack.Pose pose, VertexConsumer consumer, Rgba colors,
                                   float pMinY, float pMaxY,
                                   float pMinX, float pMinZ,
                                   float pMaxX, float pMaxZ,

                                   float pMinV, float pMaxV) {
        addVertex(pose, consumer, colors, pMaxY, pMinX, pMinZ, 1, pMinV);
        addVertex(pose, consumer, colors, pMinY, pMinX, pMinZ, 1, pMaxV);
        addVertex(pose, consumer, colors, pMinY, pMaxX, pMaxZ, 0, pMaxV);
        addVertex(pose, consumer, colors, pMaxY, pMaxX, pMaxZ, 0, pMinV);
    }

    public static void addVertex(PoseStack.Pose pose, VertexConsumer consumer,
                                 Rgba colors, float pY, float pX, float pZ, float pU, float pV) {
        float red = colors.r();
        float green = colors.g();
        float blue = colors.b();
        float alpha = colors.a();

        consumer.addVertex(pose, pX, pY, pZ)
                .setColor(red, green, blue, alpha)
                .setUv(pU, pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}
