package com.nukateam.ntgl.client.handlers;

import com.nukateam.ntgl.client.render.renderers.misc.AshPileRenderer;
import com.nukateam.ntgl.client.render.renderers.misc.FlyingGibsRenderer;
import com.nukateam.ntgl.client.render.renderers.projectiles.*;
import com.nukateam.ntgl.common.foundation.init.ModEntityTypes;
import com.nukateam.ntgl.common.foundation.init.Projectiles;
import com.nukateam.ntgl.Ntgl;import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ProjectileRenderers {
    public static void register() {
        EntityRendererRegistry.register(Projectiles.PROJECTILE.get(), ProjectileRenderer::new);
        EntityRendererRegistry.register(Projectiles.ARROW_LIKE.get(), ProjectileRenderer::new);
        EntityRendererRegistry.register(Projectiles.LASER_PROJECTILE.get(), LaserProjectileRenderer::new);
        EntityRendererRegistry.register(Projectiles.CONTINUOUS_LASER_PROJECTILE.get(), LaserProjectileRenderer::new);
        EntityRendererRegistry.register(Projectiles.TESLA_PROJECTILE.get(), TeslaProjectileRenderer::new);
        EntityRendererRegistry.register(Projectiles.FLAME_PROJECTILE.get(), FlameRenderer::new);
        EntityRendererRegistry.register(Projectiles.GRENADE.get(), GrenadeRenderer::new);
        EntityRendererRegistry.register(Projectiles.MISSILE.get(), MissileRenderer::new);
        EntityRendererRegistry.register(Projectiles.THROWABLE_GRENADE.get(), ThrowableItemRenderer::new);
        EntityRendererRegistry.register(Projectiles.THROWABLE_STUN_GRENADE.get(), ThrowableItemRenderer::new);

        EntityRendererRegistry.register(ModEntityTypes.FLYING_GIBS.get(), FlyingGibsRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.ASH_PILE.get(), AshPileRenderer::new);
    }
}
