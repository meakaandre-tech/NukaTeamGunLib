
package com.nukateam.ntgl.client.handlers;

import com.nukateam.geo.render.ItemAnimator;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.platform.SubscribeEvent;
import com.nukateam.ntgl.platform.event.ClientTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClientTickHandler {
    private static final Map<ItemAnimator, Runnable> tickingAnimators = new HashMap();
    private static final Map<Object, Runnable> tickers = new HashMap();

    public ClientTickHandler() {}

    public static void addTicker(ItemAnimator animator, Runnable onTick) {
        tickingAnimators.put(animator, onTick);
    }

    public static void addTicker(Object object, Runnable onTick) {
        tickers.put(object, onTick);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        tickingAnimators.forEach((k, v) -> v.run());
        tickers.forEach((k, v) -> v.run());
    }
}