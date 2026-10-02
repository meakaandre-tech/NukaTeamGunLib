package com.nukateam.geo.interfaces;

import com.geckolib.animatable.GeoAnimatable;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IItemAnimator extends GeoAnimatable {
    ItemStack getStack();
    void setStack(ItemStack stack);
    ItemDisplayContext getTransformType();
}
