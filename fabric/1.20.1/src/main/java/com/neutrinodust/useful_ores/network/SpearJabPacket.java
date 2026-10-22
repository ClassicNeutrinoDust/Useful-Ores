package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SpearJabPacket() implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "spear_jab");

    public static SpearJabPacket read(FriendlyByteBuf buf) {
        return new SpearJabPacket();
    }

    @Override public ResourceLocation id() { return ID; }

    @Override public void write(FriendlyByteBuf buf) { }
}
