package com.neutrinodust.useful_ores.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;

public class SperryliteCatalyticVialLingeringItem extends LingeringPotionItem {

    public SperryliteCatalyticVialLingeringItem(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.network.chat.Component getName(ItemStack stack) {
        return net.minecraft.network.chat.Component.translatable(this.getDescriptionId());
    }
}

