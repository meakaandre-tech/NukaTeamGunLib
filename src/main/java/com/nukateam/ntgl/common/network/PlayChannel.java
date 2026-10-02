package com.nukateam.ntgl.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import com.nukateam.ntgl.platform.PlatformHelper;

import java.util.function.Supplier;

/**
 * Sends NTGL payloads through Fabric networking (replaces the NeoForge PacketDistributor wrapper).
 */
public class PlayChannel {
    public void sendToPlayer(Supplier<ServerPlayer> supplier, CustomPacketPayload message) {
        ServerPlayNetworking.send(supplier.get(), message);
    }

    public void sendToTrackingEntity(Supplier<Entity> supplier, CustomPacketPayload message) {
        for (ServerPlayer player : PlayerLookup.tracking(supplier.get())) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public void sendToTrackingBlockEntity(Supplier<BlockEntity> supplier, CustomPacketPayload message) {
        for (ServerPlayer player : PlayerLookup.tracking(supplier.get())) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public void sendToTrackingLocation(Supplier<LevelLocation> supplier, CustomPacketPayload message) {
        LevelLocation location = supplier.get();
        for (ServerPlayer player : PlayerLookup.tracking(location.level(), net.minecraft.core.BlockPos.containing(location.pos()))) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public void sendToTrackingChunk(Supplier<LevelChunk> supplier, CustomPacketPayload message) {
        LevelChunk chunk = supplier.get();
        for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) chunk.getLevel(), chunk.getPos())) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, CustomPacketPayload message) {
        LevelLocation location = supplier.get();
        for (ServerPlayer player : PlayerLookup.around(location.level(), location.pos(), location.range())) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public void sendToServer(CustomPacketPayload message) {
        ClientPlayNetworking.send(message);
    }

    public void sendToAll(CustomPacketPayload message) {
        var server = PlatformHelper.getServer();
        if (server == null) return;
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, message);
        }
    }
}
