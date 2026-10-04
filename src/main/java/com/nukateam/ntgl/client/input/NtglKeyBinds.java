package com.nukateam.ntgl.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.nukateam.ntgl.Config;
import com.nukateam.ntgl.Ntgl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.resources.Identifier;

public class NtglKeyBinds {
    // Category ids become the translation keys key.category.ntgl.<path> (see the lang files)
    public static final KeyMapping.Category NTGL_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "ntgl"));
    public static final KeyMapping.Category ARMOR_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "armor"));
    public static final KeyMapping.Category DEBUG_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Ntgl.MOD_ID, "debug"));

    public static final KeyMapping KEY_RELOAD = new KeyMapping("key.ntgl.reload", InputConstants.KEY_R, NTGL_CATEGORY);
    public static final KeyMapping KEY_UNLOAD = new KeyMapping("key.ntgl.unload", InputConstants.KEY_U, NTGL_CATEGORY);
    public static final KeyMapping KEY_ATTACHMENTS = new KeyMapping("key.ntgl.attachments", InputConstants.KEY_Z, NTGL_CATEGORY);
    public static final KeyMapping KEY_INSPECT = new KeyMapping("key.ntgl.inspect", InputConstants.KEY_I, NTGL_CATEGORY);
    public static final KeyMapping KEY_FIRE_SELECT = new KeyMapping("key.ntgl.fire_select", InputConstants.KEY_B, NTGL_CATEGORY);
    public static final KeyMapping KEY_AMMO_SELECT = new KeyMapping("key.ntgl.ammo_select", InputConstants.KEY_N, NTGL_CATEGORY);
    public static final KeyMapping KEY_ADD_ATTACK = new KeyMapping("key.ntgl.melee", InputConstants.KEY_V, NTGL_CATEGORY);
    public static final KeyMapping KEY_ALT_ATTACK = new KeyMapping("key.ntgl.alt", InputConstants.KEY_LALT, NTGL_CATEGORY);
    public static final KeyMapping KEY_TIPS = new KeyMapping("key.ntgl.key_tips", InputConstants.KEY_F4, NTGL_CATEGORY);

    public static final KeyMapping KEY_DEBUG_X_ADD = new KeyMapping("key.ntgl.debug_x_add", InputConstants.KEY_LEFT, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_Y_ADD = new KeyMapping("key.ntgl.debug_y_add", InputConstants.KEY_DOWN, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_Z_ADD = new KeyMapping("key.ntgl.debug_z_add", InputConstants.KEY_PAGEUP, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_X_SUB = new KeyMapping("key.ntgl.debug_x_sub", InputConstants.KEY_RIGHT, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_Y_SUB = new KeyMapping("key.ntgl.debug_y_sub", InputConstants.KEY_UP, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_Z_SUB = new KeyMapping("key.ntgl.debug_z_sub", InputConstants.KEY_PAGEDOWN, DEBUG_CATEGORY);

    public static final KeyMapping KEY_DEBUG_ZERO = new KeyMapping("key.ntgl.debug_zero", InputConstants.KEY_NUMPADENTER, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_SHOW = new KeyMapping("key.ntgl.debug_show", InputConstants.KEY_MULTIPLY, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_TUNING_MODE = new KeyMapping("key.ntgl.debug_tuning_mode", InputConstants.KEY_NUMPAD0, DEBUG_CATEGORY);

    public static final KeyMapping KEY_DEBUG_RX_ADD = new KeyMapping("key.ntgl.debug_rx_add", InputConstants.KEY_NUMPAD4, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_RY_ADD = new KeyMapping("key.ntgl.debug_ry_add", InputConstants.KEY_NUMPAD5, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_RZ_ADD = new KeyMapping("key.ntgl.debug_rz_add", InputConstants.KEY_NUMPAD6, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_RX_SUB = new KeyMapping("key.ntgl.debug_rx_sub", InputConstants.KEY_NUMPAD1, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_RY_SUB = new KeyMapping("key.ntgl.debug_ry_sub", InputConstants.KEY_NUMPAD2, DEBUG_CATEGORY);
    public static final KeyMapping KEY_DEBUG_RZ_SUB = new KeyMapping("key.ntgl.debug_rz_sub", InputConstants.KEY_NUMPAD3, DEBUG_CATEGORY);

    public static final KeyMapping LEAVE = new KeyMapping("key.ntgl.leave",
            InputConstants.Type.KEYBOARD, InputConstants.KEY_G, ARMOR_CATEGORY);

    public static void registerKeyMappings() {
        KeyMappingHelper.registerKeyMapping(KEY_RELOAD);
        KeyMappingHelper.registerKeyMapping(KEY_UNLOAD);
        KeyMappingHelper.registerKeyMapping(KEY_ATTACHMENTS);
        KeyMappingHelper.registerKeyMapping(KEY_INSPECT);
        KeyMappingHelper.registerKeyMapping(KEY_FIRE_SELECT);
        KeyMappingHelper.registerKeyMapping(KEY_AMMO_SELECT);
        KeyMappingHelper.registerKeyMapping(KEY_ADD_ATTACK);
        KeyMappingHelper.registerKeyMapping(KEY_ALT_ATTACK);
        KeyMappingHelper.registerKeyMapping(KEY_TIPS);
        KeyMappingHelper.registerKeyMapping(LEAVE);

        if(Ntgl.isDebugging()){
            registerDebugKeys();
        }
    }

    private static void registerDebugKeys() {
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_X_ADD);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_X_SUB);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_Y_ADD);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_Y_SUB);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_Z_ADD);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_Z_SUB);

        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_RX_ADD);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_RX_SUB);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_RY_ADD);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_RY_SUB);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_RZ_ADD);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_RZ_SUB);

        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_ZERO);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_SHOW);
        KeyMappingHelper.registerKeyMapping(KEY_DEBUG_TUNING_MODE);
    }

    public static KeyMapping getAimMapping() {
        Minecraft mc = Minecraft.getInstance();
        return Config.CLIENT.controls.flipControls.get() ? mc.options.keyAttack : mc.options.keyUse;
    }

    public static KeyMapping getShootMapping() {
        Minecraft mc = Minecraft.getInstance();
        return Config.CLIENT.controls.flipControls.get() ? mc.options.keyUse : mc.options.keyAttack;
    }
}
