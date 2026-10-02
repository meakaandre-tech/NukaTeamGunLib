package com.nukateam.ntgl.client.util.helpers.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Figures {
    public static void drawBar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, float percent, int color){
//        var color = percent < 0.25 ? 0xFFFF5555 : 0xFFFFFFFF;
        var value = (int)(width * percent);
        drawFrame(graphics, x, y, width, height, color);
        graphics.fill(x, y, x + value, y + height, color);
    }

    public static void drawFrame(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color); //TOP
        graphics.fill(x, y + height, x + width, y + height + 1, color); //BOTTOM
        graphics.fill(x, y, x + 1, y + height, color); //LEFT
        graphics.fill(x + width - 1, y, x + width, y + height, color); //RIGHT
    }

    public static void drawLine(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        drawLine(graphics, x, y, x + width, y + height, 0xFFFFFFFF);
    }

    public static void drawLine(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    /**
     * Global alpha multiplier for the round figures (replacement for RenderSystem.setShaderColor,
     * which GUI rendering no longer honours).
     */
    public static float alphaMultiplier = 1.0F;

    /** Converts the RGBA colors used by the round figures to the ARGB format of the GUI pipeline. */
    private static int toArgb(int rgba) {
        int r = rgba >> 24 & 255;
        int g = rgba >> 16 & 255;
        int b = rgba >> 8 & 255;
        int a = (int) ((rgba & 255) * Mth.clamp(alphaMultiplier, 0F, 1F));
        return a << 24 | r << 16 | g << 8 | b;
    }

    /** Ring part of a circle segment plus the two triangles closing it towards the centre. */
    public static void drawBorder(GuiGraphicsExtractor guiGraphics, float centerX, float centerY,
                                  float radius, float startAngle, float sweepAngle,
                                  int color, float thickness) {
        if (radius <= 0 || sweepAngle <= 0 || thickness <= 0) return;

        float startRad = (float) Math.toRadians(startAngle - 90);
        float sweepRad = (float) Math.toRadians(sweepAngle);
        int segments = Math.max(8, (int) (sweepAngle / 2));
        float innerRadius = Math.max(0, radius - thickness);

        var quads = new QuadBuilder();
        for (int i = 0; i < segments; i++) {
            float angle = startRad + sweepRad * i / segments;
            float next = startRad + sweepRad * (i + 1) / segments;

            quads.quad(centerX + Mth.cos(angle) * radius, centerY + Mth.sin(angle) * radius,
                    centerX + Mth.cos(angle) * innerRadius, centerY + Mth.sin(angle) * innerRadius,
                    centerX + Mth.cos(next) * innerRadius, centerY + Mth.sin(next) * innerRadius,
                    centerX + Mth.cos(next) * radius, centerY + Mth.sin(next) * radius);
        }

        float endRad = startRad + sweepRad;
        quads.triangle(centerX, centerY,
                centerX + Mth.cos(startRad) * radius, centerY + Mth.sin(startRad) * radius,
                centerX + Mth.cos(startRad) * innerRadius, centerY + Mth.sin(startRad) * innerRadius);
        quads.triangle(centerX, centerY,
                centerX + Mth.cos(endRad) * innerRadius, centerY + Mth.sin(endRad) * innerRadius,
                centerX + Mth.cos(endRad) * radius, centerY + Mth.sin(endRad) * radius);

        quads.submit(guiGraphics, toArgb(color));
    }

    /** Outline of a circle segment: the arc and the two lines to the centre. */
    public static void drawOutline(GuiGraphicsExtractor guiGraphics, float centerX, float centerY,
                                   float radius, float startAngle, float sweepAngle,
                                   int color, float lineWidth) {
        if (radius <= 0 || sweepAngle <= 0) return;

        float startRad = (float) Math.toRadians(startAngle - 90);
        float sweepRad = (float) Math.toRadians(sweepAngle);
        int segments = Math.max(8, (int) (radius * Mth.PI / 4));
        float width = Math.max(lineWidth, 0.5F);

        var quads = new QuadBuilder();
        for (int i = 0; i < segments; i++) {
            float angle = startRad + sweepRad * i / segments;
            float next = startRad + sweepRad * (i + 1) / segments;
            quads.line(centerX + Mth.cos(angle) * radius, centerY + Mth.sin(angle) * radius,
                    centerX + Mth.cos(next) * radius, centerY + Mth.sin(next) * radius, width);
        }

        float endRad = startRad + sweepRad;
        quads.line(centerX, centerY, centerX + Mth.cos(startRad) * radius, centerY + Mth.sin(startRad) * radius, width);
        quads.line(centerX, centerY, centerX + Mth.cos(endRad) * radius, centerY + Mth.sin(endRad) * radius, width);

        quads.submit(guiGraphics, toArgb(color));
    }

    /** Filled circle segment (pie slice). */
    public static void drawSegment(GuiGraphicsExtractor guiGraphics, float centerX, float centerY,
                                   float radius, float startAngle, float sweepAngle, int color) {
        if (radius <= 0 || sweepAngle <= 0) return;

        float startRad = (float) Math.toRadians(startAngle - 90);
        float sweepRad = (float) Math.toRadians(sweepAngle);
        int segments = Math.max(8, (int) (radius * Mth.PI / 4));

        var quads = new QuadBuilder();
        for (int i = 0; i < segments; i++) {
            float angle = startRad + sweepRad * i / segments;
            float next = startRad + sweepRad * (i + 1) / segments;
            quads.triangle(centerX, centerY,
                    centerX + Mth.cos(next) * radius, centerY + Mth.sin(next) * radius,
                    centerX + Mth.cos(angle) * radius, centerY + Mth.sin(angle) * radius);
        }

        quads.submit(guiGraphics, toArgb(color));
    }

    /** Collects free-form quads and hands them to the GUI render state (26.x has no immediate mode drawing). */
    private static class QuadBuilder {
        private final List<Float> points = new ArrayList<>();

        void quad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
            // keep the winding of vanilla GUI quads, in case the pipeline culls back faces
            float area = (x0 * y1 - x1 * y0) + (x1 * y2 - x2 * y1) + (x2 * y3 - x3 * y2) + (x3 * y0 - x0 * y3);

            points.add(x0); points.add(y0);
            if (area > 0) {
                points.add(x3); points.add(y3);
                points.add(x2); points.add(y2);
                points.add(x1); points.add(y1);
            } else {
                points.add(x1); points.add(y1);
                points.add(x2); points.add(y2);
                points.add(x3); points.add(y3);
            }
        }

        void triangle(float x0, float y0, float x1, float y1, float x2, float y2) {
            quad(x0, y0, x1, y1, x2, y2, x2, y2);
        }

        void line(float x0, float y0, float x1, float y1, float width) {
            float dx = x1 - x0;
            float dy = y1 - y0;
            float length = Mth.sqrt(dx * dx + dy * dy);
            if (length <= 0) return;
            float nx = -dy / length * width / 2F;
            float ny = dx / length * width / 2F;
            quad(x0 + nx, y0 + ny, x0 - nx, y0 - ny, x1 - nx, y1 - ny, x1 + nx, y1 + ny);
        }

        void submit(GuiGraphicsExtractor graphics, int argb) {
            if (points.isEmpty() || (argb >>> 24) == 0) return;

            var pose = new Matrix3x2f(graphics.pose());
            var vertices = new float[points.size()];
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
            var point = new Vector2f();

            for (int i = 0; i < vertices.length; i += 2) {
                vertices[i] = points.get(i);
                vertices[i + 1] = points.get(i + 1);
                pose.transformPosition(vertices[i], vertices[i + 1], point);
                minX = Math.min(minX, point.x);
                minY = Math.min(minY, point.y);
                maxX = Math.max(maxX, point.x);
                maxY = Math.max(maxY, point.y);
            }

            var bounds = new ScreenRectangle(Mth.floor(minX), Mth.floor(minY),
                    Mth.ceil(maxX - minX) + 1, Mth.ceil(maxY - minY) + 1);
            graphics.guiRenderState.addGuiElement(new QuadsRenderState(pose, vertices, argb, bounds));
        }
    }

    private record QuadsRenderState(Matrix3x2f pose, float[] vertices, int color, ScreenRectangle bounds)
            implements GuiElementRenderState {
        @Override
        public void buildVertices(VertexConsumer consumer) {
            for (int i = 0; i < vertices.length; i += 2)
                consumer.addVertexWith2DPose(pose, vertices[i], vertices[i + 1]).setColor(color);
        }

        @Override
        public RenderPipeline pipeline() {
            return RenderPipelines.GUI;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        @Override
        public ScreenRectangle scissorArea() {
            return null;
        }
    }
}
