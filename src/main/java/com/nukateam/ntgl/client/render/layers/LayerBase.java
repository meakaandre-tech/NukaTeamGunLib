package com.nukateam.ntgl.client.render.layers;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class LayerBase<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public LayerBase(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }

    /** Re-renders the model with a glowing ("eyes") render type and the given texture. */
    protected void renderLayer(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector collector, Identifier texture) {
        this.renderer.submitRenderTasks(renderPassInfo, collector.order(1), RenderTypes.eyes(texture));
    }
}
