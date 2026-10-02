package com.nukateam.ntgl.common.data.holders;

import com.google.gson.JsonParseException;
import com.nukateam.ntgl.Ntgl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AttachmentType {
    public static AttachmentType NONE         = new AttachmentType("none");
    public static AttachmentType SCOPE        = new AttachmentType("scope");
    public static AttachmentType BARREL       = new AttachmentType("barrel");
    public static AttachmentType STOCK        = new AttachmentType("stock");
    public static AttachmentType GRIP         = new AttachmentType("grip");
    public static AttachmentType UNDER_BARREL = new AttachmentType("under_barrel");
    public static AttachmentType MAGAZINE     = new AttachmentType("magazine");
    public static AttachmentType MUZZLE       = new AttachmentType("muzzle");
    public static AttachmentType MELEE        = new AttachmentType("melee");

    private static final Map<Identifier, AttachmentType> typeMap = new HashMap<>();
    
    static {
        registerType(NONE       );
        registerType(SCOPE       );
        registerType(BARREL      );
        registerType(STOCK       );
        registerType(GRIP        );
        registerType(UNDER_BARREL);
        registerType(MAGAZINE    );
        registerType(MUZZLE      );
        registerType(MELEE      );
    }

    private final Identifier id;

    public AttachmentType(Identifier id) {
        this.id = id;
    }

    private AttachmentType(String name) {
        this.id = Identifier.tryBuild(Ntgl.MOD_ID, name);
    }

    public static void registerType(AttachmentType mode) {
        typeMap.putIfAbsent(mode.getId(), mode);
    }

    public static AttachmentType getType(Identifier id) {
        var type = typeMap.get(id);
        if(type == null){
            throw new JsonParseException("Attachment type \"" + id.toString() + "\" doesn't exists");
        }
        return type;
    }

    public static AttachmentType getType(String path) {
        var id = Identifier.tryParse(path);
        return getType(id);
    }

    public Identifier getId() {
        return this.id;
    }

    public Identifier getIcon() {
        return Identifier.tryBuild(getId().getNamespace(), "textures/gui/icons/" + getId().getPath() + ".png");
    }

    public String getTranslationKey(){
        return "slot."+getId().getNamespace()+".attachment." + getId().getPath();
    }

    public MutableComponent getTranslationComponent(){
        return Component.translatable(this.getTranslationKey());
    }

    public boolean equals(Identifier obj) {
        return this == getType(obj);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
