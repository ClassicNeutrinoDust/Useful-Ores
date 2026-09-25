package com.neutrinodust.useful_ores.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class ClientModelLayers {

    public static final ModelLayerLocation SOLARITE_MINECART = new ModelLayerLocation(
        Identifier.fromNamespaceAndPath("useful_ores", "solarite_battery_minecart"), "main");

    private ClientModelLayers() {}
}

