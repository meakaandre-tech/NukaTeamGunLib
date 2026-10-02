package com.nukateam.ntgl.common.data.config.weapon;

import com.google.gson.Gson;
import com.nukateam.ntgl.common.data.holders.*;

import com.nukateam.ntgl.common.util.annotation.Optional;
import com.nukateam.ntgl.common.util.util.*;
import com.nukateam.ntgl.common.foundation.init.ModSounds;
import com.nukateam.ntgl.common.network.PacketHandler;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageGunSound;
import com.nukateam.ntgl.common.data.attachment.IAttachment;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.nukateam.ntgl.platform.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;

import static com.nukateam.ntgl.client.handlers.ClientHandler.*;

public class WeaponConfig implements INBTSerializable<CompoundTag> {
    public static final String GENERAL = "General";
    public static final String MELEE = "Melee";
    public static final String THROWABLE = "Throwable";
    public static final String SOUNDS = "Sounds";
    public static final String DISPLAY = "Display";
    public static final String MODULES = "Modules";
    public static final String TEXTURES = "Textures";
    public static final String ANIMATIONS = "Animations";
    public static final String AMMO_DATA = "AmmoData";
    public static final String SECONDARY_AMMO = "SecondaryAmmo";
    public static final String MODES = "Modes";
    protected General general = getWeapon();
    protected Melee melee = new Melee();
    protected ThrowableConfig throwable = new ThrowableConfig();
    protected HashMap<WeaponMode, WeaponSettings> modes = new HashMap<>(Map.of());
    protected Modules modules = new Modules();
    @Optional
    protected Zoom zoom = new Zoom();
    protected HashMap<AnimationType, Identifier> animations = new HashMap<>();
    protected LinkedHashMap<Identifier, AmmoData> ammoData = new LinkedHashMap<>();
    protected LinkedHashMap<Identifier, Fuel> fuel = new LinkedHashMap<>();
    protected HashMap<String, Identifier> sounds = new HashMap<>();
    protected HashMap<String, Identifier> textures = new HashMap<>();
    protected Display display = new Display();

    private static General getWeapon(){
        var gun = new General();
        gun.action = WeaponAction.SHOT;
        return gun;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        tag.put(GENERAL, this.general.serializeNBT(provider));
        tag.put(MELEE, this.melee.serializeNBT(provider));
        tag.put(THROWABLE, this.throwable.serializeNBT(provider));
        tag.put(SOUNDS, NbtUtils.serializeStringMap(this.sounds));
        tag.put(MODULES, this.modules.serializeNBT(provider));
        tag.put(TEXTURES, NbtUtils.serializeStringMap(this.textures));
        tag.put(ANIMATIONS, NbtUtils.serializeStringMap(this.animations));
        tag.put(AMMO_DATA, NbtUtils.serializeMap(this.ammoData, provider));
        tag.put(SECONDARY_AMMO, NbtUtils.serializeMap(this.fuel, provider));
        tag.put(MODES, NbtUtils.serializeMap(this.modes, provider));
        tag.put(DISPLAY, this.display.serializeNBT(provider));
        if (this.zoom != null) {
            tag.put("Zoom", this.zoom.serializeNBT(provider));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains(GENERAL)) {
            this.general.deserializeNBT(null,tag.getCompoundOrEmpty(GENERAL));
        }
        if (tag.contains(MELEE)) {
            this.melee.deserializeNBT(null,tag.getCompoundOrEmpty(MELEE));
        }
        if (tag.contains(THROWABLE)) {
            this.throwable.deserializeNBT(null,tag.getCompoundOrEmpty(THROWABLE));
        }
        if (tag.contains(SOUNDS)) {
            this.sounds = deserializeSounds(tag.getCompoundOrEmpty(SOUNDS));
        }
        if (tag.contains(MODULES)) {
            this.modules.deserializeNBT(null,tag.getCompoundOrEmpty(MODULES));
        }
        if (tag.contains(TEXTURES)) {
            this.textures = NbtUtils.deserializeRLMap(tag.getCompoundOrEmpty(TEXTURES));
        }
        if (tag.contains(ANIMATIONS)) {
            this.animations = NbtUtils.deserializeMap(tag.getCompoundOrEmpty(ANIMATIONS),
                    AnimationType::getType,
                    (nbt, key) -> Identifier.tryParse(nbt.getStringOr(key, ""))
            );
        }
        if (tag.contains(AMMO_DATA)) {
            this.ammoData = NbtUtils.deserializeLinkedMap(tag.getCompoundOrEmpty(AMMO_DATA), AmmoData::create);
        }
        if (tag.contains(SECONDARY_AMMO)) {
            this.fuel = NbtUtils.deserializeLinkedMap(tag.getCompoundOrEmpty(SECONDARY_AMMO), Fuel::create);
        }
        if (tag.contains(MODES)) {
            this.modes = NbtUtils.deserializeMap(tag.getCompoundOrEmpty(MODES),
                    WeaponMode::getType,
                    (nbt, key) -> WeaponSettings.create(nbt.getCompoundOrEmpty(key)));
        }
        if(tag.contains("Zoom")) {
            this.zoom = Zoom.create(tag.getCompoundOrEmpty("Zoom"));
        }
        if(tag.contains(DISPLAY)) {
            this.display = Display.create(tag.getCompoundOrEmpty(DISPLAY));
        }
    }

