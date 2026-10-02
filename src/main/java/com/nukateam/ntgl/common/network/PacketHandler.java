package com.nukateam.ntgl.common.network;

import com.nukateam.ntgl.common.data.holders.AnimationType;
import com.nukateam.ntgl.common.network.message.weapon.*;
import com.nukateam.ntgl.modules.data.message.S2CMessageUpdateEntityData;
import com.nukateam.ntgl.platform.IPayloadContext;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public class PacketHandler {
    /** Upper bound for the datapack sync payloads (all weapon/ammo/projectile/attachment configs). */
    private static final int LARGE_PAYLOAD_SIZE = 32 * 1024 * 1024;
    private static final PlayChannel playChannel = new PlayChannel();

    public static PlayChannel getPlayChannel() {
        return playChannel;
    }

    /** Payload types and server receivers; called from the mod initializer on both sides. */
    public static void register() {
        var c2s = PayloadTypeRegistry.serverboundPlay();
        var s2c = PayloadTypeRegistry.clientboundPlay();

        c2s.register(C2SMessageAim.TYPE, C2SMessageAim.CODEC);
        c2s.register(C2SMessageReload.TYPE, C2SMessageReload.CODEC);
        c2s.register(C2SMessageShoot.TYPE, C2SMessageShoot.CODEC);
        c2s.register(C2SMessageUnload.TYPE, C2SMessageUnload.CODEC);
        c2s.register(C2SMessageReloadStop.TYPE, C2SMessageReloadStop.CODEC);
        c2s.register(C2SMessageCraft.TYPE, C2SMessageCraft.CODEC);
        c2s.register(C2SMessageAttachments.TYPE, C2SMessageAttachments.CODEC);
        c2s.register(C2SMessageChangeAmmo.TYPE, C2SMessageChangeAmmo.CODEC);
        c2s.register(C2SMessageShooting.TYPE, C2SMessageShooting.CODEC);
        c2s.register(C2SMessagePreFireSound.TYPE, C2SMessagePreFireSound.CODEC);
        c2s.register(C2SMessageHandAction.TYPE, C2SMessageHandAction.CODEC);
        c2s.register(C2SMessageMeleeAttack.TYPE, C2SMessageMeleeAttack.CODEC);
        c2s.register(C2SMessageGrenade.TYPE, C2SMessageGrenade.CODEC);

        s2c.register(S2CMessagePlayerAnimation.TYPE, S2CMessagePlayerAnimation.CODEC);
        s2c.register(S2CMessageEntityDeath.TYPE, S2CMessageEntityDeath.CODEC);
        s2c.register(S2CMessageEntityDeathFx.TYPE, S2CMessageEntityDeathFx.CODEC);
        s2c.register(S2CMessageStunGrenade.TYPE, S2CMessageStunGrenade.CODEC);
        s2c.registerLarge(S2CMessageUpdateWeapons.TYPE, S2CMessageUpdateWeapons.CODEC, LARGE_PAYLOAD_SIZE);
        s2c.registerLarge(S2CMessageUpdateAmmo.TYPE, S2CMessageUpdateAmmo.CODEC, LARGE_PAYLOAD_SIZE);
        s2c.registerLarge(S2CMessageUpdateProjectiles.TYPE, S2CMessageUpdateProjectiles.CODEC, LARGE_PAYLOAD_SIZE);
        s2c.registerLarge(S2CMessageUpdateAttachments.TYPE, S2CMessageUpdateAttachments.CODEC, LARGE_PAYLOAD_SIZE);
        s2c.register(S2CMessageBlood.TYPE, S2CMessageBlood.CODEC);
        s2c.register(S2CMessageGunSound.TYPE, S2CMessageGunSound.CODEC);
        s2c.register(S2CMessageProjectileHitBlock.TYPE, S2CMessageProjectileHitBlock.CODEC);
        s2c.register(S2CMessageProjectileHitEntity.TYPE, S2CMessageProjectileHitEntity.CODEC);
        s2c.register(S2CMessageProjectileHitFluid.TYPE, S2CMessageProjectileHitFluid.CODEC);
        s2c.register(S2CMessageProjectileExplosion.TYPE, S2CMessageProjectileExplosion.CODEC);
        s2c.register(S2CMessageUpdateEntityData.TYPE, S2CMessageUpdateEntityData.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(C2SMessageAim.TYPE, (packet, ctx) -> C2SMessageAim.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageReload.TYPE, (packet, ctx) -> C2SMessageReload.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageShoot.TYPE, (packet, ctx) -> C2SMessageShoot.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageUnload.TYPE, (packet, ctx) -> C2SMessageUnload.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageReloadStop.TYPE, (packet, ctx) -> C2SMessageReloadStop.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageCraft.TYPE, (packet, ctx) -> C2SMessageCraft.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageAttachments.TYPE, (packet, ctx) -> C2SMessageAttachments.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageChangeAmmo.TYPE, (packet, ctx) -> C2SMessageChangeAmmo.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageShooting.TYPE, (packet, ctx) -> C2SMessageShooting.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessagePreFireSound.TYPE, (packet, ctx) -> C2SMessagePreFireSound.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageHandAction.TYPE, (packet, ctx) -> C2SMessageHandAction.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageMeleeAttack.TYPE, (packet, ctx) -> C2SMessageMeleeAttack.handle(packet, server(ctx)));
        ServerPlayNetworking.registerGlobalReceiver(C2SMessageGrenade.TYPE, (packet, ctx) -> C2SMessageGrenade.handle(packet, server(ctx)));
    }

    private static IPayloadContext server(ServerPlayNetworking.Context ctx) {
        return new IPayloadContext(ctx.player(), ctx.server()::execute);
    }

    public static void sendAnimation(LivingEntity entity, InteractionHand hand, AnimationType animation) {
        var levelLoc = LevelLocation.create((ServerLevel) entity.level(), entity.blockPosition());
        getPlayChannel().sendToNearbyPlayers(() -> levelLoc,
                new S2CMessagePlayerAnimation(entity.getId(), animation, hand));
    }
}
