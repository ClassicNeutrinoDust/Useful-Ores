package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record SyncGlowingBlocksPacket(List<Long> positions, Mode mode) implements CustomPacketPayload {

    public enum Mode {
        FULL, ADD, REMOVE;

        static Mode byId(int id) {
            Mode[] values = values();
            return id >= 0 && id < values.length ? values[id] : FULL;
        }
    }

    public static final CustomPacketPayload.Type<SyncGlowingBlocksPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("useful_ores", "sync_glowing_blocks"));

    public static final StreamCodec<FriendlyByteBuf, SyncGlowingBlocksPacket> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public SyncGlowingBlocksPacket decode(FriendlyByteBuf buf) {
                    Mode mode = Mode.byId(buf.readVarInt());
                    int size = buf.readVarInt();
                    List<Long> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) list.add(buf.readLong());
                    return new SyncGlowingBlocksPacket(list, mode);
                }

                @Override
                public void encode(FriendlyByteBuf buf, SyncGlowingBlocksPacket pkt) {
                    buf.writeVarInt(pkt.mode().ordinal());
                    buf.writeVarInt(pkt.positions().size());
                    for (long l : pkt.positions()) buf.writeLong(l);
                }
            };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

