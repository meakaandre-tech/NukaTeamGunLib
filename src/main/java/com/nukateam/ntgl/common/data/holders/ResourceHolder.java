package com.nukateam.ntgl.common.data.holders;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.resources.Identifier;

public class ResourceHolder {
    protected final Identifier id;

    public ResourceHolder(Identifier id) {
        this.id = id;
    }

    public ResourceHolder(String name) {
        this.id = Identifier.tryBuild(Ntgl.MOD_ID, name);
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean equals(Identifier obj) {
        return this.id.equals(obj);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
