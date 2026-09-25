package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class SolarBatteryEvents {

    @SubscribeEvent
    public void onItemTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

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

