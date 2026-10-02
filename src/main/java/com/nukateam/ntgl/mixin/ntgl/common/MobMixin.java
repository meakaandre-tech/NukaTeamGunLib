package com.nukateam.ntgl.mixin.ntgl.common;

import com.nukateam.ntgl.common.foundation.entity.StunGrenadeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mobs blinded by a stun grenade cannot pick a target (NeoForge: LivingChangeTargetEvent).
 */
@Mixin(Mob.class)
public class MobMixin {
    @ModifyVariable(method = "setTarget(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), argsOnly = true)
    private LivingEntity ntgl$blindedMobsHaveNoTarget(LivingEntity target) {
        if (target != null && StunGrenadeEntity.isBlinded((Mob) (Object) this)) {
            return null;
        }
        return target;
    }
}
