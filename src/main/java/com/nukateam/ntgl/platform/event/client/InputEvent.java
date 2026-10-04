package com.nukateam.ntgl.platform.event.client;

import com.nukateam.ntgl.platform.Event;
import com.nukateam.ntgl.platform.ICancellableEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;

/**
 * Input events, raised from MinecraftMixin and MouseHandlerMixin. Stand-in for NeoForge's InputEvent.
 */
public abstract class InputEvent extends Event {
    /** Fired after a mouse button was pressed or released (InputConstants button and action codes; SDL numbering since 26.3). */
    public static abstract class MouseButton extends InputEvent {
        private final int button;
        private final int action;
        private final int modifiers;

        protected MouseButton(int button, int action, int modifiers) {
            this.button = button;
            this.action = action;
            this.modifiers = modifiers;
        }

        public int getButton() {
            return button;
        }

        public int getAction() {
            return action;
        }

        public int getModifiers() {
            return modifiers;
        }

        public static class Post extends MouseButton {
            public Post(int button, int action, int modifiers) {
                super(button, action, modifiers);
            }
        }
    }

    /**
     * Fired when the attack, use or pick key triggers its action; cancelling stops the vanilla
     * action, and the hand swing can be suppressed separately.
     */
    public static class InteractionKeyMappingTriggered extends InputEvent implements ICancellableEvent {
        private final int button;
        private final KeyMapping keyMapping;
        private final InteractionHand hand;
        private boolean handSwing = true;

        public InteractionKeyMappingTriggered(int button, KeyMapping keyMapping, InteractionHand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        public void setSwingHand(boolean value) {
            handSwing = value;
        }

        public boolean shouldSwingHand() {
            return handSwing;
        }

        public InteractionHand getHand() {
            return hand;
        }

        public boolean isAttack() {
            return button == 0;
        }

        public boolean isUseItem() {
            return button == 1;
        }

        public boolean isPickBlock() {
            return button == 2;
        }

        public KeyMapping getKeyMapping() {
            return keyMapping;
        }
    }
}
