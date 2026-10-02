package com.nukateam.ntgl;

import com.nukateam.ntgl.platform.SubscribeEvent;
import com.nukateam.ntgl.platform.event.ClientTickEvent;

import java.util.HashMap;

import static com.nukateam.ntgl.ClientProxy.damageTypes;

public class CommonProxy {
    @SubscribeEvent
    public static void onServerTick(ClientTickEvent.Pre event) {
        var buffMap = new HashMap<>(damageTypes);

        buffMap.forEach((key, value) -> {
                if (value.ticks <= 0) {
                    damageTypes.remove(key);
                }
                value.ticks--;
        });
    }
}
