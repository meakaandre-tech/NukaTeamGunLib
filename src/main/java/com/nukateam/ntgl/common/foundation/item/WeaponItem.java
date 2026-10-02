package com.nukateam.ntgl.common.foundation.item;

import com.google.common.collect.HashMultimap;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.input.NtglKeyBinds;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.data.config.weapon.ExplosionConfig;
import com.nukateam.ntgl.common.data.config.weapon.WeaponConfig;
import com.nukateam.ntgl.common.data.holders.AttachmentType;
import com.nukateam.ntgl.common.data.holders.WeaponMode;
import com.nukateam.ntgl.common.data.config.weapon.ProjectileConfig;
import com.nukateam.ntgl.common.foundation.entity.throwable.ThrowableItemEntity;
import com.nukateam.ntgl.common.foundation.init.NtglComponents;
import com.nukateam.ntgl.common.network.ServerPlayHandler;
import com.nukateam.ntgl.common.util.managers.ProjectileManager;
import com.nukateam.ntgl.modules.datapack.ConfigSupplier;
import com.nukateam.ntgl.common.util.util.FuelUtils;
import com.nukateam.ntgl.common.util.interfaces.IWeaponModifier;
import com.nukateam.ntgl.common.util.util.*;
import com.nukateam.ntgl.common.foundation.item.interfaces.*;
import net.minecraft.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import com.nukateam.ntgl.platform.PlatformHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.*;
import com.nukateam.ntgl.platform.Lazy;

import javax.annotation.Nullable;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

/**
 * Fabric port note: the item is no longer a GeckoLib GeoItem. Its animated model is drawn by the
 * "ntgl:weapon" special item model (see the item model definition of each weapon), which looks the
 * renderer up in WeaponRegistry.
 */
public class WeaponItem extends Item implements IWeapon, IThrowable {
    public static final String VARIANT = "variant";
    private final Lazy<Identifier> id = Lazy.of(this::getRegistryName);
    private final WeakHashMap<CompoundTag, WeaponConfig> modifiedGunCache = new WeakHashMap<>();
    private WeaponConfig weaponConfig = new WeaponConfig();

    protected IWeaponModifier[] modifiers;

    public WeaponItem(Item.Properties properties, IWeaponModifier... modifiers) {
        super(properties.enchantable(5));
        this.modifiers = modifiers;
    }

    @Override
    public IWeaponModifier[] getModifiers() {
        return modifiers;
    }

    @Override
    public void setConfig(ConfigSupplier<WeaponConfig> supplier) {
        this.weaponConfig = supplier.config();
        weaponConfig.onCreated(getId().getPath());
    }

    @Override
    public WeaponConfig getConfig() {
        return this.weaponConfig;
    }

    @Override
    public Identifier getId() {
        return id.get();
    }

    public void setDefaultTag(ItemStack stack){
        WeaponStateHelper.setAmmoCount(new WeaponData(stack, null), getConfig().getGeneral().getMaxAmmo());
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, @Nullable net.minecraft.world.entity.EquipmentSlot slot) {
        if(entity instanceof LivingEntity livingEntity) {
            WeaponItemUtils.checkAmmo(stack, entity, livingEntity);
        }
    }

    private static boolean isItemInHands(ItemStack stack, LivingEntity livingEntity) {
        return stack == livingEntity.getItemInHand(InteractionHand.MAIN_HAND) ||
                stack == livingEntity.getItemInHand(InteractionHand.OFF_HAND);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        var tooltip = new ArrayList<Component>();
        appendWeaponTooltip(stack, context, tooltip, tooltipFlag);
        tooltip.forEach(tooltipAdder);
    }

    protected void appendWeaponTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        var data = new WeaponData(stack, null, context.registries());

        boolean canShoot = WeaponModifierHelper.canShoot(data);
        boolean canThrow = WeaponModifierHelper.canThrow(data);

        ProjectileConfig projectileConfig = new ProjectileConfig();
        if (canShoot) {
            projectileConfig = WeaponStateHelper.getProjectileConfig(data);
        } else if (canThrow) {
            projectileConfig = WeaponModifierHelper.getThrowable(data).getProjectile();
        }

