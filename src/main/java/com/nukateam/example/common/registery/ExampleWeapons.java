package com.nukateam.example.common.registery;

import com.nukateam.example.common.modifiers.*;
import com.nukateam.ntgl.common.foundation.item.attachment.*;
import com.nukateam.ntgl.common.data.attachment.impl.*;
import com.nukateam.ntgl.common.foundation.item.*;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.util.interfaces.IWeaponModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import com.nukateam.ntgl.platform.DeferredHolder;
import com.nukateam.ntgl.platform.DeferredRegister;

public class ExampleWeapons {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ntgl.MOD_ID);
    ///WEAPONS
    public static final DeferredHolder<Item, WeaponItem> PISTOL10MM = registerGun("pistol10mm", new TestModifier());
    public static final DeferredHolder<Item, WeaponItem> PIPE_PISTOL = registerGun("pipepistol");
    public static final DeferredHolder<Item, WeaponItem> CLASSIC10MM = registerGun("classic10mm", 10);
    public static final DeferredHolder<Item, WeaponItem> SCOUT10MM = registerGun("scout10mm");
    public static final DeferredHolder<Item, WeaponItem> PIPE_REVOLVER = registerGun("piperevolver");
    public static final DeferredHolder<Item, WeaponItem> FATMAN = registerGun("fatman");
    public static final DeferredHolder<Item, WeaponItem> MINIGUN = registerGun("minigun", new MinigunModifier());
    public static final DeferredHolder<Item, WeaponItem> POWDERGUN = registerGun("powdergun");
    public static final DeferredHolder<Item, WeaponItem> SHOTGUN = registerGun("shotgun");
    public static final DeferredHolder<Item, WeaponItem> FLAMER = registerGun("flamer");
    public static final DeferredHolder<Item, WeaponItem> GATLING = registerGun("gatling");
    public static final DeferredHolder<Item, WeaponItem> REVOLVER = registerGun("revolver");
    public static final DeferredHolder<Item, WeaponItem> RPG = registerGun("rpg");
    public static final DeferredHolder<Item, WeaponItem> HAMMER = registerGun("hammer");

    public static final DeferredHolder<Item, Item> GRENADE = ITEMS.registerItem("grenade", props -> new WeaponItem(props));

