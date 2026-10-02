//package com.nukateam.ntgl.common.base;
//
//import com.nukateam.ntgl.common.util.annotation.Optional;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.Tag;
//import com.nukateam.ntgl.platform.INBTSerializable;
//import net.neoforged.neoforge.fml.util.thread.SidedThreadGroups;
//
//public class Reloads implements INBTSerializable<CompoundTag> {
//    private int maxAmmo = 20;
//    @Optional
//    private boolean magFed = false;
//    @Optional
//    private int reloadMagTimer = 20;
//    @Optional
//    private int additionalReloadEmptyMagTimer = 0;
//    @Optional
//    private int reloadAmount = 1;
//    @Optional
//    private int[] maxAdditionalAmmoPerOC = new int[]{};
//    @Optional
//    private int preReloadPauseTicks = 0;
//    @Optional
//    private int interReloadPauseTicks = 1;
//    @Optional
//    private boolean openBolt = false;
//
//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag tag = new CompoundTag();
//        tag.putInt("MaxAmmo", this.maxAmmo);
//        tag.putBoolean("MagFed", this.magFed);
//        tag.putInt("ReloadSpeed", this.reloadAmount);
//        tag.putInt("ReloadMagTimer", this.reloadMagTimer);
//        tag.putInt("AdditionalReloadEmptyMagTimer", this.additionalReloadEmptyMagTimer);
//        tag.putIntArray("MaxAmmunitionPerOverCap", this.maxAdditionalAmmoPerOC);
//        tag.putInt("ReloadPauseTicks", this.preReloadPauseTicks);
//        tag.putInt("InterReloadPauseTicks", this.interReloadPauseTicks);
//        tag.putBoolean("OpenBolt", this.openBolt);
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
//        if (tag.contains("MaxAmmo")) {
//            this.maxAmmo = tag.getIntOr("MaxAmmo", 0);
//        }
//        if (tag.contains("MagFed")) {
//            this.magFed = tag.getBooleanOr("MagFed", false);
//        }
//        if (tag.contains("ReloadSpeed")) {
//            this.reloadAmount = tag.getIntOr("ReloadSpeed", 0);
//        }
//        if (tag.contains("ReloadMagTimer")) {
//            this.reloadMagTimer = tag.getIntOr("ReloadMagTimer", 0);
//        }
//        if (tag.contains("AdditionalReloadEmptyMagTimer")) {
//            this.additionalReloadEmptyMagTimer = tag.getIntOr("AdditionalReloadEmptyMagTimer", 0);
//        }
//        if (tag.contains("MaxAmmunitionPerOverCap")) {
//            this.maxAdditionalAmmoPerOC = tag.getIntArray("MaxAmmunitionPerOverCap");
//        }
//        if (tag.contains("ReloadPauseTicks")) {
//            this.preReloadPauseTicks = tag.getIntOr("ReloadPauseTicks", 0);
//        }
//        if (tag.contains("InterReloadPauseTicks")) {
//            this.interReloadPauseTicks = tag.getIntOr("InterReloadPauseTicks", 0);
//        }
//        if (tag.contains("OpenBolt")) {
//            this.openBolt = tag.getBooleanOr("OpenBolt", false);
//        }
//    }
//
//    /**
//     * @return A copy of the general get
//     */
//    public Reloads copy() {
//        Reloads reloads = new Reloads();
//        reloads.magFed = this.magFed;
//        reloads.maxAmmo = this.maxAmmo;
//        reloads.reloadAmount = this.reloadAmount;
//        reloads.reloadMagTimer = this.reloadMagTimer;
//        reloads.additionalReloadEmptyMagTimer = this.additionalReloadEmptyMagTimer;
//        reloads.maxAdditionalAmmoPerOC = this.maxAdditionalAmmoPerOC;
//        reloads.preReloadPauseTicks = this.preReloadPauseTicks;
//        reloads.interReloadPauseTicks = this.interReloadPauseTicks;
//        reloads.openBolt = this.openBolt;
//        return reloads;
//    }
//
//    /**
//     * @return Does this gun reload all ammunition following a single timer and replenish
//     */
//    public boolean isMagFed() {
//        return this.magFed;
//    }
//
//    /**
//     * @return The maximum amount of projectile this weapon can hold
//     */
//    public int getMaxAmmo() {
//        return this.maxAmmo;
//    }
//
//    /**
//     * @return The amount of projectile to add to the weapon each reload cycle
//     */
//    public int getReloadAmount() {
//        return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER
//                && Config.COMMON.development.enableTDev.get()
//                && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ?
//                (int) (this.reloadAmount + GunEditor.get().getReloadAmountMod()) : this.reloadAmount;
//    }
//
//    /**
//     * @return The amount of projectile to add to the weapon each reload cycle
//     */
//    public int getReloadMagTimer() {
//        return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER
//                && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ?
//                (int) (this.reloadMagTimer + GunEditor.get().getReloadMagTimerMod()) : this.reloadMagTimer;
//    }
//
//    /**
//     * @return The amount of projectile to add to the weapon each reload cycle
//     */
//    public int getAdditionalReloadEmptyMagTimer() {
//        return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER &&
//                Config.COMMON.development.enableTDev.get() &&
//                GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ?
//                (int) (this.additionalReloadEmptyMagTimer + GunEditor.get().getAdditionalReloadEmptyMagTimerMod()) :
//                this.additionalReloadEmptyMagTimer;
//    }
//
//    /**
//     * @return The amount of projectile to add to the weapon each reload cycle
//     */
//    public int[] getMaxAdditionalAmmoPerOC() {
//        return this.maxAdditionalAmmoPerOC;
//    }
//
//    /**
//     * @return The amount of projectile to add to the weapon each reload cycle
//     */
//    public int getPreReloadPauseTicks() {
//        return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.preReloadPauseTicks + GunEditor.get().getPreReloadPauseTicksMod()) : this.preReloadPauseTicks;
//    }
//
//    /**
//     * @return The amount of projectile to add to the weapon each reload cycle
//     */
//    public int getinterReloadPauseTicks() {
//        return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.interReloadPauseTicks + GunEditor.get().getInterReloadPauseTicksMod()) : this.interReloadPauseTicks;
//    }
//
//    /**
//     * @return Does this gun reload all ammunition following a single timer and replenish
//     */
//    public boolean isOpenBolt() {
//        return this.openBolt;
//    }
//}
