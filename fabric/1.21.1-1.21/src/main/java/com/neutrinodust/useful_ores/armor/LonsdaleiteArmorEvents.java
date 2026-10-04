package com.neutrinodust.useful_ores.armor;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LonsdaleiteArmorEvents {

    public static final float FULL_SET_BLAST_REDUCTION = 0.70F;
    private static final int PIECE_COUNT = 4;

    public static float blastDamageReduction(LivingEntity entity) {
        int pieces = countLonsdaleitePieces(entity);
        return (pieces / (float) PIECE_COUNT) * FULL_SET_BLAST_REDUCTION;
    }

    public static float blastDamageMultiplier(LivingEntity entity) {
        return 1.0F - blastDamageReduction(entity);
    }

    private static int countLonsdaleitePieces(LivingEntity entity) {
        int count = 0;
        if (isLonsdaleitePiece(entity.getItemBySlot(EquipmentSlot.HEAD), 8)) count++;
        if (isLonsdaleitePiece(entity.getItemBySlot(EquipmentSlot.CHEST), 9)) count++;
        if (isLonsdaleitePiece(entity.getItemBySlot(EquipmentSlot.LEGS), 10)) count++;
        if (isLonsdaleitePiece(entity.getItemBySlot(EquipmentSlot.FEET), 11)) count++;
        return count;
    }

    private static boolean isLonsdaleitePiece(ItemStack stack, int lonsdaleiteItemsIndex) {
        Item expected = ModItems.LONSDALEITE_ITEMS.get(lonsdaleiteItemsIndex).get();
        return stack.is(expected);
    }
}