    public JsonObject toJsonObject() {
        var gson = new Gson();
        var object = new JsonObject();
        object.add("general"    , this.general.toJsonObject());
        object.add("melee"      , this.melee.toJsonObject());
        object.add("throwable"  , this.throwable.toJsonObject());
        GunJsonUtil.addObjectIfNotEmpty(object, "ammoData", gson.toJsonTree(this.ammoData).getAsJsonObject());
        GunJsonUtil.addObjectIfNotEmpty(object, "sounds"  , gson.toJsonTree(this.sounds).getAsJsonObject());
        GunJsonUtil.addObjectIfNotEmpty(object, "modules" , this.modules.toJsonObject());
        GunJsonUtil.addObjectIfNotEmpty(object, "modes"   , gson.toJsonTree(this.modes).getAsJsonObject());
        if (zoom != null)
            object.add("zoom", this.zoom.toJsonObject());
        if (display != null)
            object.add("display", this.display.toJsonObject());
        return object;
    }

    public WeaponConfig copy() {
        var gun = new WeaponConfig();
        gun.general     = this.general.copy();
        gun.melee       = this.melee.copy();
        gun.throwable   = this.throwable.copy();
        gun.sounds      = (HashMap<String, Identifier>) this.sounds.clone();
        gun.textures    = (HashMap<String, Identifier>) this.textures.clone();
        gun.animations  = (HashMap<AnimationType, Identifier>) this.animations.clone();
        gun.ammoData    = copyAmmoData(this.ammoData);
        gun.fuel        = copyFuel(this.fuel);
        gun.modes       = copyModes(this.modes);
        gun.modules     = this.modules.copy();
        gun.zoom        = this.zoom != null ? this.zoom.copy() : null;
        gun.display     = this.display.copy();
        return gun;
    }

    private static LinkedHashMap<Identifier, AmmoData> copyAmmoData(LinkedHashMap<Identifier, AmmoData> source) {
        var result = new LinkedHashMap<Identifier, AmmoData>();
        source.forEach((key, value) -> result.put(key, value.copy()));
        return result;
    }

    private static LinkedHashMap<Identifier, Fuel> copyFuel(LinkedHashMap<Identifier, Fuel> source) {
        var result = new LinkedHashMap<Identifier, Fuel>();
        source.forEach((key, value) -> result.put(key, value.copy()));
        return result;
    }

    private static HashMap<WeaponMode, WeaponSettings> copyModes(HashMap<WeaponMode, WeaponSettings> source) {
        var result = new HashMap<WeaponMode, WeaponSettings>();
        source.forEach((mode, settings) -> result.put(mode, settings.copy()));
        return result;
    }

    public static WeaponConfig create(Identifier id, CompoundTag tag) {
        var gun = new WeaponConfig();
        gun.deserializeNBT(null,tag);
        return gun;
    }

    public void onCreated(String id){}

    public General getGeneral() {
        return this.general;
    }

    public Melee getMelee() {
        return this.melee;
    }

    public ThrowableConfig getThrowable() {
        return this.throwable;
    }

    public HashMap<String, Identifier> getSounds() {
        return sounds;
    }

    public HashMap<String, Identifier> getSoundsMap() {
        return sounds;
    }

    public Identifier getSound(String name){
        return sounds.get(name);
    }


    public Modules getModules() {
        return this.modules;
    }
//    public Identifier getTexture(String variant) {
//        return preparedTextures.computeIfAbsent(variant, v ->
//                prepareTexture(textures.get(variant))
//        );
//    }

    public Map<String, Identifier> getTextures() {
        return textures;
    }

    public HashMap<AnimationType, Identifier> getAnimations() {
        return animations;
    }

    public Identifier getAnimation(AnimationType type) {
        return animations.get(type);
    }

    public boolean canAttachType(@Nullable AttachmentType type) {
        var attachments = this.getModules().getAttachments();
        if(attachments == null)
            return false;
        return attachments.containsKey(type);
    }

    public ArrayList<Modules.Attachment> getAttachmentConfigs(ArrayList<ItemStack> itemStacks) {
        var result = new ArrayList<Modules.Attachment>();

        for (var stack : itemStacks) {
            var item = stack.getItem();
            var attachment = findAttachment(item);

            if(attachment != null)
                result.add(attachment);
        }
        return result;
    }

    public Modules.Attachment findAttachment(Item item) {
        var itemId = BuiltInRegistries.ITEM.getKey(item);

        if(item instanceof IAttachment attachmentItem){
            var attachmentType = attachmentItem.getType();

            if(!getModules().getAttachments().containsKey(attachmentType))
                return new Modules.Attachment();

            var attachments = getModules().getAttachments().get(attachmentType);

            for (var attachment : attachments) {
                if(attachment.getItemId() != null && attachment.getItemId().equals(itemId)){
                    return attachment;
                }
            }
        }
        return new Modules.Attachment();
    }

