package com.nukateam.ntgl.client.handlers;

import com.nukateam.ntgl.client.util.handler.ClientReloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import com.nukateam.ntgl.platform.event.client.RenderHandEvent;
import com.nukateam.ntgl.platform.SubscribeEvent;


public class PlayerEvents {
    @SubscribeEvent()
    public static void onRenderHand(RenderHandEvent event) {
        var reloadHandler = ClientReloadHandler.get();
        var player = Minecraft.getInstance().player;

        var isReloadingLeft = reloadHandler.isReloadingLeft(player);
        var isRightArm = event.getHand() == InteractionHand.MAIN_HAND;

        var isReloadingRight = reloadHandler.isReloadingRight(player);
        var isLeftArm = event.getHand() == InteractionHand.OFF_HAND;

        if ((isReloadingLeft && isRightArm) || (isReloadingRight && isLeftArm)) {
            event.setCanceled(true);
        }
    }
}
