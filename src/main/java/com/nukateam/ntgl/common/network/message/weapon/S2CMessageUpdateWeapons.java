package com.nukateam.ntgl.common.network.message.weapon;

import com.nukateam.ntgl.platform.IPayloadContext;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.handlers.ClientPlayHandler;
import com.nukateam.ntgl.common.data.config.weapon.WeaponConfig;
import com.nukateam.ntgl.modules.datapack.managers.NetworkWeaponManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.Validate;

public class S2CMessageUpdateWeapons implements CustomPacketPayload {
    public static final Type<S2CMessageUpdateWeapons> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "s2c_message_update_weapons"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMessageUpdateWeapons> CODEC = StreamCodec.of(
            (buffer, message) -> encode(message, buffer),
            buffer -> decode(buffer));

    private ImmutableMap<Identifier, WeaponConfig> registeredGuns;

    public S2CMessageUpdateWeapons() {}

    public static void encode(S2CMessageUpdateWeapons message, FriendlyByteBuf buffer) {
        Validate.notNull(NetworkWeaponManager.get());
        NetworkWeaponManager.get().writeRegisteredGuns(buffer);
    }

    public static S2CMessageUpdateWeapons decode(FriendlyByteBuf buffer) {
        var message = new S2CMessageUpdateWeapons();
        message.registeredGuns = NetworkWeaponManager.readRegisteredWeapons(buffer);
        return message;
    }

    public static void handle(S2CMessageUpdateWeapons message, IPayloadContext supplier) {
        supplier.enqueueWork((() -> ClientPlayHandler.handleUpdateWeapons(message)));
    }

    public ImmutableMap<Identifier, WeaponConfig> getRegisteredGuns() {
        return this.registeredGuns;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
