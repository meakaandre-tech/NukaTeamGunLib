package com.nukateam.ntgl.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Stand-in for NeoForge's DeferredRegister. Fabric registries are open during mod initialisation,
 * so entries are registered at once when {@code register(...)} is called (that is, when the class
 * holding the static fields is initialised from the mod initializer).
 */
public class DeferredRegister<R> {
    private final Registry<R> registry;
    private final ResourceKey<? extends Registry<R>> registryKey;
    private final String namespace;
    private final List<DeferredHolder<R, ? extends R>> entries = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected DeferredRegister(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
        this.registry = (Registry<R>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
        if (this.registry == null) {
            throw new IllegalStateException("Unknown registry " + registryKey);
        }
    }

    public static <R> DeferredRegister<R> create(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
        return new DeferredRegister<>(registryKey, namespace);
    }

    public static <R> DeferredRegister<R> create(Registry<R> registry, String namespace) {
        return new DeferredRegister<>(registry.key(), namespace);
    }

    public ResourceKey<R> key(String name) {
        return ResourceKey.create(registryKey, Identifier.fromNamespaceAndPath(namespace, name));
    }

    /** Registers an object that does not need to know its own id. */
    public <T extends R> DeferredHolder<R, T> register(String name, Supplier<? extends T> supplier) {
        return register(name, key -> supplier.get());
    }

    /** Registers an object built from its registry key (items, blocks and entity types need it). */
    public <T extends R> DeferredHolder<R, T> register(String name, Function<ResourceKey<R>, ? extends T> factory) {
        var key = key(name);
        T value = factory.apply(key);
        var holder = Registry.registerForHolder(registry, key, value);
        @SuppressWarnings("unchecked")
        var result = new DeferredHolder<R, T>(key, value, (net.minecraft.core.Holder<R>) (Object) holder);
        entries.add(result);
        return result;
    }

    /** Items: the factory receives properties that already carry the item id. */
    @SuppressWarnings("unchecked")
    public <T extends Item> DeferredHolder<Item, T> registerItem(String name, Function<Item.Properties, ? extends T> factory) {
        var self = (DeferredRegister<Item>) this;
        return self.register(name, (Function<ResourceKey<Item>, T>) key -> factory.apply(new Item.Properties().setId(key)));
    }

    /** Blocks: the factory receives properties that already carry the block id. */
    @SuppressWarnings("unchecked")
    public <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> factory, Supplier<BlockBehaviour.Properties> properties) {
        var self = (DeferredRegister<Block>) this;
        return self.register(name, (Function<ResourceKey<Block>, T>) key -> factory.apply(properties.get().setId(key)));
    }

    public Collection<DeferredHolder<R, ? extends R>> getEntries() {
        return entries;
    }

    public Registry<R> getRegistry() {
        return registry;
    }

    /** Kept for source compatibility: entries are already registered. */
    public void register() {
    }
}
