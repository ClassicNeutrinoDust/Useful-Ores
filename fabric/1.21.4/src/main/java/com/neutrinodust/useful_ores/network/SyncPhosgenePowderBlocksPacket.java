package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record SyncPhosgenePowderBlocksPacket(List<Entry> entries, Mode mode) implements CustomPacketPayload {
    public record Entry(long position, int color) {}

    public enum Mode {
        FULL, ADD, REMOVE;
        static Mode byId(int id) {
            return id >= 0 && id < values().length ? values()[id] : FULL;
        }
    }

    public static final Type<SyncPhosgenePowderBlocksPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("useful_ores", "sync_phosgene_powder_blocks"));

    public static final StreamCodec<FriendlyByteBuf, SyncPhosgenePowderBlocksPacket> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public SyncPhosgenePowderBlocksPacket decode(FriendlyByteBuf buf) {
                    Mode mode = Mode.byId(buf.readVarInt());
                    int size = buf.readVarInt();
                    List<Entry> entries = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) entries.add(new Entry(buf.readLong(), buf.readInt()));
                    return new SyncPhosgenePowderBlocksPacket(entries, mode);
                }

                @Override
                public void encode(FriendlyByteBuf buf, SyncPhosgenePowderBlocksPacket packet) {
                    buf.writeVarInt(packet.mode().ordinal());
                    buf.writeVarInt(packet.entries().size());
                    for (Entry e : packet.entries()) {
                        buf.writeLong(e.position());
                        buf.writeInt(e.color());
                    }
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

