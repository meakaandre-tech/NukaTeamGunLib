package com.nukateam.geo.render;

import com.nukateam.geo.interfaces.IResourceProvider;
import com.nukateam.geo.interfaces.IItemAnimator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.nukateam.ntgl.platform.Lazy;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;

import static com.geckolib.util.GeckoLibUtil.createInstanceCache;


public abstract class ItemAnimator implements GeoEntity, IItemAnimator, IResourceProvider {
    protected final AnimatableInstanceCache cache = createInstanceCache(this);
    protected final ItemDisplayContext transformType;
    protected ItemStack itemStack;
    private final Lazy<Identifier> id = Lazy.of(this::crateId);

    public ItemAnimator(ItemDisplayContext transformType) {
        this.transformType = transformType;
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }

    @Override
    public void setStack(ItemStack stack){
        this.itemStack = stack;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public ItemDisplayContext getTransformType() {
        return transformType;
    }

    @Override
    public Identifier getId() {
        return id.get();
    }

    private Identifier crateId() {
        var item = getStack().getItem();
        if (item instanceof IResourceProvider provider) {
            return provider.getId();
        } else {
            return getRegistryKey(item);
        }
    }

    @Nullable
    private static Identifier getRegistryKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
