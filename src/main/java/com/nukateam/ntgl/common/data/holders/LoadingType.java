package com.nukateam.ntgl.common.data.holders;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class LoadingType extends ResourceHolder {
    public static final LoadingType MAGAZINE = new LoadingType(Identifier.tryBuild(Ntgl.MOD_ID, "magazine"));
    public static final LoadingType PER_CARTRIDGE = new LoadingType(Identifier.tryBuild(Ntgl.MOD_ID, "per_cartridge"));

    private static final Map<Identifier, LoadingType> loadingTypeMap = new HashMap<>();

    static {
        registerType(MAGAZINE);
        registerType(PER_CARTRIDGE);
    }

    public LoadingType(Identifier id) {
        super(id);
    }

    public static void registerType(LoadingType mode) {
        loadingTypeMap.putIfAbsent(mode.getId(), mode);
    }

    public static LoadingType getType(Identifier id) {
        return loadingTypeMap.getOrDefault(id, MAGAZINE);
    }

    public static LoadingType getType(String id) {
        return getType(Identifier.tryParse(id));
    }
}
