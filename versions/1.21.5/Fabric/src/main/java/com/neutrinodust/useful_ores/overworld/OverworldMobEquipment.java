package com.neutrinodust.useful_ores.overworld;

import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.init.ModRegisters;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gives Overworld hostile mobs a small, tier-scaled chance to spawn wearing armor made from
 * this mod's Overworld ores, similar in spirit to vanilla's own random zombie/skeleton armor
 * rolls, but restricted to ores at least as strong as diamond. Higher-tier ores are rarer.
 * Never overwrites armor a mob already spawned with (vanilla gold/iron/etc. is untouched).
 */
public final class OverworldMobEquipment {
    private OverworldMobEquipment() {}

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static final class MaterialTier {
        final List<ModRegisters.RegisteredItem<Item>> items;
        final float fullChance;
        final float twoChance;
        final float oneChance;
        MaterialTier(List<ModRegisters.RegisteredItem<Item>> items, float fullChance, float twoChance, float oneChance) {
            this.items = items; this.fullChance = fullChance; this.twoChance = twoChance; this.oneChance = oneChance;
        }
    }

    // Diamond-equivalent ores: rarer than nether painite, but the most common of this tier list.
    // Beyond-netherite ores (nyxium/arcanite): much rarer, like true "jackpot" spawns.
    private static final List<MaterialTier> TIERS = List.of(
        new MaterialTier(ModItems.CHROMITE_ITEMS, 0.0025F, 0.0050F, 0.0075F), // diamond tier, 1.5% total
        new MaterialTier(ModItems.ILMENITE_ITEMS, 0.0025F, 0.0050F, 0.0075F), // diamond tier, 1.5% total
        new MaterialTier(ModItems.NYXIUM_ITEMS,   0.0005F, 0.0010F, 0.0015F), // beyond netherite, 0.3% total
        new MaterialTier(ModItems.ARCANITE_ITEMS, 0.0005F, 0.0010F, 0.0015F)  // beyond netherite, 0.3% total
    );
    // Combined chance across all tiers/pieces: 3.6%. ~96.4% of overworld mobs get no bonus armor.

    public static void onEntityLoad(Entity entity, ServerLevel level) {
        if (!(entity instanceof Monster mob) || level.dimension() != Level.OVERWORLD || hasModArmor(mob)) return;

        float roll = mob.getRandom().nextFloat();
        float cumulative = 0F;
        for (MaterialTier tier : TIERS) {
            cumulative += tier.fullChance;
            if (roll < cumulative) { equip(mob, level, tier, 4); return; }
            cumulative += tier.twoChance;
            if (roll < cumulative) { equip(mob, level, tier, 2); return; }
            cumulative += tier.oneChance;
            if (roll < cumulative) { equip(mob, level, tier, 1); return; }
        }
    }

    private static void equip(Monster mob, ServerLevel level, MaterialTier tier, int pieceCount) {
        // Only fill slots the mob doesn't already have gear in (never replaces vanilla armor).
        List<EquipmentSlot> emptySlots = new ArrayList<>();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (mob.getItemBySlot(slot).isEmpty()) emptySlots.add(slot);
        }
        if (emptySlots.isEmpty()) return;

        for (int i = emptySlots.size() - 1; i > 0; i--) {
            int j = mob.getRandom().nextInt(i + 1);
            EquipmentSlot tmp = emptySlots.get(i);
            emptySlots.set(i, emptySlots.get(j));
            emptySlots.set(j, tmp);
        }

        int toEquip = Math.min(pieceCount, emptySlots.size());
        for (int i = 0; i < toEquip; i++) {
            EquipmentSlot slot = emptySlots.get(i);
            mob.setItemSlot(slot, enchanted(armorItemFor(tier, slot), level, mob));
            mob.setDropChance(slot, 0.05F);
        }
    }

    private static ItemStack armorItemFor(MaterialTier tier, EquipmentSlot slot) {
        int index = switch (slot) {
            case HEAD -> 8;
            case CHEST -> 9;
            case LEGS -> 10;
            default -> 11;
        };
        return tier.items.get(index).get().getDefaultInstance();
    }

    private static boolean hasModArmor(Monster m) {
        for (MaterialTier tier : TIERS) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack stack = m.getItemBySlot(slot);
                for (int idx = 8; idx <= 11; idx++) {
                    if (stack.is(tier.items.get(idx).get())) return true;
                }
            }
        }
        return false;
    }

    private static ItemStack enchanted(ItemStack s, Level l, Monster m) {
        try {
            var lookup = l.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            Holder.Reference<Enchantment> prot = lookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.PROTECTION);
            s.enchant(prot, 1 + m.getRandom().nextInt(4));
            if (m.getRandom().nextFloat() < 0.35F) {
                Holder.Reference<Enchantment> unb = lookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING);
                s.enchant(unb, 1 + m.getRandom().nextInt(3));
            }
        } catch (RuntimeException ignored) { }
        return s;
    }
}
