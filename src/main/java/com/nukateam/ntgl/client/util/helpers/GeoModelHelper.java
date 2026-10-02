package com.nukateam.ntgl.client.util.helpers;

import com.nukateam.geo.interfaces.IResourceProvider;
import net.minecraft.resources.Identifier;

public class GeoModelHelper {
    public static Identifier getGunResource(IResourceProvider animator, String path, String extension) {
        var name  = animator.getId().getPath();
        var modId = animator.getId().getNamespace();

        return Identifier.tryBuild(modId, path + name + extension);
    }
}
