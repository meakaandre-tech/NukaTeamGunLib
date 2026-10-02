package com.nukateam.ntgl.smoketest;

import com.nukateam.example.common.registery.ExampleWeapons;
import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.data.holders.WeaponMode;
import com.nukateam.ntgl.common.network.ServerPlayHandler;
import com.nukateam.ntgl.common.network.enums.KeyAction;
import com.nukateam.ntgl.common.network.message.weapon.C2SMessageGrenade;
import com.nukateam.ntgl.common.network.message.weapon.C2SMessageReload;
import com.nukateam.ntgl.common.network.message.weapon.C2SMessageShoot;
import com.nukateam.ntgl.common.util.util.WeaponStateHelper;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.TreeSet;

/**
 * CI smoke test (not shipped), run on a real dedicated server: a mock player shoots, reloads and throws a
 * grenade through the same server handlers the network packets use. It fails on any exception, which is how
 * a client-only class referenced from server code would show up.
 */
public class NtglServerGameTest {
    @GameTest(maxTicks = 600)
    public void weaponsOnDedicatedServer(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockServerPlayerInLevel();
        var position = helper.absoluteVec(new Vec3(1.5, 2.0, 1.5));
        player.setGameMode(GameType.SURVIVAL);
        player.snapTo(position.x, position.y, position.z, 0F, 0F);

        var pistol = new ItemStack(ExampleWeapons.PISTOL10MM.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, pistol);
        WeaponStateHelper.setAmmoCount(new WeaponData(pistol, player), 20);

        var zombie = helper.spawnWithNoFreeWill(EntityTypes.ZOMBIE, new Vec3(1.5, 2.0, 5.5));
        var startHealth = zombie.getHealth();
        var seen = new TreeSet<String>();

        log("start: player " + player.position() + " zombie " + zombie.position() + " ammo " + ammo(player));
        ServerPlayHandler.handleShoot(new C2SMessageShoot(player.getId(), 0F, 0F, 0F, 0F, InteractionHand.MAIN_HAND, WeaponMode.PRIMARY), player);
        collect(helper, seen);

        helper.runAfterDelay(3, () -> collect(helper, seen));
        helper.runAfterDelay(30, () -> {
            collect(helper, seen);
            log("after shooting: zombie health " + zombie.getHealth() + " (was " + startHealth + ") ammo " + ammo(player) + " entities seen " + seen);

            var held = player.getMainHandItem();
            WeaponStateHelper.setAmmoCount(new WeaponData(held, player), 0);
            for (int i = 0; i < 5; i++)
                player.getInventory().add(new ItemStack(ExampleWeapons.ROUND10MM.get()));
            ServerPlayHandler.handleReload(new C2SMessageReload(InteractionHand.MAIN_HAND, WeaponMode.PRIMARY), player);
        });

        helper.runAfterDelay(130, () -> {
            log("after reloading: ammo " + ammo(player) + " inventory " + player.getInventory().countItem(ExampleWeapons.ROUND10MM.get()));

            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ExampleWeapons.GRENADE.get()));
            ServerPlayHandler.handleGrenade(new C2SMessageGrenade(KeyAction.HOLD, InteractionHand.MAIN_HAND, WeaponMode.PRIMARY), player);
        });

        helper.runAfterDelay(170, () ->
                ServerPlayHandler.handleGrenade(new C2SMessageGrenade(KeyAction.RELEASE, InteractionHand.MAIN_HAND, WeaponMode.PRIMARY), player));

        for (int tick = 171; tick < 330; tick += 4)
            helper.runAfterDelay(tick, () -> collect(helper, seen));

        helper.runAfterDelay(330, () -> {
            log("after the grenade: held " + player.getMainHandItem() + " entities seen " + seen + " zombie alive " + zombie.isAlive());
            player.discard();
            helper.succeed();
        });
    }

    private static int ammo(net.minecraft.server.level.ServerPlayer player) {
        return WeaponStateHelper.getAmmoCount(new WeaponData(player.getMainHandItem(), player));
    }

    private static void collect(GameTestHelper helper, TreeSet<String> seen) {
        for (var entity : helper.getLevel().getAllEntities())
            seen.add(entity.getType().toShortString());
    }

    private static void log(String message) {
        Ntgl.LOGGER.info("[server-smoke] {}", message);
    }
}
