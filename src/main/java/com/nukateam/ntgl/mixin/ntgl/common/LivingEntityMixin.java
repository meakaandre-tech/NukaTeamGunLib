package com.nukateam.ntgl.mixin.ntgl.common;

import com.nukateam.ntgl.common.foundation.item.WeaponItem;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.common.foundation.entity.ProjectileEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Author: MrCrayfish
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    /**
     * Knockback of projectile hits is configurable. 26.x: the default knockback moved from hurt()
     * to dealDefaultKnockback(), which receives the damage source.
     */
    @ModifyArg(method = "dealDefaultKnockback(Lnet/minecraft/world/damagesource/DamageSource;FZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDDLnet/minecraft/world/damagesource/DamageSource;F)V"), index = 0)
    private double modifyApplyKnockbackArgs(double original, @Local(argsOnly = true) DamageSource source) {
        if (source.getDirectEntity() instanceof ProjectileEntity) {
            if (!Config.COMMON.gameplay.enableKnockback.get()) {
                return 0;
            }

            double strength = Config.COMMON.gameplay.knockbackStrength.get();
            if (strength > 0) {
                return strength;
            }
        }

        return original;
    }

    /**
     * Stand-in for NeoForge's IItemExtension.onEntitySwing: weapons suppress the vanilla arm swing.
     */
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void ntgl$onEntitySwing(InteractionHand hand, boolean updateSelf, CallbackInfo ci) {
        var self = (LivingEntity) (Object) this;
        var stack = self.getItemInHand(hand);

        if (!stack.isEmpty() && stack.getItem() instanceof WeaponItem weapon && weapon.onEntitySwing(stack, self, hand)) {
            ci.cancel();
        }
    }
}
