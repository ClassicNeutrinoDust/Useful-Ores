package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class SolariteFurnaceMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public SolariteFurnaceMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(4), new SimpleContainerData(4), ContainerLevelAccess.NULL);
    }

    public SolariteFurnaceMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(ModMenuTypes.SOLARITE_FURNACE.get(), containerId);
        checkContainerSize(container, 4);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        this.access = access;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, SolariteFurnaceBlockEntity.SLOT_INPUT, 42, 17));

        this.addSlot(new Slot(container, SolariteFurnaceBlockEntity.SLOT_BATTERY, 42, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.SOLAR_BATTERY.get());
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(container, SolariteFurnaceBlockEntity.SLOT_DEAD_BATTERY, 67, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public int getMaxStackSize() {
                return SolariteFurnaceBlockEntity.DEAD_BATTERY_STACK_LIMIT;
            }
        });

        this.addSlot(new Slot(container, SolariteFurnaceBlockEntity.SLOT_OUTPUT, 126, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    public int getCookProgressScaled(int scale) {
        return data.get(1) <= 0 ? 0 : Math.min(scale, data.get(0) * scale / data.get(1));
    }

    public int getSourceMode() {
        return data.get(2);
    }

    public int getBatteryEnergyPercent() {
        return data.get(3);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModItems.SOLARITE_FURNACE_BLOCK.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    private static final int MENU_SLOT_INPUT = 0;
    private static final int MENU_SLOT_BATTERY = 1;
    private static final int MENU_SLOT_DEAD_BATTERY = 2;
    private static final int MENU_SLOT_OUTPUT = 3;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < 4) {

                if (!this.moveItemStackTo(stackInSlot, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {

                if (stackInSlot.is(ModItems.SOLAR_BATTERY.get())) {
                    if (!this.moveItemStackTo(stackInSlot, MENU_SLOT_BATTERY, MENU_SLOT_BATTERY + 1, false)) {
                        if (!this.moveItemStackTo(stackInSlot, MENU_SLOT_INPUT, MENU_SLOT_INPUT + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (!this.moveItemStackTo(stackInSlot, MENU_SLOT_INPUT, MENU_SLOT_INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            slot.onTake(player, stackInSlot);
        }
        return result;
    }
}

