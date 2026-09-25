package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerComponents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;


@EventBusSubscriber(modid="useful_ores", bus=EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
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
            var contents = stack.get(DataComponents.POTION_CONTENTS);
            return contents != null ? contents.getColor() : -1;
        },
            getItem("sperrylite_catalytic_vial"),
            getItem("sperrylite_catalytic_vial_splash"),
            getItem("sperrylite_catalytic_vial_lingering")
        );
    }

    private static void register() {
        registerBooleanProperty(ResourceLocation.fromNamespaceAndPath("useful_ores","farseeker"), ModFarseekerComponents.FARSEEKER.get(), new String[]{
            "attributed_subspace_axe","attributed_subspace_hoe","attributed_subspace_pickaxe","attributed_subspace_shovel",
            "subspace_axe","subspace_hoe","subspace_pickaxe","subspace_shovel"
        });
        registerBooleanProperty(ResourceLocation.fromNamespaceAndPath("useful_ores","painite_fury_tool"), ModDataComponents.PAINITE_FURY_TOOL.get(), new String[]{
            "painite_axe","painite_boots","painite_chestplate","painite_helmet","painite_hoe","painite_leggings",
            "painite_pickaxe","painite_shovel","painite_spear","painite_sword"
        });
        ItemProperties.register(getItem("sperrylite_catalytic_vial"), ResourceLocation.fromNamespaceAndPath("useful_ores","potion_contents"),
                (stack, level, entity, seed) -> stack.get(DataComponents.POTION_CONTENTS) != null ? 1.0F : 0.0F);
        
    }

    private static <T> void registerBooleanProperty(ResourceLocation id, net.minecraft.core.component.DataComponentType<T> type, String[] itemIds) {
        for (String path : itemIds) {
            Item item = getItem(path);
            ItemProperties.register(item, id, (stack, level, entity, seed) -> Boolean.TRUE.equals(stack.get(type)) ? 1.0F : 0.0F);
        }
    }

    private static Item getItem(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
            net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("useful_ores", path))
        ).map(net.minecraft.core.Holder.Reference::value).orElse(net.minecraft.world.item.Items.AIR);
    }
}
