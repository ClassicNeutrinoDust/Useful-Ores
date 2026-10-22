package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public record SpearChargeFramePacket(
        double velocityX20,
        double velocityY20,
        double velocityZ20,
        UUID target) implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "spear_charge_frame");

    public static SpearChargeFramePacket read(FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        UUID target = buf.readBoolean() ? buf.readUUID() : null;
        return new SpearChargeFramePacket(x, y, z, target);
    }

    @Override public ResourceLocation id() { return ID; }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(velocityX20);
        buf.writeDouble(velocityY20);
        buf.writeDouble(velocityZ20);
        buf.writeBoolean(target != null);
        if (target != null) buf.writeUUID(target);
    }
}
