package com.nukateam.ntgl.modules.datapack.handlers;

import com.nukateam.ntgl.common.network.PacketHandler;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageUpdateAmmo;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageUpdateAttachments;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageUpdateProjectiles;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageUpdateWeapons;
import com.nukateam.ntgl.modules.datapack.managers.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;

public class NetworkManagerHandler {
    /** Registers the datapack reload listeners and the sync of their content to players. */
    public static void register() {
        NetworkWeaponManager.register();
        NetworkAmmoManager.register();
        NetworkProjectileManager.register();
        NetworkAttachmentManager.register();

        // fires for every player on join and again after /reload
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> sync(player));
    }

    private static void sync(ServerPlayer player) {
        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageUpdateWeapons());
        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageUpdateAmmo());
        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageUpdateProjectiles());
        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageUpdateAttachments());
    }
}
