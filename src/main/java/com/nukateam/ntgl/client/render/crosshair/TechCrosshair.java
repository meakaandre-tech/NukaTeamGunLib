package com.nukateam.ntgl.client.render.crosshair;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Author: MrCrayfish
 */
public class TechCrosshair extends Crosshair {
    private static final Identifier TECH_CROSSHAIR = Identifier.tryBuild(Ntgl.MOD_ID, "textures/crosshair/tech.png");
    private static final Identifier DOT_CROSSHAIR = Identifier.tryBuild(Ntgl.MOD_ID, "textures/crosshair/dot.png");

    private float scale;
    private float prevScale;
    private float rotation;
    private float prevRotation;

    public TechCrosshair() {
        super(Identifier.tryBuild(Ntgl.MOD_ID, "tech"));
    }

    @Override
    public void tick() {
        this.prevRotation = this.rotation;
        this.prevScale = this.scale;
        this.rotation += 4;
        this.scale *= 0.75F;
    }

    @Override
    public void onGunFired() {
        this.scale = 1.5F;
    }

    @Override
    public void render(Minecraft mc, GuiGraphicsExtractor graphics, int windowWidth, int windowHeight, float partialTicks) {
        float alpha = 1.0F - (float) AimingHandler.get().getNormalisedAdsProgress();
        int size = 8;
        int color = ARGB.white(alpha);
        var pose = graphics.pose();

        pose.pushMatrix();
        {
            pose.translate((windowWidth - size) / 2F, (windowHeight - size) / 2F);
            graphics.blit(RenderPipelines.CROSSHAIR, DOT_CROSSHAIR, 0, 0, 0, 0, size, size, size, size, color);
        }
        pose.popMatrix();

        pose.pushMatrix();
        {
            pose.translate(windowWidth / 2F, windowHeight / 2F);
            float scale = 1F + Mth.lerp(partialTicks, this.prevScale, this.scale);
            pose.scale(scale, scale);
            pose.rotate(Mth.DEG_TO_RAD * Mth.lerp(partialTicks, this.prevRotation, this.rotation));
            pose.translate(-size / 2F, -size / 2F);
            graphics.blit(RenderPipelines.CROSSHAIR, TECH_CROSSHAIR, 0, 0, 0, 0, size, size, size, size, color);
        }
        pose.popMatrix();
    }
}
