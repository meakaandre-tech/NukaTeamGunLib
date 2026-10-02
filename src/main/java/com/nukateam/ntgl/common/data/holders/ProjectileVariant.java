package com.nukateam.ntgl.common.data.holders;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ProjectileVariant extends ResourceHolder {
    public static ProjectileVariant STANDARD = new ProjectileVariant("standard");

    private static final Map<Identifier, ProjectileVariant> typeMap = new HashMap<>();

    static {
        registerType(STANDARD);
    }

    public ProjectileVariant(Identifier id) {
        super(id);
    }

    public ProjectileVariant(String name) {
        super(name);
    }

    public Identifier getIcon() {
        return Identifier.tryBuild(id.getNamespace(), "textures/projectile/" + id.getPath() + ".png");
    }

    public static void registerType(ProjectileVariant mode) {
        typeMap.putIfAbsent(mode.getId(), mode);
    }

    public static ProjectileVariant getType(Identifier id) {
        return typeMap.getOrDefault(id, createDefault(id));
    }

    public static ProjectileVariant getType(String path) {
        var id = Identifier.tryParse(path);
        return getType(id);
    }

    private static ProjectileVariant createDefault(Identifier id){
        var type = new ProjectileVariant(id);
        registerType(type);
        return type;
    }
}
