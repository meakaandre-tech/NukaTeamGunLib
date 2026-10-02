package com.nukateam.ntgl.platform.event.client;

import com.nukateam.ntgl.platform.Event;

public abstract class ClientPlayerNetworkEvent extends Event {
    /** Fired on the client when the connection to a (integrated or remote) server is closed. */
    public static class LoggingOut extends ClientPlayerNetworkEvent {
    }
}
