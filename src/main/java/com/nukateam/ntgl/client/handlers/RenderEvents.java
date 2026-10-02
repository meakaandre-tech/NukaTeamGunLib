package com.nukateam.ntgl.client.handlers;

import com.nukateam.ntgl.ClientProxy;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.data.enums.DeathType;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import com.nukateam.ntgl.platform.EventPriority;
import com.nukateam.ntgl.platform.SubscribeEvent;

public class RenderEvents {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderLivingEventPre(RenderLivingEvent.Pre event) {
        var dt = ClientProxy.getDamageType(event.getEntity());

        if (dt != null && (dt == DeathType.LASER || dt == DeathType.FIRE || dt == DeathType.GORE)) {
            event.setCanceled(true);
//            DeathEffectEntityRenderer.doRender(event.getRenderer(), event.getEntity(), event.getPoseStack(),
//            event.getMultiBufferSource(), event.getPackedLight(), event.getEntity().position());
        }
    }
}
