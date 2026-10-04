package com.neutrinodust.useful_ores.farseeker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Iterator;

public class FarseekerBlockEvents {

    @SubscribeEvent
    public void onBlockDrops(BlockDropsEvent event) {
        Entity breaker = event.getBreaker();
        if (!(breaker instanceof Player player) || player.level().isClientSide()) return;

        ItemStack tool = event.getTool();
        if (tool == null || !ModFarseekerComponents.has(tool)) return;

        Iterator<ItemEntity> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemEntity itemEntity = iterator.next();
            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) continue;

            boolean added = player.getInventory().add(stack);
            if (added && stack.isEmpty()) {

                iterator.remove();
            }

        }
    }
}

