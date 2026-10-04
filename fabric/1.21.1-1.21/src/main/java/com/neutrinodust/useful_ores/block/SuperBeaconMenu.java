package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class SuperBeaconMenu extends BeaconMenu {
    private final ContainerLevelAccess access;

    public SuperBeaconMenu(int containerId, Container inventory, ContainerData beaconData, ContainerLevelAccess access) {
        super(containerId, inventory, beaconData, access);
        this.access = access;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModItems.SUPER_BEACON.get());
    }
}

