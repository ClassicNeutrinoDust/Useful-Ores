package com.neutrinodust.useful_ores.filter;

import com.neutrinodust.useful_ores.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArgentiteFilterMenu extends AbstractContainerMenu {

    private final Container container;

    public ArgentiteFilterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(ArgentiteFilterBlockEntity.CONTAINER_SIZE));
    }

    public ArgentiteFilterMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.ARGENTITE_FILTER, containerId);
        checkContainerSize(container, ArgentiteFilterBlockEntity.CONTAINER_SIZE);
        this.container = container;
        container.startOpen(playerInventory.player);

        for (int x = 0; x < ArgentiteFilterBlockEntity.CONTAINER_SIZE; x++) {
            this.addSlot(new Slot(container, x, 44 + x * 18, 20));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < ArgentiteFilterBlockEntity.CONTAINER_SIZE) {

                if (!this.moveItemStackTo(stackInSlot, ArgentiteFilterBlockEntity.CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {

                if (!this.moveItemStackTo(stackInSlot, 0, ArgentiteFilterBlockEntity.CONTAINER_SIZE, false)) {
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

