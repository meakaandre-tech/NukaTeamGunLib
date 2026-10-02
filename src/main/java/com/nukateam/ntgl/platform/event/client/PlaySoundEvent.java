package com.nukateam.ntgl.platform.event.client;

import com.nukateam.ntgl.platform.Event;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

/**
 * Fired before a sound starts playing; the sound may be replaced (null cancels it).
 * Stand-in for NeoForge's PlaySoundEvent, raised from SoundEngineMixin.
 */
public class PlaySoundEvent extends Event {
    private final SoundEngine engine;
    private final SoundInstance originalSound;
    private SoundInstance sound;

    public PlaySoundEvent(SoundEngine engine, SoundInstance sound) {
        this.engine = engine;
        this.originalSound = sound;
        this.sound = sound;
    }

    public SoundEngine getEngine() {
        return engine;
    }

    public SoundInstance getOriginalSound() {
        return originalSound;
    }

    public SoundInstance getSound() {
        return sound;
    }

    public void setSound(SoundInstance sound) {
        this.sound = sound;
    }
}
