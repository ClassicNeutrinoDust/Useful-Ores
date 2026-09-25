package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;










public record SpearChargeFramePacket(
        double velocityX20,
        double velocityY20,
        double velocityZ20,
        UUID target) implements CustomPacketPayload {

    public static final Type<SpearChargeFramePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("useful_ores", "spear_charge_frame"));

    public static final StreamCodec<FriendlyByteBuf, SpearChargeFramePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeDouble(packet.velocityX20());
                        buf.writeDouble(packet.velocityY20());
                        buf.writeDouble(packet.velocityZ20());
                        buf.writeBoolean(packet.target() != null);
                        if (packet.target() != null) {
                            buf.writeUUID(packet.target());
                        }
                    },
                    buf -> {
                        double x = buf.readDouble();
                        double y = buf.readDouble();
                        double z = buf.readDouble();
                        UUID target = buf.readBoolean() ? buf.readUUID() : null;
                        return new SpearChargeFramePacket(x, y, z, target);
                    });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
