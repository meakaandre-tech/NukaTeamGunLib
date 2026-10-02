package com.nukateam.ntgl.common.foundation.entity;

import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.util.world.ExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ArrowLikeProjectile extends ProjectileEntity {
    private boolean inGround;
    private BlockPos inBlockPos;
    private int shakeTime;

    public ArrowLikeProjectile(EntityType<? extends ProjectileEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public ArrowLikeProjectile(EntityType<? extends ProjectileEntity> entityType, Level worldIn, WeaponData data) {
        super(entityType, worldIn, data);
    }

    @Override
    protected void saveNbt(CompoundTag compound) {
        super.saveNbt(compound);
        compound.putBoolean("inGround", this.inGround);
        if (this.inBlockPos != null) {
            compound.store("inBlock", net.minecraft.core.BlockPos.CODEC, inBlockPos);
        }
        compound.putInt("shakeTime", this.shakeTime);
    }

    @Override
    protected void loadNbt(CompoundTag compound) {
        super.loadNbt(compound);
        this.inGround = compound.getBooleanOr("inGround", false);
        if (compound.contains("inBlock")) {
            compound.read("inBlock", net.minecraft.core.BlockPos.CODEC).ifPresent(pos -> this.inBlockPos = pos);
        }
        this.shakeTime = compound.getIntOr("shakeTime", 0);
    }

    @Override
    protected void travel() {
        if (this.inGround) {
            this.applyEffectsFromBlocks();

            if (this.shakeTime > 0) {
                this.shakeTime--;
            }
        } else {
            super.travel();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult, BlockState blockState) {
        if (!this.inGround) {
            this.inGround = true;
            this.inBlockPos = hitResult.getBlockPos().immutable();
            this.shakeTime = 20;
            this.needsSync = false;
            this.setDeltaMovement(Vec3.ZERO);
            this.setPos(hitResult.getLocation());
        }

        super.onHitBlock(hitResult, blockState);
    }

    @Override
    protected boolean removeOnHit(HitTarget hitTarget) {
        return hitTarget == HitTarget.ENTITY;
    }

    @Override
    protected void onExpired() {
        if (!this.inGround && ExplosionUtils.isExplosive(getProjectile().getExplosion())) {
            ExplosionUtils.createExplosion(this, getProjectile().getExplosion(), position());
        }
    }

    @Override
    public void playerTouch(Player player) {
        if ((this.level().isClientSide() || this.inGround && this.shakeTime <= 0) && this.canBePickedUp(player)) {
            this.pickup(player);
        }
    }

    private boolean canBePickedUp(Player player) {
        return !this.isRemoved() && (player.getInventory().add(this.getPickupItem()) || player.getAbilities().instabuild);
    }

    protected ItemStack getPickupItem() {
        return this.getItem().copy();
    }

    private void pickup(Player player) {
        if (player.getAbilities().instabuild) {
            this.discard();
        } else {
            player.take(this, 1);
            this.discard();
        }

        this.playSound(SoundEvents.ITEM_PICKUP, 0.2F, 1.0F);
    }
}
