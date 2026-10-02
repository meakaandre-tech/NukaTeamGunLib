package com.nukateam.geo.render;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Per-render data handed to GeckoLib as the "relative object" of a {@link DynamicGeoItemRenderer} pass.
 */
public record RenderContext(LivingEntity entity, ItemStack stack, ItemDisplayContext transformType) {}
