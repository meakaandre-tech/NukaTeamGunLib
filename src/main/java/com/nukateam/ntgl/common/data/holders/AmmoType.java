package com.nukateam.ntgl.common.data.holders;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AmmoType extends ResourceHolder {
    public static AmmoType STANDARD = new AmmoType("standard");

    private static final Map<Identifier, AmmoType> typeMap = new HashMap<>();
    
    static {
        registerType(STANDARD);
    }

    public AmmoType(Identifier id) {
        super(id);
    }

    public AmmoType(String name) {
        super(name);
    }

    public Identifier getIcon() {
        return Identifier.tryBuild(id.getNamespace(), "textures/hud/ammo_type/" + id.getPath() + ".png");
    }

    public static void registerType(AmmoType mode) {
        typeMap.putIfAbsent(mode.getId(), mode);
    }

    public static AmmoType getType(Identifier id) {
        return typeMap.getOrDefault(id, createDefault(id));
    }

    public static AmmoType getType(String path) {
        var id = Identifier.tryParse(path);
        return getType(id);
    }

    private static AmmoType createDefault(Identifier id){
        var type = new AmmoType(id);
        registerType(type);
        return type;
    }
}
