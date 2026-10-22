package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ModPacket {
    ResourceLocation id();

    void write(FriendlyByteBuf buf);
}
