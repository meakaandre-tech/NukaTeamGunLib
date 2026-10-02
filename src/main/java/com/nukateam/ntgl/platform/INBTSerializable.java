package com.nukateam.ntgl.platform;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

/**
 * Stand-in for NeoForge's INBTSerializable.
 */
public interface INBTSerializable<T extends Tag> {
    T serializeNBT(HolderLookup.Provider provider);

    void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
