package com.neutrinodust.useful_ores.brewing;

import java.util.WeakHashMap;
import java.util.Map;

import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public final class VialConversionBrewing {

    public static final int BREW_TICKS = 400;

    private static final Map<BrewingStandBlockEntity, Integer> BREW_PROGRESS = new WeakHashMap<>();
    private VialConversionBrewing() {
    }

    public static int fuelCharges(BrewingStandBlockEntity brewingStand) {
        return SperryliteVialFuel.charges(brewingStand);
    }

    public static int remainingTicks(BrewingStandBlockEntity brewingStand) {
        Integer progress = BREW_PROGRESS.get(brewingStand);
        if (progress == null) return -1;
        return Math.max(0, BREW_TICKS - progress);
    }

    public static boolean tick(Level level, BrewingStandBlockEntity brewingStand) {
        if (level.isClientSide()) {
            return matches(brewingStand) != Target.NONE;
        }

        Target target = matches(brewingStand);
        if (target == Target.NONE) {
            BREW_PROGRESS.remove(brewingStand);
            return false;
        }

        if (!SperryliteVialFuel.ensureCharge(brewingStand)) {
            BREW_PROGRESS.remove(brewingStand);
            return false;
        }

        int progress = BREW_PROGRESS.getOrDefault(brewingStand, 0) + 1;
        if (progress >= BREW_TICKS) {
            BREW_PROGRESS.remove(brewingStand);
            SperryliteVialFuel.spendCharge(brewingStand);
            brew(brewingStand, target);
        } else {
            BREW_PROGRESS.put(brewingStand, progress);
        }
        return true;
    }

    private enum Target { NONE, SPLASH, LINGERING }

    public static boolean matchesRecipe(Container brewingStand) {
        return matches(brewingStand) != Target.NONE;
    }

    private static Target matches(Container brewingStand) {
        if (brewingStand.getContainerSize() < 5) return Target.NONE;

        ItemStack ingredient = brewingStand.getItem(3);
        if (ingredient.isEmpty()) return Target.NONE;

        Target target;
        Item source;
        if (ingredient.is(Items.GUNPOWDER)) {
            target = Target.SPLASH;
            source = ModItems.SPERRYLITE_CATALYTIC_VIAL.get();
        } else if (ingredient.is(Items.DRAGON_BREATH)) {
            target = Target.LINGERING;
            source = ModItems.SPERRYLITE_CATALYTIC_VIAL_SPLASH.get();
        } else {
            return Target.NONE;
        }

        for (int i = 0; i < 3; i++) {
            ItemStack stack = brewingStand.getItem(i);
            if (stack.is(source) && stack.has(DataComponents.POTION_CONTENTS)) {
                return target;
            }
        }
        return Target.NONE;
    }

    private static void brew(Container brewingStand, Target target) {
        Item source = target == Target.SPLASH
            ? ModItems.SPERRYLITE_CATALYTIC_VIAL.get()
            : ModItems.SPERRYLITE_CATALYTIC_VIAL_SPLASH.get();
        Item result = target == Target.SPLASH
            ? ModItems.SPERRYLITE_CATALYTIC_VIAL_SPLASH.get()
            : ModItems.SPERRYLITE_CATALYTIC_VIAL_LINGERING.get();

        for (int i = 0; i < 3; i++) {
            ItemStack stack = brewingStand.getItem(i);
            if (!stack.is(source) || !stack.has(DataComponents.POTION_CONTENTS)) continue;

            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            ItemStack converted = new ItemStack(result, stack.getCount());
            converted.set(DataComponents.POTION_CONTENTS, contents);
            brewingStand.setItem(i, converted);
        }

        brewingStand.getItem(3).shrink(1);
        brewingStand.setChanged();
    }
}

