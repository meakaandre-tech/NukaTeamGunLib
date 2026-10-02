package com.nukateam.geo.render;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.ntgl.client.registry.WeaponRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * Item renderer GeckoLib calls for the "geckolib:geckolib" special item model (gui, ground, item frames...).
 * It does not render anything by itself, it forwards to the {@link DynamicGeoItemRenderer} registered for the item.
 */
public class ProxyItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    public static final DataTicket<GeoItemRenderer.RenderData> RENDER_DATA =
            DataTicket.create("ntgl_item_render_data", GeoItemRenderer.RenderData.class);

    public ProxyItemRenderer() {
        super(new PlaceholderModel<>());
    }

    @Override
    public GeoRenderState fillRenderState(T animatable, GeoItemRenderer.RenderData renderData,
                                          GeoRenderState renderState, float partialTick) {
        renderState.addGeckolibData(RENDER_DATA, renderData);
        return renderState;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void submit(GeoRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, int outlineColor) {
        var renderData = renderState.getOrDefaultGeckolibData(RENDER_DATA, (GeoItemRenderer.RenderData) null);
        if (renderData == null) return;

        var stack = renderData.itemStack();
        DynamicGeoItemRenderer renderer = WeaponRegistry.getRenderer(stack.getItem());
        if (renderer == null) return;

        LivingEntity entity = Minecraft.getInstance().player;
        if (entity == null) return;

        renderer.render(entity, stack, renderData.renderPerspective(), poseStack, collector, renderState.getPackedLight());
    }
}
