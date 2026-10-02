package com.nukateam.ntgl.mixin.ntgl.client;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.platform.event.client.PlaySoundEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Raises PlaySoundEvent, which lets the stun grenade handler mute sounds while the player is deafened.
 */
@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @ModifyVariable(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;",
            at = @At("HEAD"), argsOnly = true)
    private SoundInstance ntgl$onPlaySound(SoundInstance sound) {
        if (sound == null) return null;
        var event = Ntgl.EVENT_BUS.post(new PlaySoundEvent((SoundEngine) (Object) this, sound));
        return event.getSound() != null ? event.getSound() : sound;
    }
}
