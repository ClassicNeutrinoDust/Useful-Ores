package com.neutrinodust.useful_ores.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;





@EventBusSubscriber(modid = "useful_ores", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ContextualItemModelRegistration {
    private static final String NS = "useful_ores";
    private static final String[] EXTRA = {
            "meteor_staff_flat",
            "nyxiumnite_staff_flat",
            "scheelite_chisel",
            "scheelite_chisel_icon",
            "scheelite_chisel_hand",
            "arcanite_xp_jar_ground",
            "arcanite_xp_jar_icon_0",
            "arcanite_xp_jar_icon_1",
            "arcanite_xp_jar_icon_2",
            "arcanite_xp_jar_icon_3",
            "arcanite_xp_jar_icon_4",
            "arcanite_xp_jar_icon_5",
            "arcanite_xp_jar_icon_6",
            "arcanite_xp_jar_icon_7"
    };

    private ContextualItemModelRegistration() {}

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        for (String path : EXTRA) {
            event.register(ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath(NS, "item/" + path)));
        }
        for (String path : SPEAR_HAND_MODELS) {
            event.register(ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath(NS, "item/" + path)));
        }
    }

    private static final String[] SPEAR_HAND_MODELS = {
            "chromite_spear_in_hand",
            "enderium_spear_in_hand",
            "nyxium_spear_in_hand",
            "phosgene_spear_in_hand",
            "arcanite_spear_in_hand",
            "fulgurite_spear_in_hand",
            "osmium_spear_in_hand",
            "solarite_spear_in_hand",
            "sperrylite_spear_in_hand",
            "argentite_spear_in_hand",
            "zephyrite_spear_in_hand",
            "ilmenite_spear_in_hand",
            "scheelite_spear_in_hand",
            "voidshard_spear_in_hand",
            "lonsdaleite_spear_in_hand",
            "painite_spear_in_hand",
            "painite_spear_in_hand_fury",
            "subspace_spear_in_hand"
    };
}
