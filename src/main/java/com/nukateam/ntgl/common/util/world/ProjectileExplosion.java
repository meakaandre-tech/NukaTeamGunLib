package com.nukateam.ntgl.common.util.world;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.data.config.weapon.ExplosionConfig;
import com.nukateam.ntgl.common.foundation.ModTags;
import com.nukateam.ntgl.common.util.helpers.compatibility.EffectHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * NTGL's own explosion. In 26.x {@link Explosion} is an interface (the vanilla implementation is
 * ServerExplosion), so this class now keeps its own list of affected blocks and hit players.
 * It is also built on the client (from S2CMessageProjectileExplosion) only to play the effects.
 */
public class ProjectileExplosion implements Explosion {
    private static final ExplosionDamageCalculator DEFAULT_CONTEXT = new ExplosionDamageCalculator();

    private final Level level;
    private final float radius;
    private final Entity exploder;
    private final ExplosionDamageCalculator context;
    private final float damage;
    private final float knockback;
    private final boolean damageDecreaseWithDistance;
    private final boolean causesFire;
    private final RandomSource random = RandomSource.create();
    private final BlockInteraction blockInteraction;
    private final Vec3 pos;
    private final DamageSource damageSource;
    private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList<>();
    private final Map<Player, Vec3> hitPlayers = new HashMap<>();

    public ProjectileExplosion(Level level, Entity exploder,
                               @Nullable DamageSource source,
                               @Nullable ExplosionDamageCalculator context, ExplosionConfig projectile,
                               Vec3 pos, BlockInteraction mode) {
        this.level = level;
        this.causesFire = projectile.isCauseFire();
        this.blockInteraction = mode;
        this.pos = pos;
        this.radius = projectile.getRadius();
        this.exploder = exploder;
        this.context = context == null ? DEFAULT_CONTEXT : context;
        this.damage = projectile.getDamage();
        this.damageSource = source != null || level.isClientSide() ? source : Explosion.getDefaultDamageSource(level, exploder);
        this.knockback = projectile.getKnockback();
        this.damageDecreaseWithDistance = projectile.isDamageReduceOverDistance();
    }

    public ProjectileExplosion(Level level, Entity exploder, ExplosionConfig projectile,
                               Vec3 pos, BlockInteraction mode, List<BlockPos> toBlow) {
        this(level, exploder, null, null, projectile, pos, mode);
        this.getToBlow().addAll(toBlow);
    }

    public List<BlockPos> getToBlow() {
        return toBlow;
    }

    public void clearToBlow() {
        toBlow.clear();
    }

    public Map<Player, Vec3> getHitPlayers() {
        return hitPlayers;
    }

    public boolean interactsWithBlocks() {
        return this.blockInteraction != BlockInteraction.KEEP;
    }

    @Override
    public ServerLevel level() {
        return (ServerLevel) this.level;
    }

    @Override
    public BlockInteraction getBlockInteraction() {
        return this.blockInteraction;
    }

    @Override
    public @Nullable LivingEntity getIndirectSourceEntity() {
        return Explosion.getIndirectSourceEntity(this.exploder);
    }

    @Override
    public @Nullable Entity getDirectSourceEntity() {
        return this.exploder;
    }

    @Override
    public float radius() {
        return this.radius;
    }

    @Override
    public Vec3 center() {
        return this.pos;
    }

    @Override
    public boolean canTriggerBlocks() {
        return false;
    }

    @Override
    public boolean shouldAffectBlocklikeEntities() {
        return this.blockInteraction != BlockInteraction.KEEP;
    }

    public void explode() {
        destroyBlocks();
        this.level.gameEvent(this.exploder, GameEvent.EXPLODE, new Vec3(this.pos.x, this.pos.y, this.pos.z));

        var diameter = this.radius * 2.0D;
        int minX = Mth.floor(this.pos.x - diameter - 1.0D);
        int maxX = Mth.floor(this.pos.x + diameter + 1.0D);
        int minY = Mth.floor(this.pos.y - diameter - 1.0D);
        int maxY = Mth.floor(this.pos.y + diameter + 1.0D);
        int minZ = Mth.floor(this.pos.z - diameter - 1.0D);
        int maxZ = Mth.floor(this.pos.z + diameter + 1.0D);

        var entities = this.level.getEntities(null, new AABB(minX, minY, minZ, maxX, maxY, maxZ));

        for (var entity : entities) {
            if (entity.ignoreExplosion(this))
                continue;

            var strength = Math.sqrt(entity.distanceToSqr(pos)) / diameter;
            if (strength > 1.0D)
                continue;

            var deltaX = entity.getX() - pos.x;
            var deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - pos.y;
            var deltaZ = entity.getZ() - pos.z;
            var distanceToExplosion = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if (distanceToExplosion != 0.0D) {
                deltaX /= distanceToExplosion;
                deltaY /= distanceToExplosion;
                deltaZ /= distanceToExplosion;
            } else {
                deltaX = 0.0;
                deltaY = 1.0;
                deltaZ = 0.0;
            }

            var blockDensity = (double) ServerExplosion.getSeenPercent(pos, entity);
            var knockback = (1.0D - strength) * blockDensity * this.knockback;
            float finalDamage = this.damage;

            if(this.damageDecreaseWithDistance){
                finalDamage *= 1.0D - strength;
            }

            entity.hurt(this.damageSource, finalDamage);

            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * knockback, deltaY * knockback, deltaZ * knockback));

