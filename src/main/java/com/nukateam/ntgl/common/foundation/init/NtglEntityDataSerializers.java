package com.nukateam.ntgl.common.foundation.init;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.data.config.weapon.General;
import com.nukateam.ntgl.common.data.config.weapon.ProjectileConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class NtglEntityDataSerializers {
    

    public static final EntityDataSerializer<ProjectileConfig> PROJECTILE_CONFIG_SERIALIZER =
            EntityDataSerializer.forValueType(
                    StreamCodec.of(
                            NtglEntityDataSerializers::writeProjectile,
                            NtglEntityDataSerializers::readProjectile
                    )
            );

    public static final EntityDataSerializer<General> GENERAL_CONFIG_SERIALIZER =
            EntityDataSerializer.forValueType(
                    StreamCodec.of(
                            NtglEntityDataSerializers::writeGeneral,
                            NtglEntityDataSerializers::readGeneral
                    )
            );

    /** Vanilla 26.x has no CompoundTag serializer any more. */
    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG =
            EntityDataSerializer.forValueType(net.minecraft.network.codec.ByteBufCodecs.COMPOUND_TAG);

    public static final Supplier<EntityDataSerializer<ProjectileConfig>> PROJECTILE_CONFIG =
            () -> PROJECTILE_CONFIG_SERIALIZER;

    public static final Supplier<EntityDataSerializer<General>> GENERAL_CONFIG =
            () -> GENERAL_CONFIG_SERIALIZER;

    private static void writeProjectile(RegistryFriendlyByteBuf buf, ProjectileConfig config) {
        CompoundTag tag = config.serializeNBT(buf.registryAccess());
        buf.writeNbt(tag);
    }

    private static ProjectileConfig readProjectile(RegistryFriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();

        if(tag == null)
            return new ProjectileConfig();

        return ProjectileConfig.create(tag);
    }

    private static void writeGeneral(RegistryFriendlyByteBuf buf, General config) {
        CompoundTag tag = config.serializeNBT(buf.registryAccess());
        buf.writeNbt(tag);
    }

    private static General readGeneral(RegistryFriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();

        if(tag == null)
            return new General();

        return General.create(tag);
    }

    public static void register(){
        FabricEntityDataRegistry.register(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "projectile_config"), PROJECTILE_CONFIG_SERIALIZER);
        FabricEntityDataRegistry.register(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "general_config"), GENERAL_CONFIG_SERIALIZER);
        FabricEntityDataRegistry.register(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "compound_tag"), COMPOUND_TAG);
    }
}
