package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.neutrinodust.useful_ores.util.ModPotionContents;

public final class PhosgenePotionPowderItem extends Item {
    public PhosgenePotionPowderItem(Properties properties) {
        super(properties);
    }

    public static ModPotionContents getPotionContents(ItemStack stack) {
        return com.neutrinodust.useful_ores.util.ModPotionContents.getOrEmpty(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        ModPotionContents contents = getPotionContents(stack);
        if (contents.potion().isPresent()) {
            ResourceLocation id = contents.potionId().orElse(null);
            if (id != null) {
                String path = id.getPath().replace('_', ' ');
                path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
                return Component.literal(path + " Phosgene Powder");
            }
        }
        return Component.translatable(this.getDescriptionId());
    }
}

