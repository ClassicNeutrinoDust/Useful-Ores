package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SolarBatteryEvents {

    public static final SolarBatteryEvents INSTANCE = new SolarBatteryEvents();

    private SolarBatteryEvents() {}

    public void onItemEntityTick(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        if (level.isClientSide()) return;

        ItemStack stack = itemEntity.getItem();
        if (!stack.is(ModItems.SOLAR_BATTERY.get())) return;

        itemEntity.setUnlimitedLifetime();

        int energy = SolarBatteryItem.getEnergy(stack);
        if (energy >= SolarBatteryItem.MAX_ENERGY) return;

        BlockPos pos = itemEntity.blockPosition();

        boolean isDaytime = (level.getDayTime() % 24000L) < 12000L;
        if (isDaytime && level.canSeeSky(pos)) {
            SolarBatteryItem.setEnergy(stack, energy + 1);
        }
    }
}

