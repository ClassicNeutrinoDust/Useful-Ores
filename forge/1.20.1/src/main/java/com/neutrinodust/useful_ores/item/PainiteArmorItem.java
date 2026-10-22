package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class PainiteArmorItem extends ArmorItem {
    private static final ResourceLocation NORMAL_LAYER_1 =
            ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/models/armor/painite_layer_1.png");
    private static final ResourceLocation NORMAL_LAYER_2 =
            ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/models/armor/painite_layer_2.png");

    public PainiteArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, @Nullable String type) {
        boolean fury = entity instanceof LivingEntity living && PainitePower.isFuryActive(living);
        boolean innerModel = slot == EquipmentSlot.LEGS;
        if (!fury) return (innerModel ? NORMAL_LAYER_2 : NORMAL_LAYER_1).toString();
        String path = "textures/models/armor/painite_low_health_layer_" + (innerModel ? "2" : "1") + ".png";
        return "useful_ores:" + path;
    }
}
