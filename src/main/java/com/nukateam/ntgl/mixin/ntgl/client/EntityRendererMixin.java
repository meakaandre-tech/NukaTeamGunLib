package com.nukateam.ntgl.mixin.ntgl.client;

import com.nukateam.ntgl.ClientProxy;
import com.nukateam.ntgl.client.render.NtglRenderData;
import com.nukateam.ntgl.common.data.enums.DeathType;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Marks the render state of entities that are being replaced by a death effect (laser, fire, gore),
 * so EntityRenderDispatcherMixin can skip them. Replaces the RenderLivingEvent.Pre and
 * GeoEntityRenderer hooks of the NeoForge version and covers vanilla and GeckoLib renderers alike.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("RETURN"))
    private void ntgl$markHidden(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> cir) {
        var dt = ClientProxy.getDamageType(entity);
        boolean hidden = dt == DeathType.LASER || dt == DeathType.FIRE || dt == DeathType.GORE;
        var data = (FabricRenderState) cir.getReturnValue();
        if (hidden || data.getData(NtglRenderData.HIDDEN) != null) {
            data.setData(NtglRenderData.HIDDEN, hidden);
        }
    }
}
