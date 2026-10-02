package com.nukateam.ntgl.common.foundation.entity.misc;

import com.nukateam.ntgl.common.foundation.init.ModEntityTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AshPile extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final int LIFE = 20 * 10;
    public int timeToLive = LIFE;

    public AshPile(EntityType<?> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public AshPile(Level pLevel, Vec3 pos) {
        super(ModEntityTypes.ASH_PILE.get(), pLevel);
        this.setPos(pos);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.timeToLive > 0)
            --timeToLive;
        else this.discard();
    }

    public int getMaxLife(){
        return LIFE;
    }

    public int getLife(){
        return timeToLive;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
