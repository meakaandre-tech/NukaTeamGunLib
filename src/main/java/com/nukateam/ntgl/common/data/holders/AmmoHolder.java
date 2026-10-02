package com.nukateam.ntgl.common.data.holders;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public class AmmoHolder extends ResourceHolder {
    private static final Map<Identifier, AmmoHolder> ammoTypeMap = new HashMap<>();

    private boolean canReturnAmmo = false;
    private Function<ItemStack, Boolean> isAcceptable = (stack) -> false;
    private Function<ItemStack, Integer> getValue = (stack) -> 1;
    private BiFunction<ItemStack, Integer, List<ItemStack>> onConsume = (stack, i) -> List.of();

    private Function<AmmoHolder, String> getDescriptionId = (ammo) ->
            "info." + ammo.id.getNamespace() + "." + ammo.id.getPath();

    public AmmoHolder(Identifier id) {
        super(id);
    }

    public boolean canReturnAmmo() {
        return canReturnAmmo;
    }

    public boolean isAcceptable(ItemStack ammoStack) {
        return isAcceptable.apply(ammoStack);
    }

    public int getValue(ItemStack ammoStack) {
        return getValue.apply(ammoStack);
    }

    public BiFunction<ItemStack, Integer, List<ItemStack>> onConsume() {
        return onConsume;
    }

    public String getDescriptionId() {
        return getDescriptionId.apply(this);
    }

    public static void registerType(AmmoHolder mode) {
        ammoTypeMap.putIfAbsent(mode.getId(), mode);
    }

    public static AmmoHolder getType(String id) {
        return getType(Identifier.tryParse(id));
    }

    public static AmmoHolder getType(Identifier id) {
        return ammoTypeMap.getOrDefault(id, createDefault(id));
    }

    private static AmmoHolder createDefault(Identifier id){
        var holder = Builder.create(id)
                .isAcceptable((stack) -> Objects.equals(getKey(stack), id))
                .value((s) -> 1)
                .descriptionId((ammo) -> {
                    var item = BuiltInRegistries.ITEM.getValue(ammo.getId());
                    if (item != null){
                        return item.getDescriptionId();
                    }
                    return "---";
                })
                .canReturnAmmo()
                .build();

        registerType(holder);
        return holder;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        else return other instanceof AmmoHolder holder
                    && id.getNamespace().equals(holder.id.getNamespace())
                    && this.id.getPath().equals(holder.id.getPath());
    }

    private static @Nullable Identifier getKey(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    public static class Builder {
        private final AmmoHolder holder;

        public Builder(AmmoHolder holder) {
            this.holder = holder;
        }

        public static Builder create(Identifier id) {
            var holder = new AmmoHolder(id);
            return new Builder(holder);
        }

        public Builder isAcceptable(Function<ItemStack, Boolean> isAcceptable) {
            holder.isAcceptable = isAcceptable;
            return this;
        }

        public Builder value(Function<ItemStack, Integer> getValue) {
            holder.getValue = getValue;
            return this;
        }

        public Builder onConsume(BiFunction<ItemStack, Integer, List<ItemStack>> onConsume) {
            holder.onConsume = onConsume;
            return this;
        }

        public Builder descriptionId(Function<AmmoHolder, String > getValue) {
            holder.getDescriptionId = getValue;
            return this;
        }

        public Builder canReturnAmmo() {
            holder.canReturnAmmo = true;
            return this;
        }

        public AmmoHolder build() {
            return holder;
        }

    }
}
