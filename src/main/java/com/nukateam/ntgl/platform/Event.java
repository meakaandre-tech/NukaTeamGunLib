package com.nukateam.ntgl.platform;

/**
 * Base class of the events NTGL posts on {@link EventBus}. Stand-in for NeoForge's Event.
 */
public abstract class Event {
    private boolean canceled;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
