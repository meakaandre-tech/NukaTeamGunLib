package com.nukateam.ntgl.modules.crafting;

import com.nukateam.ntgl.modules.crafting.registry.ModRecipeSerializers;
import com.nukateam.ntgl.modules.crafting.registry.ModRecipeTypes;

public class CraftingModule {
    public static void init() {
        ModRecipeSerializers.REGISTER.register();
        ModRecipeTypes.REGISTER.register();
    }
}
