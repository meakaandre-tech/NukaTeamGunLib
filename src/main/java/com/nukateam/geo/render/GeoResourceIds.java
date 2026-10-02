package com.nukateam.geo.render;

import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * GeckoLib 5 keys baked models/animations by an id without the directory prefix and file suffix
 * (<code>assets/ns/geckolib/models/weapons/gun.geo.json</code> -> <code>ns:weapons/gun</code>).
 * This converts the ids used by the GeckoLib 4 code (<code>ns:geo/weapons/gun.geo.json</code>) to that form.
 */
public final class GeoResourceIds {
    private static final Pattern PREFIX = Pattern.compile("^(geckolib/models/|geckolib/animations/|geo/|animations/)");
    private static final Pattern SUFFIX = Pattern.compile("((\\.geo)|(\\.animations?))?(\\.json)$");
    private static final Map<Identifier, Identifier> CACHE = new ConcurrentHashMap<>();

    private GeoResourceIds() {}

    public static Identifier strip(Identifier id) {
        if (id == null) return null;
        return CACHE.computeIfAbsent(id, key -> {
            var path = SUFFIX.matcher(PREFIX.matcher(key.getPath()).replaceFirst("")).replaceFirst("");
            return path.equals(key.getPath()) ? key : Identifier.fromNamespaceAndPath(key.getNamespace(), path);
        });
    }
}
