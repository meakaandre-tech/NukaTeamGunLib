package com.nukateam.ntgl.smoketest;

import com.nukateam.ntgl.client.input.NtglKeyBinds;
import com.nukateam.ntgl.modules.gunpack.regestry.ModBlocks;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.minecraft.client.CameraType;
import net.minecraft.world.MenuProvider;
import org.lwjgl.glfw.GLFW;

/**
 * CI smoke test (not shipped): opens a singleplayer world, holds the example weapons, shoots, aims,
 * reloads, opens the screens and takes screenshots so the port can be checked without a local game.
 */
public class NtglSmokeTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (var singleplayer = context.worldBuilder().create()) {
            var connection = singleplayer.getConnection();
            var server = singleplayer.getServer();

            connection.waitForChunksRender();
            server.runCommand("gamemode creative @a");
            server.runCommand("time set noon");
            server.runCommand("weather clear");

            hold(context, server, "pistol10mm");
            server.runCommand("give @a ntgl:round10mm 64");
            context.waitTicks(30);
            context.takeScreenshot("01_pistol_first_person");

            step("shoot", () -> {
                context.getInput().holdMouseFor(0, 4);
                context.waitTicks(2);
                context.takeScreenshot("02_pistol_shooting");
                context.waitTicks(20);
            });

            step("aim", () -> {
                context.getInput().holdMouse(1);
                context.waitTicks(15);
                context.takeScreenshot("03_pistol_aiming");
                context.getInput().releaseMouse(1);
                context.waitTicks(10);
            });

            step("reload", () -> {
                context.getInput().pressKey(NtglKeyBinds.KEY_RELOAD);
                context.waitTicks(12);
                context.takeScreenshot("04_pistol_reloading");
                context.waitTicks(60);
            });

            step("third person", () -> {
                context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
                context.waitTicks(10);
                context.takeScreenshot("05_pistol_third_person_back");
                context.runOnClient(mc -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
                context.waitTicks(10);
                context.takeScreenshot("06_pistol_third_person_front");
                context.runOnClient(mc -> mc.options.setCameraType(CameraType.FIRST_PERSON));
                context.waitTicks(5);
            });

            for (var weapon : new String[]{"shotgun", "minigun", "hammer", "grenade", "fatman", "flamer"}) {
                step(weapon, () -> {
                    hold(context, server, weapon);
                    context.waitTicks(25);
                    context.takeScreenshot("10_" + weapon + "_first_person");
                    context.getInput().holdMouseFor(0, 6);
                    context.waitTicks(4);
                    context.takeScreenshot("11_" + weapon + "_attack");
                    context.waitTicks(25);
                });
            }

            step("inventory", () -> {
                hold(context, server, "pistol10mm");
                server.runCommand("give @a ntgl:minigun");
                server.runCommand("give @a ntgl:shotgun");
                server.runCommand("give @a ntgl:workbench");
                context.waitTicks(10);
                context.getInput().pressKey(options -> options.keyInventory);
                context.waitTicks(10);
                context.takeScreenshot("20_inventory");
                context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
                context.waitTicks(5);
            });

            step("attachments screen", () -> {
                context.getInput().pressKey(NtglKeyBinds.KEY_ATTACHMENTS);
                context.waitTicks(15);
                context.takeScreenshot("21_attachments_screen");
                context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
                context.waitTicks(5);
            });

            step("workbench screen", () -> {
                server.runOnServer(minecraftServer -> {
                    var player = minecraftServer.getPlayerList().getPlayers().getFirst();
                    var level = player.level();
                    var pos = player.blockPosition().above(4);
                    level.setBlockAndUpdate(pos, ModBlocks.WORKBENCH.get().defaultBlockState());
                    if (level.getBlockEntity(pos) instanceof MenuProvider provider)
                        player.openMenu(provider);
                });
                context.waitTicks(15);
                context.takeScreenshot("22_workbench_screen");
                context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
                context.waitTicks(5);
            });

            step("dropped item", () -> {
                server.runCommand("execute at @p run summon item ^ ^1 ^2 {Item:{id:\"ntgl:pistol10mm\",count:1},NoGravity:1b}");
                context.waitTicks(20);
                context.takeScreenshot("30_dropped_item");
            });
        }
    }

    private static void hold(ClientGameTestContext context, TestServerContext server, String weapon) {
        server.runCommand("item replace entity @a weapon.mainhand with ntgl:" + weapon);
        context.waitTicks(5);
    }

    /** A failing step must not hide the results of the following ones. */
    private static void step(String name, Runnable action) {
        try {
            action.run();
        } catch (Throwable throwable) {
            System.err.println("[NTGL smoke test] step '" + name + "' failed: " + throwable);
            throwable.printStackTrace();
        }
    }
}
