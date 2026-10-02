package com.nukateam.ntgl.client.util.helpers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Third person player animations were played through the optional PlayerAnimator mod.
 * It is not available for Fabric 26.2, so these hooks do nothing (the vanilla style arm poses still apply).
 */
public class PlayerAnimations {
    public static void playFireAnimation(Player player, InteractionHand hand) {}

    public static void playMeleeAnimation(Player player, InteractionHand hand) {}

    public static void playReloadAnimation(Player player, InteractionHand hand) {}
}
