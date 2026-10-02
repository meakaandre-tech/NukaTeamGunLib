package com.nukateam.ntgl.common.network.message.weapon;

import com.nukateam.ntgl.platform.IPayloadContext;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.foundation.init.ModSyncedDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class C2SMessageAim implements CustomPacketPayload {
    public static final Type<C2SMessageAim> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "c2s_message_aim"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SMessageAim> CODEC = StreamCodec.of(
            (buffer, message) -> encode(message, buffer),
            buffer -> decode(buffer));

    private boolean aiming;

    public C2SMessageAim() {
    }

    public C2SMessageAim(boolean aiming) {
        this.aiming = aiming;
    }

    public static void encode(C2SMessageAim message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.aiming);
    }

    public static C2SMessageAim decode(FriendlyByteBuf buffer) {
        return new C2SMessageAim(buffer.readBoolean());
    }

    public static void handle(C2SMessageAim message, IPayloadContext supplier) {
        supplier.enqueueWork((() ->
        {
            var player = supplier.player();
            if (player != null && !player.isSpectator()) {
                ModSyncedDataKeys.AIMING.setValue(player, message.aiming);
            }
        }));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
