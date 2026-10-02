package com.nukateam.ntgl.common.data.config.weapon;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.nukateam.ntgl.common.data.holders.MeleeMode;
import com.nukateam.ntgl.common.debug.IDebugWidget;
import com.nukateam.ntgl.common.debug.IEditorMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.nukateam.ntgl.platform.INBTSerializable;
import net.minecraft.core.HolderLookup;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Supplier;

public class Melee implements INBTSerializable<CompoundTag> {
    public static final String DAMAGE = "damage";
    public static final String MODE = "mode";
    public static final String COOLDOWN = "cooldown";
    public static final String DELAY = "delay";
    public static final String DISTANCE = "distance";
    public static final String ATTACK_RADIUS = "attackRadius";
    public static final String KNOCKBACK = "knockback";
    public static final String MAX_TARGETS = "maxTargets";

    private MeleeMode mode = MeleeMode.SINGLE;
    private float damage = 1;
    private int cooldown = 0;
    private int delay = 0;
    private float distance = 1;
    private float knockback = 0;
    private int maxTargets = 0;
    private float angle = 10;

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        tag.putFloat(DAMAGE, this.damage);
        tag.putString(MODE, this.mode.toString());
        tag.putInt(COOLDOWN, this.cooldown);
        tag.putInt(DELAY, this.delay);
        tag.putFloat(DISTANCE, this.distance);
        tag.putFloat(ATTACK_RADIUS, this.angle);
        tag.putFloat(KNOCKBACK, this.knockback);
        tag.putInt(MAX_TARGETS, this.maxTargets);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains(MODE)) {
            this.mode = MeleeMode.getType(tag.getStringOr(MODE, ""));
        }
        if (tag.contains(DAMAGE)) {
            this.damage = tag.getFloatOr(DAMAGE, 0F);
        }
        if (tag.contains(COOLDOWN)) {
            this.cooldown = tag.getIntOr(COOLDOWN, 0);
        }
        if (tag.contains(DELAY)) {
            this.delay = tag.getIntOr(DELAY, 0);
        }
        if (tag.contains(DISTANCE)) {
            this.distance = tag.getFloatOr(DISTANCE, 0F);
        }
        if (tag.contains(ATTACK_RADIUS)) {
            this.angle = tag.getFloatOr(ATTACK_RADIUS, 0F);
        }
        if (tag.contains(KNOCKBACK)) {
            this.knockback = tag.getFloatOr(KNOCKBACK, 0F);
        }
        if (tag.contains(MAX_TARGETS)) {
            this.maxTargets = tag.getIntOr(MAX_TARGETS, 0);
        }
    }

    public JsonObject toJsonObject() {
        Preconditions.checkArgument(this.damage >= 0.0F, "Damage must be more than or equal to zero");
        var object = new JsonObject();
        object.addProperty(DAMAGE, this.damage);
        object.addProperty(MODE, this.mode.toString());
        object.addProperty(COOLDOWN, this.cooldown);
        object.addProperty(DELAY, this.delay);
        object.addProperty(DISTANCE, this.distance);
        object.addProperty(KNOCKBACK, this.knockback);
        object.addProperty(MAX_TARGETS, this.maxTargets);
        object.addProperty(ATTACK_RADIUS, this.angle);
        return object;
    }

    public Melee copy() {
        var projectile = new Melee();
        projectile.damage = this.damage;
        projectile.mode = this.mode;
        projectile.cooldown = this.cooldown;
        projectile.delay = this.delay;
        projectile.distance = this.distance;
        projectile.knockback = this.knockback;
        projectile.maxTargets = this.maxTargets;
        projectile.angle = this.angle;
        return projectile;
    }

    /**
     * @return The damage caused by melee attack
     */
    public float getDamage() {
        return this.damage;
    }

    public MeleeMode getMode() {
        return mode;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getDelay() {
        return delay;
    }

    public float getDistance() {
        return distance;
    }

    public float getKnockback() {
        return knockback;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public float getAngle() {
        return angle;
    }

    public static Melee create(CompoundTag tag) {
        var general = new Melee();
        general.deserializeNBT(null,tag);
        return general;
    }

    public static class Builder {
        private final Melee projectile;

        private Builder() {
            this.projectile = new Melee();
        }

        private Builder(Melee projectile) {
            this.projectile = projectile.copy();
        }

        public static Melee.Builder create() {
            return new Melee.Builder();
        }

        public static Melee.Builder create(Melee projectile) {
            return new Melee.Builder(projectile);
        }

        public Melee build() {
            return this.projectile.copy(); //Copy since the builder could be used again
        }

        public Melee.Builder setDamage(Identifier id, float damage) {
            this.projectile.damage = damage;
            return this;
        }

        public Melee.Builder setDistance(float distance) {
            this.projectile.distance = distance;
            return this;
        }

        public Melee.Builder setKnockback(float knockback) {
            this.projectile.knockback = knockback;
            return this;
        }

        public Melee.Builder setMaxTargets(int maxTargets) {
            this.projectile.maxTargets = maxTargets;
            return this;
        }

        public Melee.Builder setAttackRadius(float attackRadius) {
            this.projectile.angle = attackRadius;
            return this;
        }
    }
}
