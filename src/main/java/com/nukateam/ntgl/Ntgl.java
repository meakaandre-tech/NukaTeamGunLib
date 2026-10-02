package com.nukateam.ntgl;

import com.mojang.logging.LogUtils;
import com.nukateam.example.common.registery.ExampleWeapons;
import com.nukateam.ntgl.client.settings.NtglOptions;
import com.nukateam.ntgl.common.data.holders.AnimationType;
import com.nukateam.ntgl.common.foundation.entity.StunGrenadeEntity;
import com.nukateam.ntgl.common.foundation.init.*;
import com.nukateam.ntgl.common.handlers.*;
import com.nukateam.ntgl.common.network.PacketHandler;
import com.nukateam.ntgl.common.registry.AmmoHolders;
import com.nukateam.ntgl.common.registry.ProjectileRegistry;
import com.nukateam.ntgl.common.util.managers.BoundingBoxManager;
import com.nukateam.ntgl.common.util.trackers.*;
import com.nukateam.ntgl.common.util.util.DelayedTask;
import com.nukateam.ntgl.modules.crafting.CraftingModule;
import com.nukateam.ntgl.modules.data.DataKeyManager;
import com.nukateam.ntgl.modules.datapack.handlers.NetworkManagerHandler;
import com.nukateam.ntgl.modules.gunpack.GunPackModule;
import com.nukateam.ntgl.platform.EventBus;
import com.nukateam.ntgl.platform.FabricEvents;
import com.nukateam.ntgl.platform.PlatformHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class Ntgl implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "ntgl";

    /**
     * Carries NTGL's own events (GunFireEvent, GunReloadEvent, GunProjectileHitEvent, MeleeAttackEvent, ...)
     * and the game events of com.nukateam.ntgl.platform.event. Replaces NeoForge.EVENT_BUS:
     * register handler classes with {@code Ntgl.EVENT_BUS.register(MyHandlers.class)}.
     */
    public static final EventBus EVENT_BUS = new EventBus();

    public static boolean controllableLoaded = false;
    public static boolean backpackedLoaded = false;
    public static boolean sophisticatedLoaded = false;
    public static boolean travelersLoaded = false;
    public static boolean yyzBackpackLoaded = false;
    public static boolean curiosLoaded = false;
    public static boolean playerReviveLoaded = false;
    public static boolean playerAnimatorLoaded = false;
    public static boolean subtleEffectsLoaded = false;
    public static boolean irisLoaded = false;
    public static boolean sableLoaded = false;

    @Override
    public void onInitialize() {
        Config.load();

        NtglContainers.REGISTER.register();
        ModEffects.REGISTER.register();
        Projectiles.REGISTER.register();
        ExampleWeapons.register();
        ModParticleTypes.REGISTER.register();
        ModSounds.REGISTER.register();
        NtglComponents.REGISTER.register();
        ModEntityTypes.register();
        NtglEntityDataSerializers.register();

        GunPackModule.init();
        ModTileEntities.REGISTER.register();
        CraftingModule.init();
        NtglGameEvents.register();

        irisLoaded = PlatformHelper.isModLoaded("iris");

        AmmoHolders.register();
        AnimationType.register();

        PacketHandler.register();
        FabricEvents.register();
        registerEventHandlers();

        ModSyncedDataKeys.register();
        ProjectileRegistry.registerProjectiles();
    }

    private static void registerEventHandlers() {
        EVENT_BUS.register(EntityEvents.class);
        EVENT_BUS.register(GunBehaviorManager.class);
        EVENT_BUS.register(GunEventHandler.class);
        EVENT_BUS.register(PlayerEventHandler.class);
        EVENT_BUS.register(ServerEvent.class);
        EVENT_BUS.register(EntityReloadTracker.class);
        EVENT_BUS.register(EquipTracker.class);
        EVENT_BUS.register(MeleeTracker.class);
        EVENT_BUS.register(ReloadTracker.class);
        EVENT_BUS.register(SpreadTracker.class);
        EVENT_BUS.register(ThrowingTracker.class);
        EVENT_BUS.register(DelayedTask.class);
        EVENT_BUS.register(DataKeyManager.class);
        NetworkManagerHandler.register();
        EVENT_BUS.register(StunGrenadeEntity.class);
        // The handler itself checks the improvedHitboxes option every tick
        EVENT_BUS.register(new BoundingBoxManager());
    }

    public static boolean isDebugging() {
        return PlatformHelper.isDevelopment();
    }

    public static NtglOptions getOptions() {
        return NtglOptions.getInstance();
    }

    public static Identifier ntglResource(String name) {
        return Identifier.tryBuild(MOD_ID, name);
    }
}
