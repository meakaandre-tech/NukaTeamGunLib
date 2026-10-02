package com.nukateam.ntgl.common.data.holders;

import com.nukateam.ntgl.common.data.WeaponData;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomAttack extends ResourceHolder {
    private static final Map<Identifier, CustomAttack> attackMap = new HashMap<>();
    private final Function<WeaponData, Boolean> onAttack;

    public CustomAttack(Identifier id, Function<WeaponData, Boolean> onAttack) {
        super(id);
        this.onAttack = onAttack;
    }

    public boolean attack(WeaponData data) {
        return onAttack.apply(data);
    }

    public static void registerType(CustomAttack mode) {
        attackMap.putIfAbsent(mode.getId(), mode);
    }

    public static CustomAttack getType(Identifier id) {
        return attackMap.get(id);
    }

    public static CustomAttack getType(String id) {
        return getType(Identifier.tryParse(id));
    }
}
