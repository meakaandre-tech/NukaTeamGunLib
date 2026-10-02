package com.nukateam.ntgl.common.util.util;

import com.nukateam.ntgl.Ntgl;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

public class ResourceUtils {
    public static Identifier modResource(String path) {
        return Identifier.tryBuild(Ntgl.MOD_ID, path);
    }

    public static String getResourceName(Identifier resourceLocation) {
        String path = resourceLocation.getPath();
        return FilenameUtils.removeExtension(FilenameUtils.getName(path));
    }

//    public static boolean resourceExists(Identifier path) {
//        var minecraft = Minecraft.getInstance();
//        var buff = minecraft.getResourceManager().listResources("textures", rl -> rl.equals(path));
//        return !buff.isEmpty();
//    }

    public static boolean resourceExists(Identifier path) {
        var minecraft = Minecraft.getInstance();
        var resource = minecraft.getResourceManager().getResource(path);
        return resource.isPresent();
    }
}
