package com.nukateam.ntgl.client.render.layers;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.nukateam.ntgl.client.model.IGlowingModel;
import com.nukateam.ntgl.common.util.util.ResourceUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class GlowingLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public static final DataTicket<Identifier> GLOW_TEXTURE = DataTicket.create("ntgl_glow_texture", Identifier.class);
    public static HashMap<Identifier, Boolean> textures = new HashMap<>();

    public GlowingLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }

    protected boolean resourceExists(Identifier location){
        return textures.computeIfAbsent(location, ResourceUtils::resourceExists);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addRenderData(T animatable, O relatedObject, R renderState, float partialTick) {
        if (getGeoModel() instanceof IGlowingModel glowingModel) {
            var texture = glowingModel.getGlowingTextureResource(animatable);

            if (texture != null && resourceExists(texture))
                renderState.addGeckolibData(GLOW_TEXTURE, texture);
        }
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector collector) {
        var texture = renderPassInfo.renderState().getOrDefaultGeckolibData(GLOW_TEXTURE, (Identifier) null);

        if (texture != null && renderPassInfo.willRender())
            renderLayer(renderPassInfo, collector, texture);
    }

    protected void renderLayer(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector collector, Identifier texture) {
        this.renderer.submitRenderTasks(renderPassInfo, collector.order(1), RenderTypes.entityTranslucentEmissive(texture));
    }
}
