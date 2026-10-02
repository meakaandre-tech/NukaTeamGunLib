package com.nukateam.ntgl.client.render.crosshair;

import com.nukateam.ntgl.client.util.handler.AimingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/**
 * Author: MrCrayfish
 */
public class TexturedCrosshair extends Crosshair {
    private Identifier texture;
    private boolean blend;

    public TexturedCrosshair(Identifier id) {
        this(id, true);
    }

    public TexturedCrosshair(Identifier id, boolean blend) {
        super(id);
        this.texture = Identifier.tryBuild(id.getNamespace(), "textures/crosshair/" + id.getPath() + ".png");
        this.blend = blend;
    }

    @Override
    public void render(Minecraft mc, GuiGraphicsExtractor graphics, int windowWidth, int windowHeight, float partialTicks) {
        float alpha = 1.0F - (float) AimingHandler.get().getNormalisedAdsProgress();
        int size = 8;

        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate((windowWidth - size) / 2F, (windowHeight - size) / 2F);
        // the crosshair pipeline inverts the background like the vanilla crosshair does
        graphics.blit(this.blend ? RenderPipelines.CROSSHAIR : RenderPipelines.GUI_TEXTURED, this.texture,
                0, 0, 0, 0, size, size, size, size, ARGB.white(alpha));
        pose.popMatrix();
    }
}
