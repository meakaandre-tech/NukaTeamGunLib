package com.nukateam.ntgl.client.render.screen;

import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.Ntgl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Author: MrCrayfish
 */
public class CheckBox extends AbstractWidget {
    private static final Identifier GUI = Identifier.tryBuild(Ntgl.MOD_ID, "textures/gui/components.png");

    private boolean toggled = false;

    public CheckBox(int left, int top, Component title) {
        super(left, top, 8, 8, title);
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.getX(), this.getY(), 0, 0, 8, 8, 256, 256); // checkbox background
        if (this.toggled) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.getX(), this.getY() - 1, 8, 0, 9, 8, 256, 256); // the actual checkmark
        }
        graphics.text(Minecraft.getInstance().font, this.getMessage(), this.getX() + 12, this.getY(), 0xFFFFFFFF);
    }

    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        this.toggled = !this.toggled;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}