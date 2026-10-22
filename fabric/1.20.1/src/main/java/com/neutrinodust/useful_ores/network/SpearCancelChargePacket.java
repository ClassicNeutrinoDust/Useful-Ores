package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SpearCancelChargePacket() implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "spear_cancel_charge");

    public static SpearCancelChargePacket read(FriendlyByteBuf buf) {
        return new SpearCancelChargePacket();
    }

    @Override public ResourceLocation id() { return ID; }

    @Override public void write(FriendlyByteBuf buf) { }
}
