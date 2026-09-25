package com.neutrinodust.useful_ores.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;

public class SperryliteCatalyticVialSplashItem extends SplashPotionItem {

    public SperryliteCatalyticVialSplashItem(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.network.chat.Component getName(ItemStack stack) {

        return net.minecraft.network.chat.Component.translatable(this.getDescriptionId());
    }
}

