package com.nukateam.ntgl.client.handlers;

import com.nukateam.chassis_core.common.util.helpers.PlayerUtils;
import com.nukateam.ntgl.*;
import com.nukateam.ntgl.client.model.GunIconModels;
import com.nukateam.ntgl.client.tooltip.*;
import com.nukateam.ntgl.client.render.hud.*;
import com.nukateam.ntgl.client.tooltip.ItemsTooltipData;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.*;
import com.nukateam.ntgl.platform.event.client.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ClientEvents {
    @SubscribeEvent
    public static void onRegisterTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemsTooltipData.class, ItemsClientTooltipComponent::new);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        var key = Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "gun_icon_loader");
        event.register(key, GunIconModels.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerOverlay(final RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "ammo"),
                WeaponHud::render);

        event.registerBelow(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "action_wheel"),
                ActionWheelHud::render);

        event.registerBelow(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "debug"),
                DebugHud::render);

        event.registerBelow(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "scope"),
                ScopeHud::render);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Pre event) {
        if (PlayerUtils.isLocalWearingChassis()) {
            if (event.getName().equals(VanillaGuiLayers.VEHICLE_HEALTH))
                event.setCanceled(true);
        }
    }
}
