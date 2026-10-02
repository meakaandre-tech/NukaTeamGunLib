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

            context.getInput().resizeWindow(1280, 720);
            connection.waitForChunksRender();
            server.runCommand("gamemode creative @a");
            server.runCommand("time set noon");
            server.runCommand("weather clear");

            hold(context, server, "pistol10mm");
            server.runCommand("give @a ntgl:round10mm 2");
            context.waitTicks(30);
            context.takeScreenshot("01_pistol_first_person");
            step("diagnostics", () -> context.runOnClient(NtglSmokeTest::logDiagnostics));

            step("input state", () -> context.runOnClient(mc -> logInput(mc, "before input")));

            step("reload", () -> {
                context.getInput().holdKeyFor(NtglKeyBinds.KEY_RELOAD, 4);
                context.waitTicks(10);
                context.takeScreenshot("02_pistol_reloading");
                context.waitTicks(70);
                context.runOnClient(mc -> logInput(mc, "after reload"));
            });

            step("shoot", () -> {
                context.getInput().holdMouse(0);
                context.waitTicks(3);
                context.runOnClient(mc -> logInput(mc, "while shooting"));
                context.takeScreenshot("03_pistol_shooting");
                context.getInput().releaseMouse(0);
                context.waitTicks(20);
                context.runOnClient(mc -> logInput(mc, "after shooting"));
            });

            step("aim", () -> {
                context.getInput().holdMouse(1);
                context.waitTicks(15);
                context.runOnClient(mc -> logInput(mc, "while aiming"));
                context.takeScreenshot("04_pistol_aiming");
                context.getInput().releaseMouse(1);
                context.waitTicks(10);
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

            step("dropped item", () -> {
                server.runCommand("execute at @p run summon item ^ ^1 ^2 {Item:{id:\"ntgl:pistol10mm\",count:1},NoGravity:1b}");
                context.waitTicks(20);
                context.takeScreenshot("30_dropped_item");
            });
            step("workbench screen", () -> {
                server.runOnServer(minecraftServer -> {
                    var player = minecraftServer.getPlayerList().getPlayers().getFirst();
                    var level = player.level();
                    var pos = player.blockPosition().above(4);
                    level.setBlockAndUpdate(pos, ModBlocks.WORKBENCH.get().defaultBlockState());
                });
                context.waitTicks(10);
                server.runOnServer(minecraftServer -> {
                    var player = minecraftServer.getPlayerList().getPlayers().getFirst();
                    if (player.level().getBlockEntity(player.blockPosition().above(4)) instanceof MenuProvider provider)
                        player.openMenu(provider);
                });
                context.waitTicks(15);
                context.takeScreenshot("22_workbench_screen");
                context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
                context.waitTicks(5);
            });

        }
    }

    private static void logDiagnostics(net.minecraft.client.Minecraft mc) {
        var log = com.nukateam.ntgl.Ntgl.LOGGER;
        var animations = com.geckolib.cache.GeckoLibResources.getBakedAnimations().cache();
        var models = com.geckolib.cache.GeckoLibResources.getBakedModels().cache();
        log.info("[smoke] animation ids: {}", animations.keySet().stream().filter(id -> id.getNamespace().equals("ntgl")).sorted().toList());
        log.info("[smoke] model ids: {}", models.keySet().stream().filter(id -> id.getNamespace().equals("ntgl")).sorted().toList());

        var player = mc.player;
        var stack = player.getMainHandItem();
        log.info("[smoke] held: {} components {}", stack, stack.getComponentsPatch());
        var transform = com.nukateam.ntgl.client.util.helpers.render.ModelRenderUtil.getTransform(stack,
                net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, player);
        log.info("[smoke] first person transform: {}", transform);
        log.info("[smoke] hand heights: main {} old {}", mc.gameRenderer.itemInHandRenderer.mainHandHeight, mc.gameRenderer.itemInHandRenderer.oMainHandHeight);

        var renderer = com.nukateam.ntgl.client.registry.WeaponRegistry.getRenderer(stack.getItem());
        var animator = (com.nukateam.ntgl.client.animators.WeaponAnimator) renderer.getAnimator(player,
                net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, stack);
        var model = com.nukateam.ntgl.client.model.gun.GeoWeaponModel.INSTANCE;
        log.info("[smoke] animator id {} animation resource {} model {} texture {}", animator.getId(),
                model.getAnimationResource(animator), model.getModelResource(animator), model.getTextureResource(animator));
        var helper = new com.nukateam.ntgl.common.util.util.AnimationHelper<>(animator, model);
        log.info("[smoke] has hold {} shot {} reload {} hold length {}", helper.hasAnimation("hold"), helper.hasAnimation("shot"),
                helper.hasAnimation("reload"), helper.getAnimationDuration("hold"));
        var manager = animator.getAnimatableInstanceCache().getManagerForId(animator.hashCode());
        manager.getAnimationControllers().forEach((name, controller) -> log.info("[smoke] controller {} state {} animation {} animating bones {}",
                name, controller.getPlayState(), controller.getCurrentRawAnimation(), controller.isAnimatingBones()));
        log.info("[smoke] muzzle matrix first person {} third person {}", com.nukateam.ntgl.client.helpers.MuzzleMatrixHelper.lastMuzzleMatrix != null,
                com.nukateam.ntgl.client.helpers.MuzzleMatrixHelper.lastThirdPersonMuzzleMatrix != null);
    }

    private static void logInput(net.minecraft.client.Minecraft mc, String when) {
        var stack = mc.player.getMainHandItem();
        var data = new com.nukateam.ntgl.common.data.WeaponData(stack, mc.player);
        com.nukateam.ntgl.Ntgl.LOGGER.info("[smoke] {}: inGame {} mouseGrabbed {} windowActive {} screen {} attackDown {} useDown {} aiming {} ads {} ammo {} shooting {} reloading {} entities {}",
                when,
                com.nukateam.ntgl.client.util.handler.ClientShootingHandler.isInGame(), mc.mouseHandler.isMouseGrabbed(), mc.isWindowActive(),
                mc.gui.screen(), mc.options.keyAttack.isDown(), mc.options.keyUse.isDown(),
                com.nukateam.ntgl.client.util.handler.AimingHandler.get().isAiming(),
                com.nukateam.ntgl.client.util.handler.AimingHandler.get().getNormalisedAdsProgress(),
                com.nukateam.ntgl.common.util.util.WeaponStateHelper.getAmmoCount(data),
                com.nukateam.ntgl.client.util.handler.ClientShootingHandler.get().isShooting(mc.player, net.minecraft.world.InteractionHand.MAIN_HAND),
                com.nukateam.ntgl.client.util.handler.ClientReloadHandler.get().isReloading(mc.player, net.minecraft.world.InteractionHand.MAIN_HAND),
                mc.level.entitiesForRendering() == null ? -1 : java.util.stream.StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false)
                        .map(e -> e.getType().toShortString()).toList());
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