        var explosion = projectileConfig.getExplosion();
        boolean hasExplosion = explosion != null && explosion.getRadius() > 0;
        boolean isPureMelee = !canShoot && !canThrow && !hasExplosion;

        if (canShoot) {
            WeaponItemTooltips.addRangedStats(tooltip, data);
        }

        if (hasExplosion) {
            WeaponItemTooltips.addExplosionStats(tooltip, data, projectileConfig);
        }

        if (isPureMelee) {
            WeaponItemTooltips.addVanillaMeleeStats(tooltip, data);
        }

        WeaponItemTooltips.addFuel(tooltip, data);
        WeaponItemTooltips.addAttachmentsStats(tooltip, data);

        boolean hasHandlingOptions = canShoot;
        boolean hasAttachments = !WeaponModifierHelper.getAttachmentTypes(data).isEmpty();

        if (hasHandlingOptions) {
            if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
                WeaponItemTooltips.addHandlingStats(tooltip, data, true);
            } else {
                tooltip.add(Component.translatable("info.ntgl.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            }
        } else if (hasAttachments) {
            if (!net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
                tooltip.add(Component.translatable("info.ntgl.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        var name = NtglKeyBinds.KEY_ATTACHMENTS.getTranslatedKeyMessage();

        tooltip.add(Component.translatable("info.ntgl.attachment_help", name)
                .withStyle(ChatFormatting.YELLOW));
    }
    protected WeaponData getWeaponData(ItemStack stack) {
        return new WeaponData(stack, Minecraft.getInstance().player);
    }

    @Override
    public WeaponConfig getModifiedConfig(ItemStack stack) {
        var tag = NtglComponents.getWeaponTag(stack);
        if (tag.contains("Gun")) {
            return this.modifiedGunCache.computeIfAbsent(tag, item ->
            {
                if (tag.getBooleanOr("Custom", false)) {
                    var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    return WeaponConfig.create(key, tag.getCompoundOrEmpty("Gun"));
                } else {
                    var gunCopy = this.weaponConfig.copy();
                    gunCopy.deserializeNBT(null, tag.getCompoundOrEmpty("Gun"));
                    return gunCopy;
                }
            });
        }

        return this.weaponConfig;
    }

    /** Weapons only take enchantments when their config allows it (replaces isEnchantable/isBookEnchantable). */
    @Override
    public boolean canBeEnchantedWith(ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment, net.fabricmc.fabric.api.item.v1.EnchantingContext context) {
        return this.weaponConfig.getGeneral().isEnchantable()
                && super.canBeEnchantedWith(stack, enchantment, context);
    }

    /** Ammo and state live in data components; changing them must not replay the equip animation. */
    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    private Identifier getRegistryName() {
        return BuiltInRegistries.ITEM.getKey(this);
    }

    @Override
    public void expire(LivingEntity entityLiving) {
        var throwableEntity = this.createThrowable(entityLiving.level(), entityLiving, 0);
        throwableEntity.onDeath();
    }

    @Override
    public void throwItem(ItemStack stack, LivingEntity entityLiving, int timeLeft) {
        var level = entityLiving.level();

        if (!(entityLiving instanceof Player player) || !player.isCreative()) {
            stack.shrink(1);
        }

        var grenade = this.createThrowable(level, entityLiving, timeLeft);
        grenade.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, Math.min(1.0F, timeLeft / 20F), 1.0F);
        level.addFreshEntity(grenade);
        this.onThrown(level, grenade);

        if (entityLiving instanceof Player) {
            ((Player) entityLiving).awardStat(Stats.ITEM_USED.get(this));
        }
    }

    protected void onThrown(Level world, ThrowableItemEntity entity) {}

    protected ThrowableItemEntity createThrowable(Level world, LivingEntity entity, int timeLeft) {
        var projectile = getConfig().getThrowable().getProjectile().getProjectileType();
        return ProjectileManager.getInstance()
                .getFactory(projectile)
                .create(world, entity, this, timeLeft);
    }
}
