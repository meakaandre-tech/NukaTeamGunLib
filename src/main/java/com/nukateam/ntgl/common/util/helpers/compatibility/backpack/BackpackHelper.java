package com.nukateam.ntgl.common.util.helpers.compatibility.backpack;

import com.nukateam.ntgl.common.data.holders.AmmoHolder;
import com.nukateam.ntgl.common.util.helpers.context.AmmoContext;
import com.nukateam.ntgl.common.util.helpers.context.IAmmoContext;
import net.minecraft.world.entity.player.Player;

/**
 * Backpack mods supported on NeoForge (Backpacked, Sophisticated Backpacks, Traveler's Backpack,
 * Yyz's Backpack) have no Fabric 26.2 builds, so no backpack is searched for ammo here.
 */
public class BackpackHelper {
    public static IAmmoContext findAmmo(Player player, AmmoHolder id) {
        return AmmoContext.NONE;
    }

    public static IAmmoContext findMagazine(Player player, AmmoHolder id) {
        return AmmoContext.NONE;
    }
}
