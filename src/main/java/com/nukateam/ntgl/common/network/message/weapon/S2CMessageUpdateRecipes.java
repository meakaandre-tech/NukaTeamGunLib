package com.nukateam.ntgl.common.network.message.weapon;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.modules.crafting.recipe.WorkbenchRecipe;
import com.nukateam.ntgl.modules.crafting.recipe.WorkbenchRecipeSerializer;
import com.nukateam.ntgl.modules.crafting.recipe.WorkbenchRecipes;
import com.nukateam.ntgl.platform.IPayloadContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Sends the workbench recipes to a client (26.x no longer synchronises recipes itself).
 */
public class S2CMessageUpdateRecipes implements CustomPacketPayload {
    public static final Type<S2CMessageUpdateRecipes> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "s2c_message_update_recipes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMessageUpdateRecipes> CODEC = StreamCodec.of(
            (buffer, message) -> encode(message, buffer),
            S2CMessageUpdateRecipes::decode);

    private final List<RecipeHolder<WorkbenchRecipe>> recipes;

    public S2CMessageUpdateRecipes(List<RecipeHolder<WorkbenchRecipe>> recipes) {
        this.recipes = recipes;
    }

    public static void encode(S2CMessageUpdateRecipes message, RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(message.recipes.size());
        for (var holder : message.recipes) {
            buffer.writeIdentifier(holder.id().identifier());
            WorkbenchRecipeSerializer.STREAM_CODEC.encode(buffer, holder.value());
        }
    }

    public static S2CMessageUpdateRecipes decode(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        var recipes = new ArrayList<RecipeHolder<WorkbenchRecipe>>(size);
        for (int i = 0; i < size; i++) {
            var id = buffer.readIdentifier();
            var recipe = WorkbenchRecipeSerializer.STREAM_CODEC.decode(buffer);
            recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), recipe));
        }
        return new S2CMessageUpdateRecipes(recipes);
    }

    public static void handle(S2CMessageUpdateRecipes message, IPayloadContext supplier) {
        supplier.enqueueWork(() -> WorkbenchRecipes.setClientRecipes(message.recipes));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
