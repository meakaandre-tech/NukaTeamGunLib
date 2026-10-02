package com.nukateam.ntgl.common.data.config.attachment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.data.holders.FireMode;
import com.nukateam.ntgl.common.data.holders.AmmoHolder;
import com.nukateam.ntgl.common.data.holders.GripType;
import com.nukateam.ntgl.common.data.holders.LoadingType;
import com.nukateam.ntgl.common.util.annotation.Optional;
import com.nukateam.ntgl.common.util.interfaces.IWeaponModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import com.nukateam.ntgl.platform.INBTSerializable;
import net.minecraft.core.HolderLookup;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Modifiers implements INBTSerializable<CompoundTag>, IWeaponModifier {
    @Optional Set<FireMode> fireModes = new HashSet<>();
    @Optional float additionalDamage = 0;
    @Optional String damage = "";
    @Optional String projectileSpeed = "";
    @Optional String spread = "";
    @Optional float additionalProjectileGravity = 0;
    @Optional String projectileGravity = "";
    @Optional String projectileLife = "";
    @Optional float recoilModifier = 1;
    @Optional float kickModifier = 1;
    @Optional float criticalChance = 0;
    @Optional String muzzleFlashSize = "";
    @Optional String muzzleFlashScale = "";
    @Optional String aimDownSightSpeed = "";
    @Optional String rate = "";
    @Optional String maxAmmo = "";
    @Optional String projectileAmount = "";
    @Optional String fireDelay = "";
    @Optional String reloadStart = "";
    @Optional String reloadTime = "";
    @Optional String reloadEnd = "";
    @Optional String equipTime = "";
    @Optional String ammoPerShot = "";
    @Optional String fireSoundVolume = "";
    @Optional String fireSound = "";
    @Optional String silencedFire = "";
    @Optional HashMap<AmmoHolder, Integer> maxFuel = new HashMap<>();
    @Optional
    GripType gripType = null;
    @Optional String needsFullCharge = "";
    @Optional String oneTimeCharge = "";
    @Optional Set<AmmoHolder> ammoItems = new HashSet<>();
    @Optional String autoReload = "";
    @Optional String renderHud = "";
    @Optional
    LoadingType loadingType = null;
    @Optional Set<AmmoHolder> fuel = new HashSet<>();

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        // Existing serialization
        tag.putString("fireSoundVolume", this.fireSoundVolume);
        tag.putString("fireSound", this.fireSound);
        tag.putString("silencedFire", this.silencedFire);

        // Numeric fields
        tag.putFloat("additionalDamage", this.additionalDamage);
        tag.putString("damage", this.damage);
        tag.putString("projectileSpeed", this.projectileSpeed);
        tag.putString("spread", this.spread);
        tag.putFloat("additionalProjectileGravity", this.additionalProjectileGravity);
        tag.putString("projectileGravity", this.projectileGravity);
        tag.putString("projectileLife", this.projectileLife);
        tag.putFloat("recoilModifier", this.recoilModifier);
        tag.putFloat("kickModifier", this.kickModifier);
        tag.putString("muzzleFlashSize", this.muzzleFlashSize);
        tag.putString("muzzleFlashScale", this.muzzleFlashScale);
        tag.putString("aimDownSightSpeed", this.aimDownSightSpeed);
        tag.putString("rate", this.rate);
        tag.putFloat("criticalChance", this.criticalChance);
        tag.putString("maxAmmo", this.maxAmmo);
        tag.putString("projectileAmount", this.projectileAmount);
        tag.putString("fireDelay", this.fireDelay);
        tag.putString("reloadStart", this.reloadStart);
        tag.putString("reloadTime", this.reloadTime);
        tag.putString("reloadEnd", this.reloadEnd);
        tag.putString("equipTime", this.equipTime);
        tag.putString("ammoPerShot", this.ammoPerShot);

        // Collections and enums
        writeFireModes(tag);
        writeGripType(tag);
        tag.putString("needsFullCharge", this.needsFullCharge);
        tag.putString("oneTimeCharge", this.oneTimeCharge);
        writeAmmoItems(tag);
        tag.putString("autoReload", this.autoReload);
        tag.putString("renderHud", this.renderHud);
        writeLoadingType(tag);
        writeFuelTypes(tag);
        writeFuelMax(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        // Existing deserialization
        if (tag.contains("fireSoundVolume")) this.fireSoundVolume = tag.getStringOr("fireSoundVolume", "");
        if (tag.contains("fireSound")) this.fireSound = tag.getStringOr("fireSound", "");
        if (tag.contains("silencedFire")) this.silencedFire = tag.getStringOr("silencedFire", "");

        // Numeric fields
        if (tag.contains("additionalDamage")) this.additionalDamage = tag.getFloatOr("additionalDamage", 0F);
        if (tag.contains("damage")) this.damage = tag.getStringOr("damage", "");
        if (tag.contains("projectileSpeed")) this.projectileSpeed = tag.getStringOr("projectileSpeed", "");
        if (tag.contains("spread")) this.spread = tag.getStringOr("spread", "");
        if (tag.contains("additionalProjectileGravity")) this.additionalProjectileGravity = tag.getFloatOr("additionalProjectileGravity", 0F);
        if (tag.contains("projectileGravity")) this.projectileGravity = tag.getStringOr("projectileGravity", "");
        if (tag.contains("projectileLife")) this.projectileLife = tag.getStringOr("projectileLife", "");
        if (tag.contains("recoilModifier")) this.recoilModifier = tag.getFloatOr("recoilModifier", 0F);
        if (tag.contains("kickModifier")) this.kickModifier = tag.getFloatOr("kickModifier", 0F);
        if (tag.contains("muzzleFlashSize")) this.muzzleFlashSize = tag.getStringOr("muzzleFlashSize", "");
        if (tag.contains("muzzleFlashScale")) this.muzzleFlashScale = tag.getStringOr("muzzleFlashScale", "");
        if (tag.contains("aimDownSightSpeed")) this.aimDownSightSpeed = tag.getStringOr("aimDownSightSpeed", "");
        if (tag.contains("rate")) this.rate = tag.getStringOr("rate", "");
        if (tag.contains("criticalChance")) this.criticalChance = tag.getFloatOr("criticalChance", 0F);
        if (tag.contains("maxAmmo")) this.maxAmmo = tag.getStringOr("maxAmmo", "");
        if (tag.contains("projectileAmount")) this.projectileAmount = tag.getStringOr("projectileAmount", "");
        if (tag.contains("fireDelay")) this.fireDelay = tag.getStringOr("fireDelay", "");
        if (tag.contains("reloadStart")) this.reloadStart = tag.getStringOr("reloadStart", "");
        if (tag.contains("reloadTime")) this.reloadTime = tag.getStringOr("reloadTime", "");
        if (tag.contains("reloadEnd")) this.reloadEnd = tag.getStringOr("reloadEnd", "");
        if (tag.contains("equipTime")) this.equipTime = tag.getStringOr("equipTime", "");
        if (tag.contains("ammoPerShot")) this.ammoPerShot = tag.getStringOr("ammoPerShot", "");

        // Collections and enums
        readFireModes(tag);
        readGripType(tag);
        if (tag.contains("needsFullCharge")) this.needsFullCharge = tag.getStringOr("needsFullCharge", "");
        if (tag.contains("oneTimeCharge")) this.oneTimeCharge = tag.getStringOr("oneTimeCharge", "");
        readAmmoItems(tag);
        if (tag.contains("autoReload")) this.autoReload = tag.getStringOr("autoReload", "");
        if (tag.contains("renderHud")) this.renderHud = tag.getStringOr("renderHud", "");
        readLoadingType(tag);
        readFuelTypes(tag);
        readFuelMax(tag);
    }

    // Helper methods for collections/enums
    private void writeFireModes(CompoundTag tag) {
        ListTag list = new ListTag();
        this.fireModes.forEach(mode -> list.add(StringTag.valueOf(mode.toString())));
        tag.put("fireModes", list);
    }

    private void readFireModes(CompoundTag tag) {
        if (tag.contains("fireModes")) {
            this.fireModes.clear();
            tag.getListOrEmpty("fireModes").forEach(t ->
                    this.fireModes.add(FireMode.getType(t.getAsString()))
            );
        }
    }

    private void writeGripType(CompoundTag tag) {
        if (this.gripType != null) {
            tag.putString("gripType", this.gripType.toString());
        }
    }

    private void readGripType(CompoundTag tag) {
        if (tag.contains("gripType")) {
            this.gripType = GripType.getType(tag.getStringOr("gripType", ""));
        }
    }

    private void writeAmmoItems(CompoundTag tag) {
        ListTag list = new ListTag();
        this.ammoItems.forEach(item -> list.add(StringTag.valueOf(item.toString())));
        tag.put("ammoItems", list);
    }

    private void readAmmoItems(CompoundTag tag) {
        if (tag.contains("ammoItems")) {
            this.ammoItems.clear();
            tag.getListOrEmpty("ammoItems").forEach(t ->
                    this.ammoItems.add(AmmoHolder.getType(t.getAsString()))
            );
        }
    }

    private void writeLoadingType(CompoundTag tag) {
        if (this.loadingType != null) {
            tag.putString("loadingType", this.loadingType.toString());
        }
    }

    private void readLoadingType(CompoundTag tag) {
        if (tag.contains("loadingType")) {
            this.loadingType = LoadingType.getType(tag.getStringOr("loadingType", ""));
        }
    }

    private void writeFuelTypes(CompoundTag tag) {
        ListTag list = new ListTag();
        this.fuel.forEach(type -> list.add(StringTag.valueOf(type.toString())));
        tag.put("fuel", list);
    }

    private void readFuelTypes(CompoundTag tag) {
        if (tag.contains("fuel")) {
            this.fuel.clear();
            tag.getListOrEmpty("fuel").forEach(t ->
                    this.fuel.add(AmmoHolder.getType(t.getAsString()))
            );
        }
    }

    private void writeFuelMax(CompoundTag tag) {
        CompoundTag fuelTag = new CompoundTag();
        this.maxFuel.forEach((key, value) -> fuelTag.putInt(key.toString(), value));
        tag.put("maxFuel", fuelTag);
    }

    // В методе deserializeNBT
    private void readFuelMax(CompoundTag tag) {
        if (tag.contains("maxFuel")) {
            CompoundTag fuelTag = tag.getCompoundOrEmpty("maxFuel");
            fuelTag.keySet().forEach(key ->
                    this.maxFuel.put(
                            AmmoHolder.getType(key),
                            fuelTag.getIntOr(key, 0)
                    )
            );
        }
    }

    // Copy and JSON methods
    public Modifiers copy() {
        Modifiers copy = new Modifiers();
        // Existing fields
        copy.fireSoundVolume = this.fireSoundVolume;
        copy.fireSound = this.fireSound;
        copy.silencedFire = this.silencedFire;

        // Numeric fields
        copy.additionalDamage = this.additionalDamage;
        copy.damage = this.damage;
        copy.projectileSpeed = this.projectileSpeed;
        copy.spread = this.spread;
        copy.additionalProjectileGravity = this.additionalProjectileGravity;
        copy.projectileGravity = this.projectileGravity;
        copy.projectileLife = this.projectileLife;
        copy.recoilModifier = this.recoilModifier;
        copy.kickModifier = this.kickModifier;
        copy.muzzleFlashSize = this.muzzleFlashSize;
        copy.muzzleFlashScale = this.muzzleFlashScale;
        copy.aimDownSightSpeed = this.aimDownSightSpeed;
        copy.rate = this.rate;
        copy.criticalChance = this.criticalChance;
        copy.maxAmmo = this.maxAmmo;
        copy.projectileAmount = this.projectileAmount;
        copy.fireDelay = this.fireDelay;
        copy.reloadStart = this.reloadStart;
        copy.reloadTime = this.reloadTime;
        copy.reloadEnd = this.reloadEnd;
        copy.equipTime = this.equipTime;
        copy.ammoPerShot = this.ammoPerShot;
        copy.maxFuel = new HashMap<>(this.maxFuel);

        // Collections and enums
        copy.fireModes = new HashSet<>(this.fireModes);
        copy.gripType = this.gripType;
        copy.needsFullCharge = this.needsFullCharge;
        copy.oneTimeCharge = this.oneTimeCharge;
        copy.ammoItems = new HashSet<>(this.ammoItems);
        copy.autoReload = this.autoReload;
        copy.renderHud = this.renderHud;
        copy.loadingType = this.loadingType;
        copy.fuel = new HashSet<>(this.fuel);

        return copy;
    }

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        // Existing fields
        json.addProperty("fireSoundVolume", this.fireSoundVolume);
        json.addProperty("fireSound", this.fireSound);
        json.addProperty("silencedFire", this.silencedFire);

        // Numeric fields
        json.addProperty("additionalDamage", this.additionalDamage);
        json.addProperty("damage", this.damage);
        json.addProperty("projectileSpeed", this.projectileSpeed);
        json.addProperty("spread", this.spread);
        json.addProperty("additionalProjectileGravity", this.additionalProjectileGravity);
        json.addProperty("projectileGravity", this.projectileGravity);
        json.addProperty("projectileLife", this.projectileLife);
        json.addProperty("recoilModifier", this.recoilModifier);
        json.addProperty("kickModifier", this.kickModifier);
        json.addProperty("muzzleFlashSize", this.muzzleFlashSize);
        json.addProperty("muzzleFlashScale", this.muzzleFlashScale);
        json.addProperty("aimDownSightSpeed", this.aimDownSightSpeed);
        json.addProperty("rate", this.rate);
        json.addProperty("criticalChance", this.criticalChance);
        json.addProperty("maxAmmo", this.maxAmmo);
        json.addProperty("projectileAmount", this.projectileAmount);
        json.addProperty("fireDelay", this.fireDelay);
        json.addProperty("reloadStart", this.reloadStart);
        json.addProperty("reloadTime", this.reloadTime);
        json.addProperty("reloadEnd", this.reloadEnd);
        json.addProperty("equipTime", this.equipTime);
        json.addProperty("ammoPerShot", this.ammoPerShot);
        JsonObject fuelJson = new JsonObject();
        this.maxFuel.forEach((key, value) ->
                fuelJson.addProperty(key.toString(), value)
        );

        json.add("maxFuel", fuelJson);
        var fireModesArray = new JsonArray();
        this.fireModes.forEach(mode -> fireModesArray.add(mode.toString()));
        json.add("fireModes", fireModesArray);

        return json;
    }

    @Override
    public float modifyFireSoundVolume(float volume, WeaponData data) {
        return IWeaponModifier.super.modifyFireSoundVolume(volume, data);
    }

    @Override
    public Identifier modifySound(String name, Identifier sound, WeaponData data) {
        return IWeaponModifier.super.modifySound(name, sound, data);
    }

    @Override
    public double modifyFireSoundRadius(double radius, WeaponData data) {
        return IWeaponModifier.super.modifyFireSoundRadius(radius, data);
    }

    @Override
    public boolean silencedFire(boolean value, WeaponData data) {
        return getBoolean(value, silencedFire);
    }

    @Override
    public boolean modifyNeedsFullCharge(boolean base, WeaponData data) {
        return getBoolean(base, this.needsFullCharge);
    }

    @Override
    public boolean modifyIsOneTimeCharge(boolean base, WeaponData data) {
        return getBoolean(base, oneTimeCharge);
    }

    @Override
    public boolean modifyShouldRenderHud(boolean base, WeaponData data) {
        return getBoolean(base, renderHud);
    }

    @Override
    public Set<AmmoHolder> modifyAmmoItems(Set<AmmoHolder> baseValue, WeaponData data) {
        return ammoItems != null && !ammoItems.isEmpty() ? this.ammoItems : baseValue;
    }

    @Override
    public boolean modifyAutoReloading(boolean base, WeaponData data) {
        return getBoolean(base, autoReload);
    }

    @Override
    public LoadingType modifyLoadingType(LoadingType baseValue, WeaponData data) {
        return loadingType != null ? this.loadingType : baseValue;
    }

    @Override
    public Set<AmmoHolder> modifyFuelItems(Set<AmmoHolder> baseValue, WeaponData data) {
        return fuel != null && !fuel.isEmpty() ? this.fuel : baseValue;
    }

    @Override
    public Set<FireMode> modifyFireModes(Set<FireMode> baseValue, WeaponData data) {
        return fireModes != null && !fireModes.isEmpty() ? this.fireModes : baseValue;
    }

    @Override
    public GripType modifyGripType(GripType baseValue, WeaponData data) {
        return this.gripType != null ? this.gripType :  baseValue;
    }

    // Calculation methods for each numeric property
    public float additionalDamage(WeaponData weaponData) {
        return additionalDamage;
    }

    public float modifyDamage(float damage, WeaponData weaponData) {
        return calculate(damage, this.damage);
    }

    public double modifyProjectileSpeed(double speed, WeaponData weaponData) {
        return calculate((float) speed, projectileSpeed);
    }

    public float modifyProjectileSpread(float spread, WeaponData weaponData) {
        return calculate(spread, this.spread);
    }

    public double additionalProjectileGravity(WeaponData weaponData) {
        return additionalProjectileGravity;
    }

    public double modifyProjectileGravity(double gravity, WeaponData weaponData) {
        return calculate((float) gravity, projectileGravity);
    }

    public int modifyProjectileLife(int life, WeaponData weaponData) {
        return (int) calculate(life, projectileLife);
    }

    public float recoilModifier(WeaponData weaponData) {
        return recoilModifier;
    }

    public float kickModifier(WeaponData weaponData) {
        return kickModifier;
    }

    public double modifyMuzzleFlashSize(double size, WeaponData weaponData) {
        return calculate((float) size, muzzleFlashSize);
    }

    public double modifyMuzzleFlashScale(double scale, WeaponData weaponData) {
        return calculate((float) scale, muzzleFlashScale);
    }

    public double modifyAimDownSightSpeed(double speed, WeaponData weaponData) {
        return calculate((float) speed, aimDownSightSpeed);
    }

    public int modifyFireRate(int base, WeaponData weaponData) {
        return (int) calculate(base, this.rate);
    }

    public float criticalChance(WeaponData weaponData) {
        return criticalChance;
    }

    public int modifyMaxAmmo(int maxAmmo, WeaponData weaponData) {
        return (int) calculate(maxAmmo, this.maxAmmo);
    }

    public int modifyProjectileAmount(int amount, WeaponData weaponData) {
        return (int) calculate(amount, projectileAmount);
    }

    public int modifyFireDelay(int delay, WeaponData weaponData) {
        return (int) calculate(delay, fireDelay);
    }

    public int modifyReloadStart(int time, WeaponData weaponData) {
        return (int) calculate(time, reloadStart);
    }

    public int modifyReloadTime(int time, WeaponData weaponData) {
        return (int) calculate(time, reloadTime);
    }

    public int modifyReloadEnd(int time, WeaponData weaponData) {
        return (int) calculate(time, reloadEnd);
    }

    public int modifyEquipTime(int time, WeaponData weaponData) {
        return (int) calculate(time, equipTime);
    }

    public int modifyAmmoPerShot(int ammo, WeaponData weaponData) {
        return (int) calculate(ammo, ammoPerShot);
    }

    public int modifyMaxFuel(int max, AmmoHolder type, WeaponData weaponData) {
        return maxFuel != null && maxFuel.get(type) != null ? maxFuel.get(type) : max;
    }

    public static boolean getBoolean(boolean base, String mod) {
        if (mod == null || mod.isEmpty()) {
            return base;
        }

        switch (mod){
            case "true" -> {
                return true;
            }
            case "false" -> {
                return false;
            }
            default -> {
                Ntgl.LOGGER.error("Invalid boolean  {}", mod);
                return base;
            }
        }
    }

    public static float calculate(float num, String operation) {
        if (operation == null || operation.isEmpty()) {
            return num;
        }

        try {
            char firstChar = operation.charAt(0);
            if (isOperator(firstChar)) {
                float operand = parseOperand(operation.substring(1));
                return applyOperation(num, firstChar, operand);
            } else {
                return parseOperand(operation);
            }
        } catch (NumberFormatException e) {
            var throwable = new IllegalArgumentException("Invalid number format in operation: " + operation, e);
            Ntgl.LOGGER.error("Invalid number format in operation: {}", operation, throwable);
            return num;
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static float parseOperand(String numberStr) {
        return Float.parseFloat(numberStr);
    }

    private static float applyOperation(float num, char operator, float operand) {
        return switch (operator) {
            case '+' -> num + operand;
            case '-' -> num - operand;
            case '*' -> num * operand;
            case '/' -> {
                if (operand == 0.0) {
                    Ntgl.LOGGER.error("Division by zero", new ArithmeticException("Division by zero"));
                    yield num;
                }
                yield num / operand;
            }
            default -> {
                Ntgl.LOGGER.error("Unsupported operator: {}", operator, new IllegalArgumentException("Unsupported operator: " + operator));
                yield num / operand;
            }
        };
    }
}
