package com.nukateam.ntgl.common.data.holders;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class WeaponModeMeta extends ResourceHolder {
    public static WeaponModeMeta DEFAULT = new WeaponModeMeta("default", Component.translatable("title.ntgl.default"));

    private static final Map<Identifier, WeaponModeMeta> typeMap = new HashMap<>();

    static {
        registerType(DEFAULT);
    }

    private final Component title;

    public WeaponModeMeta(Identifier id, Component title) {
        super(id);
        this.title = title;
    }

    public WeaponModeMeta(String name, Component title) {
        super(name);
        this.title = title;
    }

    public Identifier getIcon() {
        return Identifier.tryBuild(id.getNamespace(), "textures/hud/weapon_mode/" + id.getPath() + ".png");
    }

    public Component getTitle() {
        return title;
    }

    public static void registerType(WeaponModeMeta mode) {
        typeMap.putIfAbsent(mode.getId(), mode);
    }

    public static WeaponModeMeta getType(Identifier id) {
        return typeMap.getOrDefault(id, createDefault(id));
    }

    public static WeaponModeMeta getType(String path) {
        var id = Identifier.tryParse(path);
        return getType(id);
    }

    private static WeaponModeMeta createDefault(Identifier id){
        var type = new WeaponModeMeta(id, Component.translatable("title." + id.getNamespace() + "." + id.getPath()));
        registerType(type);
        return type;
    }
}
