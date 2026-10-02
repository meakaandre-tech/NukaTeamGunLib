package com.nukateam.geo.render;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class PlaceholderModel<T extends GeoAnimatable> extends GeoModel<T> {
    private static final Identifier EMPTY = Identifier.fromNamespaceAndPath("ntgl", "placeholder");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return EMPTY;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return EMPTY;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return EMPTY;
    }
}
