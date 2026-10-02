package com.nukateam.ntgl.client.util.handler;

import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.render.crosshair.Crosshair;
import com.nukateam.ntgl.client.render.crosshair.TechCrosshair;
import com.nukateam.ntgl.client.render.crosshair.TexturedCrosshair;
import com.nukateam.ntgl.common.event.GunFireEvent;

import com.google.common.collect.ImmutableList;
import com.nukateam.ntgl.common.foundation.item.interfaces.IWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.nukateam.ntgl.platform.event.ClientTickEvent;
import com.nukateam.ntgl.platform.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class CrosshairHandler {
    private static CrosshairHandler instance;

    public static CrosshairHandler get() {
        if (instance == null) {
            instance = new CrosshairHandler();
        }
        return instance;
    }

    private final Map<Identifier, Crosshair> idToCrosshair = new HashMap<>();
    private final List<Crosshair> registeredCrosshairs = new ArrayList<>();
    private Crosshair currentCrosshair = null;

    private CrosshairHandler() {
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "better_default")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "circle")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "filled_circle"), false));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "square")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "round")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "arrow")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "dot")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "box")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "hit_marker")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "line")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "t")));
        this.register(new TexturedCrosshair(Identifier.tryBuild(Ntgl.MOD_ID, "smiley")));
        this.register(new TechCrosshair());
    }

    /**
     * Registers a new crosshair. If the crosshair has already been registered, it will be ignored.
     */
    public void register(Crosshair crosshair) {
        if (!this.idToCrosshair.containsKey(crosshair.getLocation())) {
            this.idToCrosshair.put(crosshair.getLocation(), crosshair);
            this.registeredCrosshairs.add(crosshair);
        }
    }

    /**
     * Sets the crosshair using the given id. The crosshair with the associated id must be registered
     * or the default crosshair will be used.
     *
     * @param id the id of the crosshair
     */
    public void setCrosshair(Identifier id) {
        this.currentCrosshair = this.idToCrosshair.getOrDefault(id, Crosshair.DEFAULT);
    }

    /**
     * Gets the current crosshair
     */
    @Nullable
    public Crosshair getCurrentCrosshair() {
        if (this.currentCrosshair == null && this.registeredCrosshairs.size() > 0) {
            Identifier id = Identifier.tryParse(Config.CLIENT.display.crosshair.get());
            this.currentCrosshair = id != null ? this.idToCrosshair.getOrDefault(id, Crosshair.DEFAULT) : Crosshair.DEFAULT;
        }
        return this.currentCrosshair;
    }

    /**
     * Gets a list of registered crosshairs. Please note that this list is immutable.
     */
    public List<Crosshair> getRegisteredCrosshairs() {
        return ImmutableList.copyOf(this.registeredCrosshairs);
    }

    /**
     * Draws the NTGL crosshair in place of the vanilla one (NeoForge: RenderGuiLayerEvent.Pre for the crosshair layer).
     *
     * @return true when the vanilla crosshair must not be drawn
     */
    public boolean onRenderOverlay(net.minecraft.client.gui.GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        var crosshair = this.getCurrentCrosshair();

        if (AimingHandler.get().getNormalisedAdsProgress() > 0.5) {
            return true;
        }

        if (crosshair == null || crosshair.isDefault()) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return false;

        ItemStack heldItem = mc.player.getMainHandItem();
        if (!(heldItem.getItem() instanceof IWeapon))
            return false;

        if (!mc.options.getCameraType().isFirstPerson())
            return true;

        if (mc.player.getUseItem().getItem() == Items.SHIELD)
            return true;

        graphics.pose().pushMatrix();
        crosshair.render(mc, graphics, graphics.guiWidth(), graphics.guiHeight(), deltaTracker.getGameTimeDeltaPartialTick(true));
        graphics.pose().popMatrix();
        return true;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        var crosshair = this.getCurrentCrosshair();
        if (crosshair == null || crosshair.isDefault())
            return;

        crosshair.tick();
    }

    @SubscribeEvent
    public void onGunFired(GunFireEvent.Post event) {
        Crosshair crosshair = this.getCurrentCrosshair();
        if (crosshair == null || crosshair.isDefault())
            return;

        crosshair.onGunFired();
    }

    /* Updates the crosshair from the config (called after the config was loaded). */
    public static void onConfigReload() {
        Identifier id = Identifier.tryParse(Config.CLIENT.display.crosshair.get());
        if (id != null) {
            CrosshairHandler.get().setCrosshair(id);
        }
    }
}
