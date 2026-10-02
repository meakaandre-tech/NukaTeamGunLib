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
    public static void register() {
        var registry = net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance();

        registry.register(ModParticleTypes.BULLET_HOLE.get(),
                (typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed, random) ->
                        new BulletHoleParticle(worldIn, x, y, z, typeIn.direction(), typeIn.pos()));
        registry.register(ModParticleTypes.BLOOD.get(), BloodParticle.Factory::new);
        registry.register(ModParticleTypes.TRAIL.get(), TrailParticle.Factory::new);
    }
}