    public AmmoData getAmmoData(Identifier ammo) {
        return ammoData.getOrDefault(ammo, new AmmoData());
    }

    public Fuel getFuelData(Identifier ammo) {
        return fuel.getOrDefault(ammo, new Fuel());
    }

    public boolean hasAmmo(Identifier ammo){
        return ammoData.containsKey(ammo);
    }

    public ProjectileConfig getProjectileConfig(Identifier ammo){
        return getAmmoData(ammo).getProjectile();
    }

    public AmmoConfig getAmmoConfig(Identifier ammo){
        return getAmmoData(ammo).getAmmo();
    }

    public Fuel getFuelConfig(Identifier ammo) {
        return getFuelData(ammo);
    }

    public AmmoConfig getFuelAmmoConfig(Identifier ammo){
        return getFuelData(ammo).getAmmo();
    }

    public General getGeneral(WeaponMode mode) {
        if(mode == WeaponMode.PRIMARY)
            return general;
        else return modes.getOrDefault(mode, new WeaponSettings()).getGeneral();
    }

    public Melee getMelee(WeaponMode mode) {
        if(mode == WeaponMode.PRIMARY)
            return melee;
        else return modes.getOrDefault(mode, new WeaponSettings()).getMelee();
    }

    public AmmoData getAmmoData(WeaponMode mode, Identifier ammoId) {
        if(mode == WeaponMode.PRIMARY)
            return ammoData.getOrDefault(ammoId, new AmmoData());
        else return modes.getOrDefault(mode, new WeaponSettings()).getAmmoData(ammoId);
    }

    public ThrowableConfig getThrowable(WeaponMode mode) {
        if(mode == WeaponMode.PRIMARY)
            return throwable;
        else return modes.getOrDefault(mode, new WeaponSettings()).getThrowable();
    }

    public Zoom getZoom(WeaponMode mode) {
        if(mode == WeaponMode.PRIMARY)
            return zoom;
        else return modes.getOrDefault(mode, new WeaponSettings()).getZoom();
    }

    public Display getDisplay() {
        return display;
    }

    public HashMap<WeaponMode, WeaponSettings> getModes() {
        return modes;
    }

    private HashMap<String, Identifier> deserializeSounds(CompoundTag tag){
        var result = new HashMap<String, Identifier>();
        for (var key: tag.keySet()) {
            if(tag.contains(key)) {
                result.put(key, createSound(tag, key));
            }
        }
        return result;
    }

    private Identifier createSound(CompoundTag tag, String key) {
        var sound = tag.getStringOr(key, "");
        return sound.isEmpty() ? null : Identifier.tryParse(sound);
    }

    public static class Builder {
        private final WeaponConfig weaponConfig;

        private Builder() {
            this.weaponConfig = new WeaponConfig();
        }

        private Builder(WeaponConfig weaponConfig) {
            this.weaponConfig = weaponConfig.copy();
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(WeaponConfig weaponConfig) {
            return new Builder(weaponConfig);
        }

        public WeaponConfig build() {
            return this.weaponConfig.copy(); //Copy since the builder could be used again
        }

        public WeaponConfig.Builder addAmmo(AmmoHolder id) {
            this.weaponConfig.general.ammo.add(id);
            return this;
        }

        public Builder setFireRate(int rate) {
            this.weaponConfig.general.rate = rate;
            return this;
        }

        public Builder setGripType(GripType gripType) {
            this.weaponConfig.general.gripType = gripType;
            return this;
        }

        public Builder setMaxAmmo(int maxAmmo) {
            this.weaponConfig.general.maxAmmo = maxAmmo;
            return this;
        }

        public Builder setReloadAmount(int reloadAmount) {
            this.weaponConfig.general.reloadAmount = reloadAmount;
            return this;
        }

        public Builder setReloadTime(int reloadTime) {
            this.weaponConfig.general.reloadTime = reloadTime;
            return this;
        }

        public Builder setLoadingType(LoadingType loadingType) {
            this.weaponConfig.general.loadingType = loadingType;
            return this;
        }

        public Builder setCategory(String category) {
            this.weaponConfig.general.category = category;
            return this;
        }

        public Builder setRecoilAngle(float recoilAngle) {
            this.weaponConfig.general.recoilAngle = recoilAngle;
            return this;
        }

        public Builder setRecoilDurationOffset(float recoilDurationOffset) {
            this.weaponConfig.general.recoilDurationOffset = recoilDurationOffset;
            return this;
        }

        public Builder setRecoilAdsReduction(float recoilAdsReduction) {
            this.weaponConfig.general.recoilAdsReduction = recoilAdsReduction;
            return this;
        }

        public Builder setProjectileAmount(int projectileAmount) {
            this.weaponConfig.general.projectileAmount = projectileAmount;
            return this;
        }

        public Builder setAlwaysSpread(boolean alwaysSpread) {
            this.weaponConfig.general.alwaysSpread = alwaysSpread;
            return this;
        }

        public Builder setSpread(float spread) {
            this.weaponConfig.general.spread = spread;
            return this;
        }
    }
}
