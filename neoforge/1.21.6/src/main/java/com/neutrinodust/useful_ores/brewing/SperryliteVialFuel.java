package com.neutrinodust.useful_ores.brewing;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public final class SperryliteVialFuel {

    static final int FUEL_PER_POWDER = 20;

    private static final Map<BrewingStandBlockEntity, Integer> CHARGES = new WeakHashMap<>();

    private SperryliteVialFuel() {
    }

    public static int charges(BrewingStandBlockEntity brewingStand) {
        return CHARGES.getOrDefault(brewingStand, 0);
    }

    public static boolean ensureCharge(BrewingStandBlockEntity brewingStand) {
        if (CHARGES.getOrDefault(brewingStand, 0) > 0) return true;

        ItemStack fuel = brewingStand.getItem(4);
        if (fuel.isEmpty() || !fuel.is(Items.BLAZE_POWDER)) return false;

        fuel.shrink(1);
        if (fuel.isEmpty()) brewingStand.setItem(4, ItemStack.EMPTY);
        CHARGES.put(brewingStand, FUEL_PER_POWDER);
        return true;
    }

    
    public static boolean primeCharge(BrewingStandBlockEntity brewingStand) {
        if (charges(brewingStand) > 0) return true;

        ItemStack fuel = brewingStand.getItem(4);
        if (fuel.isEmpty() || !fuel.is(Items.BLAZE_POWDER)) return false;

        fuel.shrink(1);
        if (fuel.isEmpty()) {
            brewingStand.setItem(4, ItemStack.EMPTY);
        }
        CHARGES.put(brewingStand, FUEL_PER_POWDER);
        brewingStand.setChanged();
        return true;
    }

    public static void loadCharges(BrewingStandBlockEntity brewingStand, int charges) {
        if (charges > 0) CHARGES.put(brewingStand, charges);
        else CHARGES.remove(brewingStand);
    }

    public static void spendCharge(BrewingStandBlockEntity brewingStand) {
        int remaining = CHARGES.getOrDefault(brewingStand, 0) - 1;
        if (remaining <= 0) CHARGES.remove(brewingStand);
        else CHARGES.put(brewingStand, remaining);
    }
}

