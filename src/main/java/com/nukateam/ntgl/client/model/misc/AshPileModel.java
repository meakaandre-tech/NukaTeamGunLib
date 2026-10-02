package com.nukateam.ntgl.client.model.misc;

import com.nukateam.geo.render.AnimatableGeoModel;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.foundation.entity.misc.AshPile;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class AshPileModel extends AnimatableGeoModel<AshPile> {
    @Override
    public Identifier getModelResource(AshPile object) {
        return Identifier.tryBuild(Ntgl.MOD_ID, "misc/ash");
    }

    @Override
    public Identifier getTextureResource(AshPile object) {
        return Identifier.tryBuild(Ntgl.MOD_ID, "textures/misc/ash.png");
    }

    @Override
    public Identifier getAnimationResource(AshPile object) {
        return Identifier.tryBuild(Ntgl.MOD_ID, "misc/void");
    }

    @Override
    public RenderType getRenderType(AshPile animatable, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
