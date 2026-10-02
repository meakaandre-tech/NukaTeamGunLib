package com.nukateam.ntgl.common.foundation.blockentity;

import com.nukateam.ntgl.common.foundation.container.WorkbenchContainer;
import com.nukateam.ntgl.common.foundation.blockentity.inventory.IStorageBlock;
import com.nukateam.ntgl.common.foundation.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class WorkbenchBlockEntity extends SyncedBlockEntity implements IStorageBlock, net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider<BlockPos> {
    @Override
    public BlockPos getScreenOpeningData(net.minecraft.server.level.ServerPlayer player) {
        return this.worldPosition;
    }

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public WorkbenchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        ContainerHelper.loadAllItems(tag, inventory);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index != 0 || (stack.getItem() instanceof DyeItem && this.inventory.get(index).getCount() < 1);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level.getBlockEntity(this.worldPosition) == this && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.ntgl.workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new WorkbenchContainer(windowId, playerInventory, this);
    }
}
