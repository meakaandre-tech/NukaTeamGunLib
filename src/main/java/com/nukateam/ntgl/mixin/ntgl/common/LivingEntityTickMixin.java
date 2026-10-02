package com.nukateam.ntgl.mixin.ntgl.common;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.platform.event.EntityTickEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Raises EntityTickEvent.Pre for living entities (the only entities NTGL's handlers look at).
 */
@Mixin(LivingEntity.class)
public class LivingEntityTickMixin {
    @Inject(method = "tick()V", at = @At("HEAD"))
    private void ntgl$preTick(CallbackInfo ci) {
        Ntgl.EVENT_BUS.post(new EntityTickEvent.Pre((LivingEntity) (Object) this));
    }
}