//    public static final DeferredHolder<Item, Item> MISSILE = ITEMS.register("missile",
//            () -> new AmmoItem(new Item.Properties().tab(ModItemTabs.WEAPONS)));

    public static final DeferredHolder<Item, Item> GRENADE_OG = ITEMS.registerItem("grenade_old", props -> new WeaponItem(props));

    public static final DeferredHolder<Item, Item> STUN_GRENADE = ITEMS.registerItem("stun_grenade", props -> new StunGrenadeItem(props));

    //Rounds
    public static final DeferredHolder<Item, Item> ROUND10MM = ITEMS.registerItem("round10mm", props -> new AmmoItem(props.durability(100)));

    public static final DeferredHolder<Item, Item> ROUND38    = registerAmmo("round38"    );
    public static final DeferredHolder<Item, Item> STEELBALLS = registerAmmo("steel_ball" );
    public static final DeferredHolder<Item, Item> ROUND45    = registerAmmo("round45"    );
    public static final DeferredHolder<Item, Item> ROUND5MM   = registerAmmo("round5mm"   );
    public static final DeferredHolder<Item, Item> ROUND44    = registerAmmo("round44"    );
    public static final DeferredHolder<Item, Item> ROUND50    = registerAmmo("round50"    );
    public static final DeferredHolder<Item, Item> ROUND380   = registerAmmo("round380"   );
    public static final DeferredHolder<Item, Item> ROUND556   = registerAmmo("round556"   );
    public static final DeferredHolder<Item, Item> SHOTSHELL  = registerAmmo("shotshell"  );
    public static final DeferredHolder<Item, Item> ROUND127   = registerAmmo("round127"   );
    public static final DeferredHolder<Item, Item> ROUND22    = registerAmmo("round22"    );
    public static final DeferredHolder<Item, Item> MININUKE   = registerAmmo("mini_nuke"  );
    public static final DeferredHolder<Item, Item> FUEL       = registerAmmo("fuel"  );

    /* Scope Attachments */
    public static final DeferredHolder<Item, Item> HOLOGRAPHIC_SIGHT = ITEMS.registerItem("holographic_sight", props -> new ScopeItem(Attachments.LONG_SCOPE, props.stacksTo(1)));

    public static final DeferredHolder<Item, Item> COLLIMATOR_SIGHT = ITEMS.registerItem("collimator_sight", props -> new ScopeItem(Attachments.SHORT_SCOPE, props.stacksTo(1)));

    /* Barrel Attachments */
    public static final DeferredHolder<Item, Item> SILENCER = ITEMS.registerItem("silencer", props -> new BarrelItem(Barrel.create(8.0F, WeaponModifiers.SILENCED, WeaponModifiers.REDUCED_DAMAGE), props.stacksTo(1)));

    /* Stock Attachments */
    public static final DeferredHolder<Item, Item> LIGHT_STOCK = ITEMS.registerItem("light_stock", props -> new StockItem(Stock.create(WeaponModifiers.BETTER_CONTROL), props.stacksTo(1)));
    public static final DeferredHolder<Item, Item> TACTICAL_STOCK = ITEMS.registerItem("tactical_stock", props -> new StockItem(Stock.create(WeaponModifiers.STABILISED), props.stacksTo(1)));
    public static final DeferredHolder<Item, Item> WEIGHTED_STOCK = ITEMS.registerItem("weighted_stock", props -> new StockItem(Stock.create(WeaponModifiers.SUPER_STABILISED), props.stacksTo(1)));

    /* Under Barrel Attachments */
    public static final DeferredHolder<Item, Item> LIGHT_GRIP = ITEMS.registerItem("light_grip", props -> new GripItem(Grip.create(WeaponModifiers.LIGHT_RECOIL), props.stacksTo(1)));
    public static final DeferredHolder<Item, Item> SPECIALISED_GRIP = ITEMS.registerItem("specialised_grip", props -> new GripItem(Grip.create(WeaponModifiers.REDUCED_RECOIL), props.stacksTo(1)));

    /* Magazine Attachments*/
    public static final DeferredHolder<Item, Item> EXTENDED_MAGAZINE = ITEMS.registerItem("extended_magazine", props -> new MagazineItem(Magazine.create(30, WeaponModifiers.SLOW_ADS), props.stacksTo(1)));

    public static final DeferredHolder<Item, Item> DRUM_MAGAZINE = ITEMS.registerItem("drum_magazine", props -> new MagazineItem(Magazine.create(60, WeaponModifiers.SLOWER_ADS, WeaponModifiers.EXTENDED_MAG), props.stacksTo(1)));

    public static final DeferredHolder<Item, Item> HEAD_STONE = ITEMS.registerItem("hammer_stone", props -> new GripItem(Grip.create(WeaponModifiers.REDUCED_RECOIL), props.stacksTo(1)));

    public static final DeferredHolder<Item, Item> HAMMER_DIAMOND = ITEMS.registerItem("hammer_diamond", props -> new GripItem(Grip.create(WeaponModifiers.REDUCED_RECOIL), props.stacksTo(1)));

    public static final DeferredHolder<Item, Item> AMMO_BOX = ITEMS.registerItem("ammo_box", props ->
            new AmmoBoxItem(props.stacksTo(1), 100));

    public static DeferredHolder<Item, WeaponItem> registerGun(String name, IWeaponModifier... modifiers) {
        return ITEMS.registerItem(name, props -> new WeaponItem(props.stacksTo(1), modifiers));
    }

    public static DeferredHolder<Item, WeaponItem> registerGun(String name, int durability) {
        return ITEMS.registerItem(name, props -> new WeaponItem(props.durability(durability)));
    }

    public static DeferredHolder<Item, Item> registerAmmo(String name) {
        return ITEMS.registerItem(name, props -> new AmmoItem(props));
    }

    public static void register() {
    }
}
