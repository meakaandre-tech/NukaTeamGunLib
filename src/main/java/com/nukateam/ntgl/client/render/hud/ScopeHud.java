package com.nukateam.ntgl.client.render.hud;

import net.minecraft.client.renderer.RenderPipelines;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.data.holders.AttachmentType;
import com.nukateam.ntgl.common.util.util.WeaponStateHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;


public class ScopeHud{
    private static float scopeScale;

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker partialTick) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var mainWindow = minecraft.getWindow();
        int width  = mainWindow.getGuiScaledWidth ();
        int height = mainWindow.getGuiScaledHeight();

        if (player == null) return;
        var gun = player.getMainHandItem();
        var frameTime = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        scopeScale = Mth.lerp(0.5F * frameTime, scopeScale, 1.125F);
        var data = new WeaponData(gun, player);

        if (AimingHandler.isScoping(data)) {
            var attachment = WeaponStateHelper.getAttachmentItem(AttachmentType.SCOPE, data);
            if (!attachment.isEmpty()) {
                var scope = WeaponStateHelper.getScopeItem(data);
                var overlay = scope.getProperties().getOverlay();
                renderScope(graphics, width, height, overlay);
            }
        } else {
            scopeScale = 0.5F;
        }
    }

    private static void renderScope(GuiGraphicsExtractor graphics, int width, int height, Identifier overlay) {
        var f = (float) Math.min(width, height);
        var f1 = Math.min((float) width / f, (float) height / f) * scopeScale;
        int i = Mth.floor(f * f1);
        int j = Mth.floor(f * f1);
        int k = (width - i) / 2;
        int l = (height - j) / 2;
        int i1 = k + i;
        int j1 = l + j;
        graphics.blit(RenderPipelines.GUI_TEXTURED, overlay, k, l, 0.0F, 0.0F, i, j, i, j);
        graphics.fill(0, j1, width, height, -16777216);
        graphics.fill(0, 0, width, l, -16777216);
        graphics.fill(0, l, k, j1, -16777216);
        graphics.fill(i1, l, width, j1, -16777216);
    }
}
