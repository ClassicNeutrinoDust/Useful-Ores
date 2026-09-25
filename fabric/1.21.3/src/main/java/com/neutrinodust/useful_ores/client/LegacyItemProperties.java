package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerComponents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;


public final class LegacyItemProperties {
    private LegacyItemProperties() {}

    public static void register() {
        registerBooleanProperty("useful_ores:farseeker", ModFarseekerComponents.FARSEEKER);
        registerBooleanProperty("useful_ores:painite_fury_tool", ModDataComponents.PAINITE_FURY_TOOL);
        registerPotionProperty();
        registerPotionColors();
    }

    private static void registerBooleanProperty(String name, net.minecraft.core.component.DataComponentType<Boolean> type) {
        ResourceLocation id = parse(name);
        String path = id.getPath();
        String[] items = path.equals("farseeker") ? new String[]{
            "attributed_subspace_axe","attributed_subspace_hoe","attributed_subspace_pickaxe","attributed_subspace_shovel",
            "subspace_axe","subspace_hoe","subspace_pickaxe","subspace_shovel"
        } : new String[]{
            "painite_axe","painite_boots","painite_chestplate","painite_helmet","painite_hoe","painite_leggings",
            "painite_pickaxe","painite_shovel","painite_spear","painite_sword"
        };
        for (String itemId : items) {
            Item item = getItem(itemId);
            ItemProperties.register(item, id, (stack, level, entity, seed) ->
                    Boolean.TRUE.equals(stack.get(type)) ? 1.0F : 0.0F);
        }
    }

    private static void registerPotionProperty() {
        ResourceLocation id = parse("useful_ores:potion_contents");
        Item item = getItem("sperrylite_catalytic_vial");
        ItemProperties.register(item, id, (stack, level, entity, seed) ->
                stack.get(DataComponents.POTION_CONTENTS) != null ? 1.0F : 0.0F);
    }

    



    private static void registerPotionColors() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex != 0) return -1;
            var contents = stack.get(DataComponents.POTION_CONTENTS);
            return contents != null ? contents.getColor() : -1;
        },
            getItem("sperrylite_catalytic_vial"),
            getItem("sperrylite_catalytic_vial_splash"),
            getItem("sperrylite_catalytic_vial_lingering")
        );
    }

    private static Item getItem(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.ITEM,
                        ResourceLocation.fromNamespaceAndPath("useful_ores", path)
                )
        ).map(net.minecraft.core.Holder.Reference::value).orElse(net.minecraft.world.item.Items.AIR);
    }

    private static ResourceLocation parse(String s) {
        int i=s.indexOf(':');
        return ResourceLocation.fromNamespaceAndPath(s.substring(0,i), s.substring(i+1));
    }
}
