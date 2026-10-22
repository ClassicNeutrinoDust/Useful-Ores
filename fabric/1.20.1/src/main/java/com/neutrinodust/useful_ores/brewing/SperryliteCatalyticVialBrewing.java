package com.neutrinodust.useful_ores.brewing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import com.neutrinodust.useful_ores.util.ModPotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public final class SperryliteCatalyticVialBrewing {

    public static final int BREW_TICKS = 400;

    private static final Map<BrewingStandBlockEntity, Integer> BREW_PROGRESS = new WeakHashMap<>();

    private SperryliteCatalyticVialBrewing() {
    }

    public static int fuelCharges(BrewingStandBlockEntity brewingStand) {
        return SperryliteVialFuel.charges(brewingStand);
    }

    public static int remainingTicks(BrewingStandBlockEntity brewingStand) {
        Integer progress = BREW_PROGRESS.get(brewingStand);
        if (progress == null) return -1;
        return Math.max(0, BREW_TICKS - progress);
    }

    public static int progress(BrewingStandBlockEntity brewingStand) {
        return BREW_PROGRESS.getOrDefault(brewingStand, 0);
    }

    public static void loadProgress(BrewingStandBlockEntity brewingStand, int progress) {
        if (progress <= 0) {
            BREW_PROGRESS.remove(brewingStand);
            return;
        }
        BREW_PROGRESS.put(brewingStand, Math.min(progress, BREW_TICKS - 1));
    }

    public static boolean tick(Level level, BrewingStandBlockEntity brewingStand) {
        if (level.isClientSide()) {
            return matchesItems(brewingStand);
        }

        if (!matchesItems(brewingStand)) {
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
            brew(brewingStand);
        } else {
            BREW_PROGRESS.put(brewingStand, progress);
        }
        return true;
    }

    public static boolean matchesItems(Container brewingStand) {
        if (brewingStand.getContainerSize() < 5) return false;

        int vialSlot = -1;
        int potionSlotA = -1;
        int potionSlotB = -1;

        for (int i = 0; i < 3; i++) {
            ItemStack stack = brewingStand.getItem(i);
            if (stack.isEmpty()) return false;

            if (isEmptyVial(stack)) {
                if (vialSlot != -1) return false;
                vialSlot = i;
            } else if (isFilledPotion(stack)) {
                if (potionSlotA == -1) potionSlotA = i;
                else if (potionSlotB == -1) potionSlotB = i;
                else return false;
            } else {
                return false;
            }
        }

        if (vialSlot == -1 || potionSlotA == -1 || potionSlotB == -1) return false;

        ItemStack ingredient = brewingStand.getItem(3);
        if (ingredient.isEmpty() || !ingredient.is(ModItems.SPERRYLITE_ITEMS.get(2).get())) {
            return false;
        }

        return com.neutrinodust.useful_ores.util.ModPotionContents.get(brewingStand.getItem(potionSlotA)) != null
            && com.neutrinodust.useful_ores.util.ModPotionContents.get(brewingStand.getItem(potionSlotB)) != null;
    }

    private static void brew(Container brewingStand) {
        int vialSlot = -1;
        int potionSlotA = -1;
        int potionSlotB = -1;
        for (int i = 0; i < 3; i++) {
            ItemStack stack = brewingStand.getItem(i);
            if (isEmptyVial(stack)) vialSlot = i;
            else if (potionSlotA == -1) potionSlotA = i;
            else potionSlotB = i;
        }

        ModPotionContents contentsA = com.neutrinodust.useful_ores.util.ModPotionContents.get(brewingStand.getItem(potionSlotA));
        ModPotionContents contentsB = com.neutrinodust.useful_ores.util.ModPotionContents.get(brewingStand.getItem(potionSlotB));

        List<MobEffectInstance> merged = new ArrayList<>();
        collectEffects(contentsA, merged);
        collectEffects(contentsB, merged);
        if (merged.isEmpty()) return;

        ModPotionContents mixed = new ModPotionContents(Optional.empty(), Optional.empty(), merged);

        ItemStack filledVial = new ItemStack(ModItems.SPERRYLITE_CATALYTIC_VIAL.get());
        com.neutrinodust.useful_ores.util.ModPotionContents.set(filledVial, mixed);

        brewingStand.setItem(vialSlot, filledVial);
        brewingStand.setItem(potionSlotA, new ItemStack(Items.GLASS_BOTTLE));
        brewingStand.setItem(potionSlotB, new ItemStack(Items.GLASS_BOTTLE));
        brewingStand.getItem(3).shrink(1);
        brewingStand.setChanged();
    }

    private static boolean isEmptyVial(ItemStack stack) {
        return stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL.get())
            && com.neutrinodust.useful_ores.util.ModPotionContents.get(stack) == null;
    }

    private static boolean isFilledPotion(ItemStack stack) {
        return stack.is(Items.POTION) && com.neutrinodust.useful_ores.util.ModPotionContents.get(stack) != null;
    }

    private static void collectEffects(ModPotionContents contents, List<MobEffectInstance> out) {
        if (contents.potion().isPresent()) {
            out.addAll(contents.potion().get().getEffects());
        }
        out.addAll(contents.customEffects());
    }
}

