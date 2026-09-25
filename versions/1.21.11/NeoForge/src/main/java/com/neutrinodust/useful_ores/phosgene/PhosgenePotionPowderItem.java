package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public final class PhosgenePotionPowderItem extends Item {
    public PhosgenePotionPowderItem(Properties properties) {
        super(properties);
    }

    public static PotionContents getPotionContents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    @Override
    public Component getName(ItemStack stack) {
        PotionContents contents = getPotionContents(stack);
        if (contents.potion().isPresent()) {
            Identifier id = contents.potion().get().unwrapKey().map(key -> key.identifier()).orElse(null);
            if (id != null) {
                String path = id.getPath().replace('_', ' ');
                path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
                return Component.literal(path + " Phosgene Powder");
            }
        }
        return Component.translatable(this.getDescriptionId());
    }
}

