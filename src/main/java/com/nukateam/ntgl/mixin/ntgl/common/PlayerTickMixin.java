package com.nukateam.ntgl.mixin.ntgl.common;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.platform.event.PlayerTickEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Raises PlayerTickEvent.Pre/Post (NeoForge fired these around Player#tick on both sides).
 */
@Mixin(Player.class)
public class PlayerTickMixin {
    @Inject(method = "tick()V", at = @At("HEAD"))
    private void ntgl$preTick(CallbackInfo ci) {
        Ntgl.EVENT_BUS.post(new PlayerTickEvent.Pre((Player) (Object) this));
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void ntgl$postTick(CallbackInfo ci) {
        Ntgl.EVENT_BUS.post(new PlayerTickEvent.Post((Player) (Object) this));
    }
}
