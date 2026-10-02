package com.nukateam.ntgl.modules.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Author: MrCrayfish
 */
/**
 * Codecs of the workbench recipe (RecipeSerializer is a record in 26.x, see ModRecipeSerializers).
 */
public class WorkbenchRecipeSerializer {
    public static final MapCodec<WorkbenchRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> {
                return instance.group(
                        ItemStack.CODEC.fieldOf("result").forGetter(WorkbenchRecipe::result),
                        WorkbenchIngredient.CODEC.listOf().fieldOf("materials").forGetter(WorkbenchRecipe::materials)
                ).apply(instance, WorkbenchRecipe::new);
            });

    public static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC,
                    WorkbenchRecipe::result,
                    WorkbenchIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    WorkbenchRecipe::materials,
                    WorkbenchRecipe::new
            );
}
