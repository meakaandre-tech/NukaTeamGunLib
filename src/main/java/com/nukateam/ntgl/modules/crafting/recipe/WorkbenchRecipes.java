package com.nukateam.ntgl.modules.crafting.recipe;

import com.nukateam.ntgl.modules.crafting.registry.ModRecipeTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jetug
 * <p>
 * Fabric 26.2: recipes are no longer synchronised to clients by the game, so the server sends the
 * workbench recipes with S2CMessageUpdateRecipes and the client reads them from {@link #setClientRecipes}.
 */
public class WorkbenchRecipes {
    private static List<RecipeHolder<WorkbenchRecipe>> clientRecipes = List.of();

    public static void setClientRecipes(List<RecipeHolder<WorkbenchRecipe>> recipes) {
        clientRecipes = List.copyOf(recipes);
    }

    public static boolean isEmpty(Level level) {
        return getAllHolders(level).isEmpty();
    }

    @SuppressWarnings("unchecked")
    public static List<RecipeHolder<WorkbenchRecipe>> getAllHolders(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            var result = new ArrayList<RecipeHolder<WorkbenchRecipe>>();
            for (var holder : serverLevel.recipeAccess().getRecipes()) {
                if (holder.value().getType() == ModRecipeTypes.WORKBENCH.get()) {
                    result.add((RecipeHolder<WorkbenchRecipe>) holder);
                }
            }
            return result;
        }
        return clientRecipes;
    }

    public static List<WorkbenchRecipe> getAll(Level level) {
        return getAllHolders(level)
                .stream()
                .map(RecipeHolder::value)
                .toList();
    }

    @Nullable
    public static RecipeHolder<WorkbenchRecipe> getRecipeById(Level level, Identifier id) {
        return getAllHolders(level)
                .stream()
                .filter(holder -> holder.id().identifier().equals(id))
                .findFirst()
                .orElse(null);
    }
}
