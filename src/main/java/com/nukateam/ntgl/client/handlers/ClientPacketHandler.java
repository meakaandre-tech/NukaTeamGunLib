package com.nukateam.ntgl.client.handlers;

import com.nukateam.ntgl.common.network.message.weapon.*;
import com.nukateam.ntgl.modules.data.message.S2CMessageUpdateEntityData;
import com.nukateam.ntgl.platform.IPayloadContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client half of the packet registration.
 */
public class ClientPacketHandler {
    /** Client receivers; called from the client initializer. */
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(S2CMessagePlayerAnimation.TYPE, (packet, ctx) -> S2CMessagePlayerAnimation.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageEntityDeath.TYPE, (packet, ctx) -> S2CMessageEntityDeath.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageEntityDeathFx.TYPE, (packet, ctx) -> S2CMessageEntityDeathFx.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageStunGrenade.TYPE, (packet, ctx) -> S2CMessageStunGrenade.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageUpdateWeapons.TYPE, (packet, ctx) -> S2CMessageUpdateWeapons.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageUpdateAmmo.TYPE, (packet, ctx) -> S2CMessageUpdateAmmo.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageUpdateProjectiles.TYPE, (packet, ctx) -> S2CMessageUpdateProjectiles.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageUpdateRecipes.TYPE, (packet, ctx) -> S2CMessageUpdateRecipes.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageUpdateAttachments.TYPE, (packet, ctx) -> S2CMessageUpdateAttachments.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageBlood.TYPE, (packet, ctx) -> S2CMessageBlood.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageGunSound.TYPE, (packet, ctx) -> S2CMessageGunSound.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageProjectileHitBlock.TYPE, (packet, ctx) -> S2CMessageProjectileHitBlock.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageProjectileHitEntity.TYPE, (packet, ctx) -> S2CMessageProjectileHitEntity.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageProjectileHitFluid.TYPE, (packet, ctx) -> S2CMessageProjectileHitFluid.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageProjectileExplosion.TYPE, (packet, ctx) -> S2CMessageProjectileExplosion.handle(packet, client(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(S2CMessageUpdateEntityData.TYPE, (packet, ctx) -> S2CMessageUpdateEntityData.handle(packet, client(ctx)));
    }

    private static IPayloadContext client(ClientPlayNetworking.Context ctx) {
        return new IPayloadContext(ctx.player(), ctx.client()::execute);
    }
}
