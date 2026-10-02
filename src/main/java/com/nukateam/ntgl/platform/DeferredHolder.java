package com.nukateam.ntgl.platform;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * Stand-in for NeoForge's DeferredHolder. On Fabric the object is registered immediately, so this
 * simply wraps the registered value, its key and its registry holder.
 */
public class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final ResourceKey<R> key;
    private final T value;
    private final Holder<R> holder;

    public DeferredHolder(ResourceKey<R> key, T value, Holder<R> holder) {
        this.key = key;
        this.value = value;
        this.holder = holder;
    }

    @Override
    public T get() {
        return value;
    }

    public T value() {
        return value;
    }

    public Identifier getId() {
        return key.identifier();
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    /** The registry holder (for APIs that want a Holder, such as mob effects or sound events). */
    public Holder<R> getHolder() {
        return holder;
    }

    public boolean is(Identifier id) {
        return key.identifier().equals(id);
    }
}
