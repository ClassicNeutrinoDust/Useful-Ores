package com.neutrinodust.useful_ores.config;

import com.google.gson.JsonObject;
import com.neutrinodust.useful_ores.init.ModConfig;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public final class ModConditions {

    public static final ResourceLocation ITEM_ENABLED = new ResourceLocation("useful_ores", "item_enabled");

    private ModConditions() {}

    public static void init() {
        ResourceConditions.register(ITEM_ENABLED,
            json -> ModConfig.isItemEnabled(GsonHelper.getAsString(json, "item")));
    }
}
