package com.nukateam.ntgl.common.data.holders;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ThrowMode extends ResourceHolder {
    public static ThrowMode SAFE = new ThrowMode("safe");
    public static ThrowMode UNSAFE = new ThrowMode("unsafe");

    private static final Map<Identifier, ThrowMode> typeMap = new HashMap<>();

    static {
        registerType(SAFE);
        registerType(UNSAFE);
    }

    public ThrowMode(Identifier id) {
        super(id);
    }

    public ThrowMode(String name) {
        super(name);
    }

    public Identifier getIcon() {
        return Identifier.tryBuild(id.getNamespace(), "textures/hud/throw_mode/" + id.getPath() + ".png");
    }

    public static void registerType(ThrowMode mode) {
        typeMap.putIfAbsent(mode.getId(), mode);
    }

    public static ThrowMode getType(Identifier id) {
        return typeMap.getOrDefault(id, SAFE);
    }

    public static ThrowMode getType(String path) {
        var id = Identifier.tryParse(path);
        return getType(id);
    }
}
