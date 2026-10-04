package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;









public class PainiteArmorItem extends ArmorItem implements IItemExtension {
    private static final ResourceLocation NORMAL_LAYER_1 =
            ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/models/armor/painite_layer_1.png");
    private static final ResourceLocation NORMAL_LAYER_2 =
            ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/models/armor/painite_layer_2.png");

    public PainiteArmorItem(net.minecraft.core.Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                             ArmorMaterial.Layer layer, boolean innerModel) {
        boolean fury = entity instanceof LivingEntity living && PainitePower.isFuryActive(living);
        String layerPath = layer.texture(innerModel).getPath();

        
        if (!layerPath.contains("painite_fury_core")) {
            if (!fury) {
                return innerModel ? NORMAL_LAYER_2 : NORMAL_LAYER_1;
            }
            String path = "textures/models/armor/painite_low_health_layer_"
                    + (innerModel ? "2" : "1") + ".png";
            return ResourceLocation.fromNamespaceAndPath("useful_ores", path);
        }

        
        
        int frame = fury ? (int) ((System.currentTimeMillis() / 85L) % 12L) : 0;
        String path = "textures/models/armor/painite_fury_core_"
                + String.format("%02d", frame)
                + "_layer_" + (innerModel ? "2" : "1") + ".png";
        return ResourceLocation.fromNamespaceAndPath("useful_ores", path);
    }
}
