package com.nukateam.ntgl.common.network.message.weapon;

import com.nukateam.ntgl.platform.IPayloadContext;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.handlers.ClientPlayHandler;
import com.nukateam.ntgl.common.data.config.weapon.ExplosionConfig;
import com.nukateam.ntgl.common.util.util.NbtUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class S2CMessageProjectileExplosion implements CustomPacketPayload {
    public static final Type<S2CMessageProjectileExplosion> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "s2c_message_projectile_explosion"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMessageProjectileExplosion> CODEC = StreamCodec.of(
            (buffer, message) -> encode(message, buffer),
            buffer -> decode(buffer));
    private Vec3 position;
    private Vec3 knockback;
    private ExplosionConfig config;
    private List<BlockPos> toBlow;

    public S2CMessageProjectileExplosion() {}

    public S2CMessageProjectileExplosion(Vec3 position, Vec3 knockback, ExplosionConfig config, List<BlockPos> toBlow) {
        this.position = position;
        this.knockback = Objects.requireNonNullElseGet(knockback, () -> new Vec3(0, 0, 0));
        if(knockback == null){
            this.knockback = Vec3.ZERO;
        }
        this.config = config;
        this.toBlow = toBlow;
    }

    public static void encode(S2CMessageProjectileExplosion message, FriendlyByteBuf buffer) {
        NbtUtils.writeVec3(buffer, message.position);
        NbtUtils.writeVec3(buffer, message.knockback);
        buffer.writeNbt(message.config.serializeNBT(null));
        // 26.3: FriendlyByteBuf lost writeCollection/readList; same wire format (count, then offsets)
        buffer.writeVarInt(message.toBlow.size());
        for (var blockPos : message.toBlow) {
            buffer.writeByte(blockPos.getX() - Mth.floor(message.position.x));
            buffer.writeByte(blockPos.getY() - Mth.floor(message.position.y));
            buffer.writeByte(blockPos.getZ() - Mth.floor(message.position.z));
        }
    }

    public static S2CMessageProjectileExplosion decode(FriendlyByteBuf buffer) {
        var position = NbtUtils.readVec3(buffer);
        var knockback = NbtUtils.readVec3(buffer);
        var config = ExplosionConfig.create(buffer.readNbt());
        int x = Mth.floor(position.x);
        int y = Mth.floor(position.y);
        int z = Mth.floor(position.z);
        int count = buffer.readVarInt();
        var toBlow = new java.util.ArrayList<BlockPos>(Math.min(count, 4096));
        for (int i = 0; i < count; i++) {
            int l  = buffer.readByte() + x;
            int i1 = buffer.readByte() + y;
            int j1 = buffer.readByte() + z;
            toBlow.add(new BlockPos(l, i1, j1));
        }

        return new S2CMessageProjectileExplosion(position, knockback, config, toBlow);
    }

    public static void handle(S2CMessageProjectileExplosion message, IPayloadContext supplier) {
        supplier.enqueueWork((() -> ClientPlayHandler.handleMessageExplosion(message)));
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getKnockback() {
        return knockback;
    }

    public ExplosionConfig getConfig() {
        return config;
    }

    public List<BlockPos> getToBlow() {
        return toBlow;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
