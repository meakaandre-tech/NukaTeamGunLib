package com.nukateam.ntgl.client.handlers;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.render.particle.BloodParticle;
import com.nukateam.ntgl.client.render.particle.BulletHoleParticle;
import com.nukateam.ntgl.client.render.particle.TrailParticle;
import com.nukateam.ntgl.common.foundation.init.ModParticleTypes;
import com.nukateam.ntgl.platform.event.client.*;
import com.nukateam.ntgl.platform.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ParticleFactoryRegistry {
    @SubscribeEvent
    public static void onRegisterParticleFactory(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticleTypes.BULLET_HOLE.get(),
                (typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed) ->
                        new BulletHoleParticle(worldIn, x, y, z, typeIn.direction(), typeIn.pos()));
        event.registerSpriteSet(ModParticleTypes.BLOOD.get(), BloodParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.TRAIL.get(), TrailParticle.Factory::new);
    }
}
