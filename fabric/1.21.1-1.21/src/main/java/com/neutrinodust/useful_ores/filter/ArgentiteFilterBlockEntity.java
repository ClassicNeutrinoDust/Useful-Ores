package com.neutrinodust.useful_ores.filter;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class ArgentiteFilterBlockEntity extends BlockEntity implements Container, WorldlyContainer, Hopper, MenuProvider {

    public static final int CONTAINER_SIZE = 5;
    private static final Component DEFAULT_NAME = Component.translatable("container.useful_ores.argentite_filter");

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    private final int[] reservedCount = new int[CONTAINER_SIZE];

    private int cooldownTime = -1;
    private long tickedGameTime;
    private Direction facing;

    private int lastObservedTotal = 0;

    public ArgentiteFilterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARGENTITE_FILTER, pos, state);
        this.facing = state.getValue(ArgentiteFilterBlock.FACING);
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        this.facing = state.getValue(ArgentiteFilterBlock.FACING);
    }

    private int effectiveReserve(int slot) {
        int present = items.get(slot).getCount();
        if (reservedCount[slot] > present) {
            reservedCount[slot] = present;
        }
        return reservedCount[slot];
    }

    private int getSurplus(int slot) {
        ItemStack stack = items.get(slot);
        if (stack.isEmpty()) return 0;
        return stack.getCount() - effectiveReserve(slot);
    }

    private boolean hasSurplus() {
        for (int slot = 0; slot < CONTAINER_SIZE; slot++) {
            if (getSurplus(slot) > 0) return true;
        }
        return false;
    }

    private int totalItemCount() {
        int total = 0;
        for (ItemStack stack : items) total += stack.getCount();
        return total;
    }

    private boolean bufferFull() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty() && stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private ItemStack extractSurplusOne(int slot) {
        if (getSurplus(slot) <= 0) return ItemStack.EMPTY;
        ItemStack stack = items.get(slot);
        ItemStack extracted = stack.copyWithCount(1);
        stack.shrink(1);
        setChanged();
        return extracted;
    }

    private void restoreOne(int slot) {
        items.get(slot).grow(1);
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) {
            effectiveReserve(slot);
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(items, slot);
        effectiveReserve(slot);
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(getMaxStackSize(stack));
        reservedCount[slot] = stack.isEmpty() ? 0 : stack.getCount();

        lastObservedTotal = totalItemCount();
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        for (int i = 0; i < CONTAINER_SIZE; i++) reservedCount[i] = 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= CONTAINER_SIZE || stack.isEmpty()) return false;
        ItemStack current = items.get(slot);
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, stack)) return false;
        return current.getCount() < current.getMaxStackSize();
    }

    @Override
    public boolean canTakeItem(Container into, int slot, ItemStack stack) {
        return getSurplus(slot) > 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1, 2, 3, 4};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return canTakeItem(this, slot, stack);
    }

    @Override
    public double getLevelX() {
        return worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return worldPosition.getZ() + 0.5;
    }

    @Override
    public boolean isGridAligned() {
        return true;
    }

    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, ArgentiteFilterBlockEntity entity) {
        entity.cooldownTime--;
        entity.tickedGameTime = level.getGameTime();

        int totalNow = entity.totalItemCount();
        if (totalNow > entity.lastObservedTotal && !entity.isOnCooldown()) {
            entity.setCooldown(8);
        }

        if (!entity.isOnCooldown()) {
            entity.setCooldown(0);
            tryMoveItems(level, pos, state, entity, () -> suckInItems(level, entity));
        }

        entity.lastObservedTotal = entity.totalItemCount();
    }

    private static boolean tryMoveItems(Level level, BlockPos pos, BlockState state, ArgentiteFilterBlockEntity entity, BooleanSupplier action) {
        if (level.isClientSide()) return false;

        if (!entity.isOnCooldown() && state.getValue(ArgentiteFilterBlock.ENABLED)) {
            boolean changed = false;
            if (entity.hasSurplus()) {
                changed = ejectSurplus(level, pos, entity);
            }
            if (!entity.bufferFull()) {
                changed |= action.getAsBoolean();
            }
            if (changed) {
                entity.setCooldown(8);
                setChanged(level, pos, state);
                return true;
            }
        }
        return false;
    }

    private static boolean ejectSurplus(Level level, BlockPos pos, ArgentiteFilterBlockEntity self) {
        Container container = HopperBlockEntity.getContainerAt(level, pos.relative(self.facing));
        if (container == null) return false;

        Direction direction = self.facing.getOpposite();
        if (isFullContainer(container, direction)) return false;

        for (int slot = 0; slot < CONTAINER_SIZE; slot++) {
            if (self.getSurplus(slot) <= 0) continue;

            ItemStack toMove = self.extractSurplusOne(slot);
            if (toMove.isEmpty()) continue;

            ItemStack result = HopperBlockEntity.addItem(self, container, toMove, direction);
            if (result.isEmpty()) {
                container.setChanged();
                return true;
            } else {
                self.restoreOne(slot);
            }
        }
        return false;
    }

    private static boolean suckInItems(Level level, ArgentiteFilterBlockEntity hopper) {
        BlockPos abovePos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        BlockState aboveState = level.getBlockState(abovePos);
        Container container = HopperBlockEntity.getContainerAt(level, abovePos);
        if (container != null) {
            Direction direction = Direction.DOWN;
            for (int slot : getSlots(container, direction)) {
                if (tryTakeInItemFromSlot(hopper, container, slot, direction)) return true;
            }
            return false;
        } else {
            boolean isBlocked = hopper.isGridAligned()
                    && aboveState.isCollisionShapeFullBlock(level, abovePos)
                    && !aboveState.is(BlockTags.DOES_NOT_BLOCK_HOPPERS);
            if (!isBlocked) {
                for (ItemEntity entity : HopperBlockEntity.getItemsAtAndAbove(level, hopper)) {
                    if (HopperBlockEntity.addItem(hopper, entity)) return true;
                }
            }
            return false;
        }
    }

    private static boolean tryTakeInItemFromSlot(ArgentiteFilterBlockEntity hopper, Container container, int slot, Direction direction) {
        ItemStack stack = container.getItem(slot);
        if (!stack.isEmpty() && canTakeItemFromContainer(hopper, container, stack, slot, direction)) {
            int originalCount = stack.getCount();
            ItemStack result = HopperBlockEntity.addItem(container, hopper, container.removeItem(slot, 1), null);
            if (result.isEmpty()) {
                container.setChanged();
                return true;
            }
            stack.setCount(originalCount);
            if (originalCount == 1) {
                container.setItem(slot, stack);
            }
        }
        return false;
    }

    private static boolean canTakeItemFromContainer(Container into, Container from, ItemStack stack, int slot, Direction direction) {
        if (!from.canTakeItem(into, slot, stack)) return false;
        return !(from instanceof WorldlyContainer worldly) || worldly.canTakeItemThroughFace(slot, stack, direction);
    }

    private static int[] getSlots(Container container, Direction direction) {
        if (container instanceof WorldlyContainer worldly) {
            return worldly.getSlotsForFace(direction);
        }
        int size = container.getContainerSize();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) slots[i] = i;
        return slots;
    }

    private static boolean isFullContainer(Container container, Direction direction) {
        for (int slot : getSlots(container, direction)) {
            ItemStack stack = container.getItem(slot);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    public static void entityInside(Level level, BlockPos pos, BlockState state, Entity entity, ArgentiteFilterBlockEntity hopper) {
        if (entity instanceof ItemEntity itemEntity
                && !itemEntity.getItem().isEmpty()
                && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(hopper.getSuckAabb())) {
            tryMoveItems(level, pos, state, hopper, () -> HopperBlockEntity.addItem(hopper, itemEntity));
        }
    }

    private void setCooldown(int time) {
        this.cooldownTime = time;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        ContainerHelper.saveAllItems(output, items, registries);
        int[] copy = reservedCount.clone();
        output.putIntArray("ReservedCounts", copy);
        output.putInt("TransferCooldown", cooldownTime);
    }

    @Override
    protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items, registries);
        int[] loaded = input.getIntArray("ReservedCounts");
        for (int i = 0; i < CONTAINER_SIZE && i < loaded.length; i++) {
            reservedCount[i] = loaded[i];
        }
        cooldownTime = NbtCompat.getIntOr(input, "TransferCooldown", -1);
        lastObservedTotal = totalItemCount();
    }


    @Override
    public Component getDisplayName() {
        return DEFAULT_NAME;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ArgentiteFilterMenu(containerId, inventory, this);
    }
}

