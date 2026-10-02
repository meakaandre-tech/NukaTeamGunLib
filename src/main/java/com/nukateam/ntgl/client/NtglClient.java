package com.nukateam.ntgl.client;

import com.nukateam.ntgl.CommonProxy;
import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.handlers.*;
import com.nukateam.ntgl.client.input.KeyPressHandler;
import com.nukateam.ntgl.client.input.NtglKeyBinds;
import com.nukateam.ntgl.client.render.hud.ActionWheelHud;
import com.nukateam.ntgl.client.render.hud.DebugHud;
import com.nukateam.ntgl.client.render.hud.ScopeHud;
import com.nukateam.ntgl.client.render.hud.WeaponHud;
import com.nukateam.ntgl.client.render.screen.AttachmentScreen;
import com.nukateam.ntgl.client.render.screen.WorkbenchScreen;
import com.nukateam.ntgl.client.tooltip.ItemsClientTooltipComponent;
import com.nukateam.ntgl.client.util.handler.AimingHandler;
import com.nukateam.ntgl.client.util.handler.ClientMeleeHandler;
import com.nukateam.ntgl.client.util.handler.ClientThrowHandler;
import com.nukateam.ntgl.client.util.handler.CrosshairHandler;
import com.nukateam.ntgl.common.foundation.init.ModEffects;
import com.nukateam.ntgl.common.foundation.init.NtglContainers;
import com.nukateam.ntgl.client.tooltip.ItemsTooltipData;
import com.nukateam.ntgl.platform.PlatformHelper;
import com.nukateam.ntgl.platform.event.ClientTickEvent;
import com.nukateam.ntgl.platform.event.client.ClientPlayerNetworkEvent;
import com.nukateam.ntgl.platform.event.client.RenderLevelStageEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;

/**
 * Client entry point of the Fabric port: everything NeoForge did through client-side mod bus events
 * (renderers, screens, key binds, HUD layers, particles) is registered here.
 */
public class NtglClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CrosshairHandler.onConfigReload();
        ClientPacketHandler.register();
        NtglKeyBinds.registerKeyMappings();

        ClientHandler.setup();
        Ntgl.EVENT_BUS.register(ClientHandler.class);
        Ntgl.EVENT_BUS.register(ClientTickHandler.class);
        Ntgl.EVENT_BUS.register(InputHandler.class);
        Ntgl.EVENT_BUS.register(PlayerEvents.class);
        Ntgl.EVENT_BUS.register(KeyPressHandler.class);
        Ntgl.EVENT_BUS.register(ClientMeleeHandler.class);
        Ntgl.EVENT_BUS.register(ClientThrowHandler.class);
        Ntgl.EVENT_BUS.register(CommonProxy.class);

        registerLifecycleEvents();

        MenuScreens.register(NtglContainers.WORKBENCH.get(), WorkbenchScreen::new);
        MenuScreens.register(NtglContainers.ATTACHMENTS.get(), AttachmentScreen::new);

        ProjectileRenderers.register();
        ParticleFactoryRegistry.register();

        ClientTooltipComponentCallback.EVENT.register(data ->
                data instanceof ItemsTooltipData itemsData ? new ItemsClientTooltipComponent(itemsData) : null);

        registerHud();
    }

    private static void registerLifecycleEvents() {
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> Ntgl.EVENT_BUS.post(new ClientTickEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> Ntgl.EVENT_BUS.post(new ClientTickEvent.Post()));

        ClientPlayConnectionEvents.JOIN.register((listener, sender, minecraft) -> {
            if (minecraft.level != null)
                PlatformHelper.setFuelValues(minecraft.level.fuelValues());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) ->
                Ntgl.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingOut()));

        LevelRenderEvents.START_MAIN.register(context -> {
            var minecraft = Minecraft.getInstance();
            var deltaTracker = minecraft.getDeltaTracker();
            AimingHandler.get().onRenderOverlay(deltaTracker);
            Ntgl.EVENT_BUS.post(new RenderLevelStageEvent(
                    deltaTracker.getGameTimeDeltaPartialTick(false),
                    minecraft.player != null ? minecraft.player.tickCount : 0));
        });
    }

    private static void registerHud() {
        HudElementRegistry.addFirst(id("blinded"), NtglClient::renderBlindedOverlay);

        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, id("ammo"), WeaponHud::render);
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, id("action_wheel"), ActionWheelHud::render);
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, id("debug"), DebugHud::render);
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, id("scope"), ScopeHud::render);

        // NTGL draws its own crosshair while a weapon is held and hides it when aiming down sights
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, vanilla -> (graphics, deltaTracker) -> {
            AimingHandler.get().onRenderOverlay(deltaTracker);

            if (!CrosshairHandler.get().onRenderOverlay(graphics, deltaTracker))
                vanilla.extractRenderState(graphics, deltaTracker);
        });
    }

    /** White screen overlay of the "blinded" effect (stun grenades), fading out as the effect ends. */
    private static void renderBlindedOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        var effect = player.getEffect(ModEffects.BLINDED.getHolder());
        if (effect == null) return;

        float percent = Math.min((effect.getDuration() / (float) Config.SERVER.alphaFadeThreshold.get()), 1);
        int alpha = (int) (percent * Config.SERVER.alphaOverlay.get() + 0.5);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), (alpha << 24) | 0xFFFFFF);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, path);
    }
}
