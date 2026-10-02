package com.nukateam.ntgl.client.model;

import com.geckolib.animatable.GeoAnimatable;
import net.minecraft.resources.Identifier;

public interface IGlowingModel<T extends GeoAnimatable> {
    Identifier getGlowingTextureResource(T animatable);
}
