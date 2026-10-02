package com.nukateam.ntgl.common.data.holders;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class MeleeMode extends ResourceHolder {
    public static final MeleeMode SINGLE = new MeleeMode(Identifier.tryBuild(Ntgl.MOD_ID, "single"));
    public static final MeleeMode AUTO = new MeleeMode(Identifier.tryBuild(Ntgl.MOD_ID, "auto"));

    private static final Map<Identifier, MeleeMode> loadingTypeMap = new HashMap<>();

    static {
        registerType(SINGLE);
        registerType(AUTO);
    }

    public MeleeMode(Identifier id) {
        super(id);
    }

    public static void registerType(MeleeMode mode) {
        loadingTypeMap.putIfAbsent(mode.getId(), mode);
    }

    public static MeleeMode getType(Identifier id) {
        return loadingTypeMap.getOrDefault(id, SINGLE);
    }

    public static MeleeMode getType(String id) {
        return getType(Identifier.tryParse(id));
    }
}