            if (entity instanceof Player player) {
                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    this.getHitPlayers().put(player, new Vec3(deltaX * knockback, deltaY * knockback, deltaZ * knockback));
                }
            }
        }
    }

    public void finalizeExplosion(boolean spawnParticles) {
        if (this.level.isClientSide()) {
            this.level.playLocalSound(pos.x, pos.y, pos.z,
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F,
                    (1.0F + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.2F) * 0.7F,
                    false);
        }

        var interactsWithBlocks = this.interactsWithBlocks();
        var toBlow = this.toBlow;

        if (spawnParticles) {
            EffectHelper.doExplosionSplash(level, radius, pos);

            if (!(this.radius < 2.0F) && interactsWithBlocks) {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1.0D, 0.0D, 0.0D);
            } else {
                this.level.addParticle(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 1.0D, 0.0D, 0.0D);
            }
        }

        var canBrakeGlass = Config.COMMON.gameplay.griefing.enableGlassBreaking.get();

        if ((interactsWithBlocks || canBrakeGlass) && this.level instanceof ServerLevel serverLevel) {
            var blockDrops = new ObjectArrayList<Pair<ItemStack, BlockPos>>();
            Util.shuffle(toBlow, this.level.getRandom());

            for(BlockPos blockpos : toBlow) {
                var blockState = this.level.getBlockState(blockpos);

                if (!blockState.isAir() && ((canBrakeGlass && blockState.is(ModTags.Blocks.FRAGILE)) || interactsWithBlocks)) {
                    var immutableBLockPos = blockpos.immutable();
                    // drops, block removal and the block's own reaction, as vanilla's ServerExplosion does
                    blockState.onExplosionHit(serverLevel, immutableBLockPos, this,
                            (stack, dropPos) -> addBlockDrops(blockDrops, stack, dropPos));
                }
            }

            for(Pair<ItemStack, BlockPos> pair : blockDrops) {
                Block.popResource(this.level, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.causesFire) {
            for(BlockPos blockpos2 : toBlow) {
                if (this.random.nextInt(2) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender()) {
                    this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
                }
            }
        }
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray,
                                      ItemStack pStack, BlockPos pPos) {
        int i = pDropPositionArray.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, pStack)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, pStack, 16);
                pDropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (pStack.isEmpty()) {
                    return;
                }
            }
        }

        pDropPositionArray.add(Pair.of(pStack, pPos));
    }

    private void destroyBlocks() {
        var set = Sets.<BlockPos>newHashSet();
        collectExplodedBlocks(set, this.pos);
        this.getToBlow().addAll(set);
    }

    private void collectExplodedBlocks(Set<BlockPos> set, Vec3 origin) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
                        var d0 = (double) ((float) x / 15.0F * 2.0F - 1.0F);
                        var d1 = (double) ((float) y / 15.0F * 2.0F - 1.0F);
                        var d2 = (double) ((float) z / 15.0F * 2.0F - 1.0F);
                        var d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        var f = this.radius * (0.7F + this.level.getRandom().nextFloat() * 0.6F);
                        var blockX = origin.x;
                        var blockY = origin.y;
                        var blockZ = origin.z;

                        for (; f > 0.0F; f -= 0.225F) {
                            var pos = BlockPos.containing(blockX, blockY, blockZ);
                            var blockState = this.level.getBlockState(pos);
                            var fluidState = this.level.getFluidState(pos);
                            var optional = this.context.getBlockExplosionResistance(this, this.level, pos, blockState, fluidState);

                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.context.shouldBlockExplode(this, this.level, pos, blockState, f)) {
                                set.add(pos);
                            }

                            blockX += d0 * (double) 0.3F;
                            blockY += d1 * (double) 0.3F;
                            blockZ += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }
    }
}