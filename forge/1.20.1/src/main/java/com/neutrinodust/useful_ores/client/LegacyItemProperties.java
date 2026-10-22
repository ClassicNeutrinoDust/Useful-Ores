package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerComponents;
import net.minecraft.client.renderer.item.ItemProperties;
import com.neutrinodust.useful_ores.util.PotionStackData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

@Mod.EventBusSubscriber(modid="useful_ores", bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public final class LegacyItemProperties {
    private LegacyItemProperties() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(LegacyItemProperties::register);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) return -1;
            return PotionStackData.hasContents(stack) ? net.minecraft.world.item.alchemy.PotionUtils.getColor(stack) : -1;
        },
            getItem("sperrylite_catalytic_vial"),
            getItem("sperrylite_catalytic_vial_splash"),
            getItem("sperrylite_catalytic_vial_lingering"),
            getItem("phosgene_potion_powder")
        );
    }

    private static void register() {
        registerBooleanProperty(ResourceLocation.tryParse("useful_ores:farseeker"), true, new String[]{
            "attributed_subspace_axe","attributed_subspace_hoe","attributed_subspace_pickaxe","attributed_subspace_shovel",
            "subspace_axe","subspace_hoe","subspace_pickaxe","subspace_shovel"
        });
        registerBooleanProperty(ResourceLocation.tryParse("useful_ores:painite_fury_tool"), false, new String[]{
            "painite_axe","painite_boots","painite_chestplate","painite_helmet","painite_hoe","painite_leggings",
            "painite_pickaxe","painite_shovel","painite_spear","painite_sword"
        });
        ItemProperties.register(getItem("sperrylite_catalytic_vial"), ResourceLocation.fromNamespaceAndPath("useful_ores", "potion_contents"),
                (stack, level, entity, seed) -> PotionStackData.hasContents(stack) ? 1.0F : 0.0F);
        
    }

    private static void registerBooleanProperty(ResourceLocation id, boolean farseeker, String[] itemIds) {
        for (String path : itemIds) {
            Item item = getItem(path);
            ItemProperties.register(item, id, (stack, level, entity, seed) -> (farseeker ? ModFarseekerComponents.has(stack) : ModDataComponents.isFuryTool(stack)) ? 1.0F : 0.0F);
        }
    }

    private static Item getItem(String path) {
        Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("useful_ores", path));
        return item != null ? item : net.minecraft.world.item.Items.AIR;
    }
}
