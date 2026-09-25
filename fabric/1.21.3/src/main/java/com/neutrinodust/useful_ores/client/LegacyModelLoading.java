package com.neutrinodust.useful_ores.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;


public final class LegacyModelLoading {
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
            "arcanite_xp_jar_icon_7",
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

    private LegacyModelLoading() {}

    public static void register() {
        ModelLoadingPlugin.register(context -> {
            for (String path : EXTRA) {
                context.addModels(ResourceLocation.fromNamespaceAndPath(NS, "item/" + path));
            }
        });
    }
}
