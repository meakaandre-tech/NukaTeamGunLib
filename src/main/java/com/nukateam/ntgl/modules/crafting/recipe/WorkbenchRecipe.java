package com.nukateam.ntgl.modules.crafting.recipe;

import com.nukateam.ntgl.modules.crafting.registry.ModRecipeSerializers;
import com.nukateam.ntgl.common.util.util.InventoryUtil;
import com.nukateam.ntgl.modules.crafting.registry.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record WorkbenchRecipe(ItemStack result, List<WorkbenchIngredient> materials) implements Recipe<WorkbenchRecipeInput> {
    public WorkbenchRecipe(ItemStack result, List<WorkbenchIngredient> materials) {
        this.result = result;
        this.materials = List.copyOf(materials);
    }

    @Override
    public boolean matches(WorkbenchRecipeInput input, Level level) {
        var player = input.player();

        for (WorkbenchIngredient ingredient : materials) {
            if (!InventoryUtil.hasWorkstationIngredient(player, ingredient)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(WorkbenchRecipeInput input) {
        return result.copy();
    }

    public ItemStack getResultItem(HolderLookup.Provider access) {
        return result.copy();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
        return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
        return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<WorkbenchRecipe> getSerializer() {
        return ModRecipeSerializers.WORKBENCH.get();
    }

    @Override
    public RecipeType<WorkbenchRecipe> getType() {
        return ModRecipeTypes.WORKBENCH.get();
    }

    public void consumeMaterials(Player player) {
        for (WorkbenchIngredient ingredient : materials) {
            InventoryUtil.removeWorkstationIngredient(player, ingredient);
        }
    }
}