package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SolariteFurnacePartBlockEntity extends BlockEntity implements WorldlyContainer {

    public SolariteFurnacePartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLARITE_FURNACE_PART, pos, state);
    }

    private WorldlyContainer controller() {
        if (level == null) return null;
        BlockPos controllerPos = SolariteFurnacePartBlock.controllerPos(getBlockState(), worldPosition);
        BlockEntity be = level.getBlockEntity(controllerPos);
        return be instanceof SolariteFurnaceBlockEntity furnace ? furnace : null;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        WorldlyContainer c = controller();
        return c != null ? c.getSlotsForFace(side) : new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        WorldlyContainer c = controller();
        return c != null && c.canPlaceItemThroughFace(slot, stack, dir);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        WorldlyContainer c = controller();
        return c != null && c.canTakeItemThroughFace(slot, stack, dir);
    }

    @Override
    public int getContainerSize() {
        Container c = controller();
        return c != null ? c.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        Container c = controller();
        return c == null || c.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        Container c = controller();
        return c != null ? c.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        Container c = controller();
        return c != null ? c.removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        Container c = controller();
        return c != null ? c.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        Container c = controller();
        if (c != null) c.setItem(slot, stack);
    }

    @Override
    public void setChanged() {

        Container c = controller();
        if (c != null) c.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        Container c = controller();
        return c != null && c.stillValid(player);
    }

    @Override
    public void clearContent() {
        Container c = controller();
        if (c != null) c.clearContent();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        Container c = controller();
        return c != null && c.canPlaceItem(slot, stack);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
}

