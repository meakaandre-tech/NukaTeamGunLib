package com.nukateam.ntgl.common.data.holders;

import com.nukateam.ntgl.Ntgl;
import com.nukateam.ntgl.client.input.NtglKeyBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WeaponMode extends ResourceHolder {
    private static final Map<Identifier, WeaponMode> loadingTypeMap = new HashMap<>();

    public static final WeaponMode PRIMARY     = new WeaponMode("primary"    , 0xC6C6C6FF);
    public static final WeaponMode SECONDARY   = new WeaponMode("secondary"  , 0x7CBCE0FF);
    public static final WeaponMode ADDITIONAL  = new WeaponMode("additional" , 0x7DEE7CFF);
    public static final WeaponMode ALTERNATIVE = new WeaponMode("alternative", 0xFF8484FF);

    private final int color;

    static {
        registerType(PRIMARY    );
        registerType(SECONDARY  );
        registerType(ADDITIONAL );
        registerType(ALTERNATIVE);
    }

    private WeaponMode(String id, int color) {
        super(Identifier.tryBuild(Ntgl.MOD_ID, id));
        this.color = color;
    }

    public WeaponMode(Identifier id, int color) {
        super(id);
        this.color = color;
    }

    public static void registerType(WeaponMode mode) {
        loadingTypeMap.putIfAbsent(mode.getId(), mode);
    }

    public static WeaponMode getType(Identifier id) {
        return loadingTypeMap.getOrDefault(id, PRIMARY);
    }

    public static WeaponMode getType(String id) {
        return getType(Identifier.tryParse(id));
    }


    public int getColor() {
        return color;
    }
}
