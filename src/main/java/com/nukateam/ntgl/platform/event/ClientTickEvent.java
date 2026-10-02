package com.nukateam.ntgl.platform.event;

import com.nukateam.ntgl.platform.Event;

public abstract class ClientTickEvent extends Event {
    public static class Pre extends ClientTickEvent {
    }

    public static class Post extends ClientTickEvent {
    }
}
