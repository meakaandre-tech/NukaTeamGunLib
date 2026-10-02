package com.nukateam.ntgl.common.debug.screen.widget;

import com.nukateam.ntgl.common.debug.IDebugWidget;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 * <p>
 * Fabric port: no longer a GUI Button (nothing shows the debug editor any more and the data classes
 * that describe these widgets are loaded on dedicated servers too), just the widget description.
 */
public class DebugButton implements IDebugWidget {
    @FunctionalInterface
    public interface OnPress {
        void onPress(DebugButton button);
    }

    private final Supplier<Boolean> enabled;
    private final OnPress onPress;
    private Component message;

    public DebugButton(Component label, OnPress onPress) {
        this(label, onPress, () -> true);
    }

    public DebugButton(Component label, OnPress onPress, Supplier<Boolean> enabled) {
        this.message = label;
        this.onPress = onPress;
        this.enabled = enabled;
    }

    public void onPress() {
        if (isActive()) {
            this.onPress.onPress(this);
        }
    }

    public boolean isActive() {
        return this.enabled.get();
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }
}
