package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.util.PotionStackData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public final class PhosgenePotionPowderItem extends Item {
    public PhosgenePotionPowderItem(Properties properties) {
        super(properties);
    }

    public static net.minecraft.world.item.alchemy.Potion getPotionContents(ItemStack stack) {
        return PotionStackData.potion(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        net.minecraft.world.item.alchemy.Potion contents = getPotionContents(stack);
        ResourceLocation id = BuiltInRegistries.POTION.getKey(contents);
        if (id != null && !contents.getEffects().isEmpty()) {
            if (id != null) {
                String path = id.getPath().replace('_', ' ');
                path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
                return Component.literal(path + " Phosgene Powder");
            }
        }
        return Component.translatable(this.getDescriptionId());
    }
}

