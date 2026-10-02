package com.nukateam.ntgl.client.model.misc;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.foundation.entity.misc.AshPile;
import com.geckolib.model.GeoModel;
import net.minecraft.resources.Identifier;

public class AshPileModel extends GeoModel<AshPile> {
    @Override
    public Identifier getModelResource(AshPile object) {
        return Identifier.tryBuild(Ntgl.MOD_ID, "geo/misc/ash.geo.json");
    }

    @Override
    public Identifier getTextureResource(AshPile object) {
        return Identifier.tryBuild(Ntgl.MOD_ID, "textures/misc/ash.png");
    }

    @Override
    public Identifier getAnimationResource(AshPile object) {
        return Identifier.tryBuild(Ntgl.MOD_ID, "animations/misc/void.animation.json");
    }
}