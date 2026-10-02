package com.nukateam.ntgl.platform;

/**
 * Marker for events that listeners may cancel. Stand-in for NeoForge's ICancellableEvent.
 */
public interface ICancellableEvent {
    default boolean isCanceled() {
        return ((Event) this).isCanceled();
    }

    default void setCanceled(boolean canceled) {
        ((Event) this).setCanceled(canceled);
    }
}
