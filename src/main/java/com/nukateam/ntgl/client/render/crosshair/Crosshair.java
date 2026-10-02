package com.nukateam.ntgl.client.render.crosshair;

import com.nukateam.ntgl.client.util.handler.CrosshairHandler;
import com.nukateam.ntgl.common.util.interfaces.IResourceLocation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/**
 * Author: MrCrayfish
 */
public abstract class Crosshair implements IResourceLocation {
    public static final Crosshair DEFAULT = new Crosshair(Identifier.tryParse("default")) {
    };

    static {
        CrosshairHandler.get().register(DEFAULT);
    }

    private Identifier id;

    /**
     * The default constructor for crosshairs
     *
     * @param id the id for the crosshair
     */
    protected Crosshair(Identifier id) {
        this.id = id;
    }

    /**
     * Renders the crosshair to the screen. If implementing, positioning is not initially set to
     * the center of the screen. Use windowWidth and windowHeight for calculating the center. It
     * should be considered that the player may not be in a world.
     *
     * @param mc           a minecraft instance
     * @param graphics     the gui graphics of the frame (26.x: replaces the matrix stack)
     * @param windowWidth  the scaled width of the window
     * @param windowHeight the scaled height of the window
     * @param partialTicks
     */
    public void render(Minecraft mc, GuiGraphicsExtractor graphics, int windowWidth, int windowHeight, float partialTicks) {
    }

    /**
     * Ticks the crosshair for any logic
     */
    public void tick() {
    }

    /**
     * Called when the held gun is fired
     */
    public void onGunFired() {
    }

    /**
     * Gets the id of the crosshair
     */
    @Override

    public final Identifier getLocation() {
        return this.id;
    }

    /**
     * Test for default crosshair (aka normal minecraft crosshair)
     */
    public final boolean isDefault() {
        return this == DEFAULT;
    }
}
