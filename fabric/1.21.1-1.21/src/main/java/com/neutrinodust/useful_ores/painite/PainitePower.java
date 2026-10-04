package com.neutrinodust.useful_ores.painite;

import com.neutrinodust.useful_ores.init.ModConfig;
import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PainitePower {
    private static final ResourceLocation MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("useful_ores", "painite_health_cost");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath("useful_ores", "painite_low_health_armor");
    private static final ResourceLocation ATTACK_ID = ResourceLocation.fromNamespaceAndPath("useful_ores", "painite_low_health_attack");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath("useful_ores", "painite_low_health_speed");
    private static final ResourceLocation SPEAR_SPEED_ID = ResourceLocation.fromNamespaceAndPath("useful_ores", "painite_low_health_spear_speed");

    private PainitePower() {}

    




    public static void update(LivingEntity entity) {
        int armorPieces = 0;
        double armor = 0.0D;
        boolean wearing = false;
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (isPainiteArmor(stack)) {
                armorPieces++;
                wearing = true;
                armor += defense(stack);
            }
        }

        
        set(entity.getAttribute(Attributes.MAX_HEALTH), MAX_HEALTH_ID,
                "useful_ores_painite_health_cost", -armorPieces,
                AttributeModifier.Operation.ADD_VALUE);

        
        double maxHealth = entity.getMaxHealth();
        if (entity.getHealth() > maxHealth) {
            entity.setHealth((float) maxHealth);
        }

        boolean active = isFuryActive(entity);
        set(entity.getAttribute(Attributes.ARMOR), ARMOR_ID,
                "useful_ores_painite_low_health_armor", active && wearing ? armor * 0.60D : 0.0D,
                AttributeModifier.Operation.ADD_VALUE);

        boolean tool = isPainiteCombatTool(entity.getMainHandItem());
        boolean spear = isPainiteSpear(entity.getMainHandItem());
        set(entity.getAttribute(Attributes.ATTACK_DAMAGE), ATTACK_ID,
                "useful_ores_painite_low_health_attack", active && (tool || spear) ? 0.60D : 0.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        set(entity.getAttribute(Attributes.BLOCK_BREAK_SPEED), SPEED_ID,
                "useful_ores_painite_low_health_speed", active && tool ? 0.60D : 0.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        
        set(entity.getAttribute(Attributes.ATTACK_SPEED), SPEAR_SPEED_ID,
                "useful_ores_painite_low_health_spear_speed", active && spear ? 1.50D : 0.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        if (entity instanceof Player player) {
            updateToolVisualState(player, active);
        }
    }

    




    private static void updateToolVisualState(Player player, boolean active) {
        var type = ModDataComponents.painiteFuryToolType();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            updateVisualMarker(player.getInventory().getItem(slot), type, active);
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            updateVisualMarker(player.getItemBySlot(slot), type, active);
        }
    }

    private static void updateVisualMarker(ItemStack stack, net.minecraft.core.component.DataComponentType<Boolean> type, boolean active) {
        if (!isPainiteVisualItem(stack)) return;
        boolean marked = stack.get(type) != null;
        if (active && !marked) stack.set(type, true);
        else if (!active && marked) stack.remove(type);
    }

    
    public static boolean isFuryActive(LivingEntity entity) {
        return entity.getHealth() > 0.0F
                && entity.getHealth() <= threshold(entity)
                && hasPainiteArmor(entity);
    }

    private static float threshold(LivingEntity e) {
        return e instanceof Player ? 10.0F : e.getMaxHealth() * 0.50F;
    }

    public static boolean hasPainiteArmor(LivingEntity entity) {
        return isPainiteArmor(entity.getItemBySlot(EquipmentSlot.HEAD))
            || isPainiteArmor(entity.getItemBySlot(EquipmentSlot.CHEST))
            || isPainiteArmor(entity.getItemBySlot(EquipmentSlot.LEGS))
            || isPainiteArmor(entity.getItemBySlot(EquipmentSlot.FEET));
    }

    private static void set(AttributeInstance a, ResourceLocation id, String name, double amount, AttributeModifier.Operation op) {
        if (a == null) return;
        AttributeModifier cur = a.getModifier(id);
        if (amount == 0.0D) {
            if (cur != null) a.removeModifier(id);
            return;
        }
        if (cur == null || cur.amount() != amount || cur.operation() != op) {
            if (cur != null) a.removeModifier(id);
            a.addTransientModifier(new AttributeModifier(id, amount, op));
        }
    }

    private static boolean isPainiteArmor(ItemStack s) {
        return s.is(ModItems.PAINITE_ITEMS.get(8).get()) || s.is(ModItems.PAINITE_ITEMS.get(9).get())
            || s.is(ModItems.PAINITE_ITEMS.get(10).get()) || s.is(ModItems.PAINITE_ITEMS.get(11).get());
    }

    public static boolean isPainiteArmorItem(ItemStack s) { return isPainiteArmor(s); }

    private static double defense(ItemStack s) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get("painite");
        if (c == null) return 0.0D;
        if (s.is(ModItems.PAINITE_ITEMS.get(8).get())) return c.helmetDefense.get();
        if (s.is(ModItems.PAINITE_ITEMS.get(9).get())) return c.chestplateDefense.get();
        if (s.is(ModItems.PAINITE_ITEMS.get(10).get())) return c.leggingsDefense.get();
        return c.bootsDefense.get();
    }

    
    public static boolean isPainiteCombatTool(ItemStack s) {
        return s.is(ModItems.PAINITE_ITEMS.get(3).get()) || s.is(ModItems.PAINITE_ITEMS.get(4).get())
            || s.is(ModItems.PAINITE_ITEMS.get(5).get()) || s.is(ModItems.PAINITE_ITEMS.get(6).get())
            || s.is(ModItems.PAINITE_ITEMS.get(7).get());
    }

    
    private static boolean isPainiteVisualItem(ItemStack s) {
        return isPainiteArmor(s) || isPainiteCombatTool(s) || isPainiteSpear(s);
    }

    private static boolean isPainiteSpear(ItemStack s) {
        return s.is(ModItems.PAINITE_ITEMS.get(12).get());
    }
}
