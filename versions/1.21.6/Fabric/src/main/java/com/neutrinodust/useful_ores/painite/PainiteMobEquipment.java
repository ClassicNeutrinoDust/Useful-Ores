package com.neutrinodust.useful_ores.painite;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PainiteMobEquipment {
    private PainiteMobEquipment() {}

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    // Chance tiers (checked in order): full 4-piece set, then 2-piece, then 1-piece.
    private static final float FULL_SET_CHANCE = 0.02F;
    private static final float TWO_PIECE_CHANCE = 0.025F;
    private static final float ONE_PIECE_CHANCE = 0.035F;

    public static void onEntityLoad(Entity entity, ServerLevel level) {
        if (!(entity instanceof Monster mob) || level.dimension() != Level.NETHER || hasPainite(mob)) return;

        float roll = mob.getRandom().nextFloat();
        int pieceCount;
        if (roll < FULL_SET_CHANCE) {
            pieceCount = 4;
        } else if (roll < FULL_SET_CHANCE + TWO_PIECE_CHANCE) {
            pieceCount = 2;
        } else if (roll < FULL_SET_CHANCE + TWO_PIECE_CHANCE + ONE_PIECE_CHANCE) {
            pieceCount = 1;
        } else {
            return;
        }

        // Never overwrite armor a mob already spawned with (e.g. vanilla piglin gold armor).
        // Painite only fills slots that are still empty after natural equipment generation.
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
            mob.setItemSlot(slot, enchanted(painiteItemFor(slot), level, mob));
            mob.setDropChance(slot, 0.05F);
        }
    }

    private static ItemStack painiteItemFor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ModItems.PAINITE_ITEMS.get(8).get().getDefaultInstance();
            case CHEST -> ModItems.PAINITE_ITEMS.get(9).get().getDefaultInstance();
            case LEGS -> ModItems.PAINITE_ITEMS.get(10).get().getDefaultInstance();
            default -> ModItems.PAINITE_ITEMS.get(11).get().getDefaultInstance();
        };
    }

    private static boolean hasPainite(Monster m) {
        return PainitePower.isPainiteArmorItem(m.getItemBySlot(EquipmentSlot.HEAD))
            || PainitePower.isPainiteArmorItem(m.getItemBySlot(EquipmentSlot.CHEST))
            || PainitePower.isPainiteArmorItem(m.getItemBySlot(EquipmentSlot.LEGS))
            || PainitePower.isPainiteArmorItem(m.getItemBySlot(EquipmentSlot.FEET));
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
