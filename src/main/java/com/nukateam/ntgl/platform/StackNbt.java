package com.nukateam.ntgl.platform;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

/**
 * ItemStack to/from NBT, replacing ItemStack#save and ItemStack#parseOptional that 26.x removed.
 */
public final class StackNbt {
    private StackNbt() {
    }

    /** Encodes the stack (an empty stack gives an empty tag). */
    public static CompoundTag save(HolderLookup.Provider registries, ItemStack stack) {
        if (stack.isEmpty() || registries == null) return new CompoundTag();
        var result = ItemStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack);
        return result.result().filter(tag -> tag instanceof CompoundTag).map(tag -> (CompoundTag) tag).orElseGet(CompoundTag::new);
    }

    /** Decodes a stack; empty or invalid tags give ItemStack.EMPTY. */
    public static ItemStack parse(HolderLookup.Provider registries, Tag tag) {
        if (registries == null || !(tag instanceof CompoundTag compound) || compound.isEmpty()) return ItemStack.EMPTY;
        return ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), compound).result().orElse(ItemStack.EMPTY);
    }
}
