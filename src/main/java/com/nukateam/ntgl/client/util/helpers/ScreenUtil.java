package com.nukateam.ntgl.client.util.helpers;

import net.minecraft.client.Minecraft;

/**
 * Author: MrCrayfish
 */
public class ScreenUtil {
    public static boolean isMouseWithin(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
