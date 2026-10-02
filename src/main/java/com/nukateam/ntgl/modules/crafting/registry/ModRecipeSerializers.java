package com.nukateam.ntgl.modules.crafting.registry;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.modules.crafting.recipe.WorkbenchRecipe;
import com.nukateam.ntgl.modules.crafting.recipe.WorkbenchRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.nukateam.ntgl.platform.DeferredHolder;
import com.nukateam.ntgl.platform.DeferredRegister;

/**
 * Author: MrCrayfish
 */
public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Ntgl.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WorkbenchRecipe>> WORKBENCH = REGISTER.register("workbench",
            () -> new RecipeSerializer<>(WorkbenchRecipeSerializer.CODEC, WorkbenchRecipeSerializer.STREAM_CODEC));
}
