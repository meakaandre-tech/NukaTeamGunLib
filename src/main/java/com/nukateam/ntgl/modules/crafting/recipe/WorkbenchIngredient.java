package com.nukateam.ntgl.modules.crafting.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Author: MrCrayfish
 */
public record WorkbenchIngredient(
        Ingredient ingredient,
        int count
) {

    /**
     * Reads the 26.x ingredient form ("minecraft:iron_ingot", "#c:ingots/iron" or a list) and also the
     * older object form ({"item": "..."} / {"tag": "..."}) that existing gun packs use.
     */
    private static final Codec<Ingredient> INGREDIENT_CODEC = new Codec<>() {
        @Override
        public <T> com.mojang.serialization.DataResult<com.mojang.datafixers.util.Pair<Ingredient, T>> decode(com.mojang.serialization.DynamicOps<T> ops, T input) {
            var map = ops.getMap(input).result();
            if (map.isPresent()) {
                T item = map.get().get("item");
                if (item != null) {
                    return Ingredient.CODEC.decode(ops, item);
                }
                T tag = map.get().get("tag");
                if (tag != null) {
                    return ops.getStringValue(tag).flatMap(name -> Ingredient.CODEC.decode(ops, ops.createString("#" + name)));
                }
            }
            return Ingredient.CODEC.decode(ops, input);
        }

        @Override
        public <T> com.mojang.serialization.DataResult<T> encode(Ingredient input, com.mojang.serialization.DynamicOps<T> ops, T prefix) {
            return Ingredient.CODEC.encode(input, ops, prefix);
        }
    };

    public static final Codec<WorkbenchIngredient> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    INGREDIENT_CODEC.fieldOf("ingredient").forGetter(WorkbenchIngredient::ingredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(WorkbenchIngredient::count)
            ).apply(instance, WorkbenchIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchIngredient> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    WorkbenchIngredient::ingredient,
                    ByteBufCodecs.VAR_INT,
                    WorkbenchIngredient::count,
                    WorkbenchIngredient::new
            );

    public static WorkbenchIngredient of(ItemLike item, int count) {
        return new WorkbenchIngredient(Ingredient.of(item), count);
    }

    public static WorkbenchIngredient of(ItemStack stack, int count) {
        return new WorkbenchIngredient(Ingredient.of(stack.getItem()), count);
    }

    public static WorkbenchIngredient of(TagKey<Item> tag, int count) {
        return new WorkbenchIngredient(Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow(tag)), count);
    }
}