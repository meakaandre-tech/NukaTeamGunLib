package com.nukateam.ntgl.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemsClientTooltipComponent implements ClientTooltipComponent {
    private final List<ItemStack> items;

    public ItemsClientTooltipComponent(ItemsTooltipData data) {
        this.items = data.items();
    }

    @Override
    public int getHeight(Font font) {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return items.size() * 18;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor guiGraphics) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            guiGraphics.item(item, x + i * 18, y);
            guiGraphics.itemDecorations(font, item, x + i * 18, y);
        }
    }
}