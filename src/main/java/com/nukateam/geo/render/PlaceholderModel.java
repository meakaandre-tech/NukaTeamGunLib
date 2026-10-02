package com.nukateam.geo.render;

import com.geckolib.animatable.GeoAnimatable;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class PlaceholderModel<T extends GeoAnimatable> extends GeoModel<T> {
    @Override
    public Identifier getModelResource(T gunItem) {
//        return getGunResource(gunItem, "geo/guns/", ".geo.json");
        return null;
    }

    @Override
    public Identifier getTextureResource(T gunItem) {
//        return getGunResource(gunItem, "textures/guns/" + gunItem.getName() + "/", ".png");
        return null;
    }

    @Override
    public Identifier getAnimationResource(T gunItem) {
//        return getGunResource(gunItem, "animations/guns/", ".animation.json");
        return null;
    }
}
