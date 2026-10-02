package com.nukateam.ntgl.common.foundation.init;

import com.mojang.serialization.Codec;
import com.nukateam.ntgl.Ntgl;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import com.nukateam.ntgl.platform.DeferredHolder;
import com.nukateam.ntgl.platform.DeferredRegister;

import javax.annotation.Nullable;

public class NtglComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTER =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ntgl.MOD_ID);

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerComponentType(String name, java.util.function.UnaryOperator<DataComponentType.Builder<T>> builder) {
        return REGISTER.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> CHASSIS_COMPONENT =
            registerComponentType(
                    "chassis_component",
                    builder -> builder
                            .persistent(CompoundTag.CODEC)
                            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> WEAPON_COMPONENT =
            registerComponentType(
                    "weapon_component",
                    builder -> builder
                            .persistent(CompoundTag.CODEC)
                            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> FUEL =
            registerComponentType(
                    "fuel",
                    builder -> builder
                            .persistent(CompoundTag.CODEC)
                            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> ATTACHMENTS =
            registerComponentType(
                    "attachments",
                    builder -> builder
                            .persistent(CompoundTag.CODEC)
                            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AMMO_COUNT =
            registerComponentType(
                    "ammo_count",
                    builder -> builder
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IGNORE_AMMO =
            registerComponentType(
                    "ignore_ammo",
                    builder -> builder
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> AMMO =
            registerComponentType(
                    "ammo",
                    builder -> builder
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FIRE_MODE =
            registerComponentType(
                    "fire_mode",
                    builder -> builder
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> THROW_MODE =
            registerComponentType(
                    "throw_mode",
                    builder -> builder
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    private static CompoundTag copyOrEmpty(@Nullable CompoundTag tag) {
        return tag == null ? new CompoundTag() : tag.copy();
    }

    public static CompoundTag getWeaponTag(ItemStack stack) {
        return copyOrEmpty(stack.get(WEAPON_COMPONENT.get()));
    }

    public static @Nullable CompoundTag setWeaponTag(ItemStack stack, CompoundTag tag) {
        return stack.set(WEAPON_COMPONENT.get(), copyOrEmpty(tag));
    }

    public static CompoundTag getChassisTag(ItemStack stack) {
        return copyOrEmpty(stack.get(CHASSIS_COMPONENT.get()));
    }

    public static @Nullable CompoundTag setChassisTag(ItemStack stack, CompoundTag tag) {
        return stack.set(CHASSIS_COMPONENT.get(), copyOrEmpty(tag));
    }

    public static CompoundTag getFuelTag(ItemStack stack) {
        return copyOrEmpty(stack.get(FUEL.get()));
    }

    public static @Nullable CompoundTag setFuelTag(ItemStack stack, CompoundTag tag) {
        return stack.set(FUEL.get(), copyOrEmpty(tag));
    }

    public static CompoundTag getAttachmentsTag(ItemStack stack) {
        return copyOrEmpty(stack.get(ATTACHMENTS.get()));
    }

    public static @Nullable CompoundTag setAttachmentsTag(ItemStack stack, CompoundTag tag) {
        return stack.set(ATTACHMENTS.get(), copyOrEmpty(tag));
    }
}
