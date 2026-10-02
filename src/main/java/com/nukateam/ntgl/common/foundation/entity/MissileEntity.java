package com.nukateam.ntgl.common.foundation.entity;

import com.nukateam.ntgl.common.data.WeaponData;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class MissileEntity extends ProjectileEntity {
    public MissileEntity(EntityType<? extends ProjectileEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public MissileEntity(EntityType<? extends ProjectileEntity> entityType, Level worldIn,  WeaponData data) {
        super(entityType, worldIn, data);
    }

    @Override
    protected void onProjectileTick() {
        if (this.level().isClientSide()) {
            for (int i = 5; i > 0; i--) {
                this.level().addParticle(ParticleTypes.CLOUD, true, false, this.getX() - (this.getDeltaMovement().x() / i), this.getY() - (this.getDeltaMovement().y() / i), this.getZ() - (this.getDeltaMovement().z() / i), 0, 0, 0);
            }
            if (this.level().getRandom().nextInt(2) == 0) {
                this.level().addParticle(ParticleTypes.SMOKE, true, false, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.level().addParticle(ParticleTypes.FLAME, true, false, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }
    }
}
