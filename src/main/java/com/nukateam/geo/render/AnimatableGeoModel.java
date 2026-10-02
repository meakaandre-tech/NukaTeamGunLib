package com.nukateam.geo.render;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * GeckoLib 5 resolves model and texture from the render state instead of the animatable.
 * This base class stores the animatable in the render state so models can keep resolving
 * their resources from the animatable, as they did with GeckoLib 4.
 */
public abstract class AnimatableGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    public static final DataTicket<GeoAnimatable> ANIMATABLE = DataTicket.create("ntgl_animatable", GeoAnimatable.class);

    public abstract Identifier getModelResource(T animatable);

    public abstract Identifier getTextureResource(T animatable);

    public RenderType getRenderType(T animatable, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    @SuppressWarnings("unchecked")
    public T getAnimatable(GeoRenderState renderState) {
        return (T) renderState.getOrDefaultGeckolibData(ANIMATABLE, (GeoAnimatable) null);
    }

    @Override
    public void addAdditionalStateData(T animatable, Object relatedObject, GeoRenderState renderState) {
        renderState.addGeckolibData(ANIMATABLE, animatable);
    }

    @Override
    public final Identifier getModelResource(GeoRenderState renderState) {
        return GeoResourceIds.strip(getModelResource(getAnimatable(renderState)));
    }

    @Override
    public final Identifier getTextureResource(GeoRenderState renderState) {
        return getTextureResource(getAnimatable(renderState));
    }
}
