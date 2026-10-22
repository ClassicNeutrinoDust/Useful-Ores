package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public record SyncPhosgenePowderBlocksPacket(List<Entry> entries, Mode mode) implements ModPacket {

    public record Entry(long position, int color) {}

    public enum Mode {
        FULL, ADD, REMOVE;
        static Mode byId(int id) {
            return id >= 0 && id < values().length ? values()[id] : FULL;
        }
    }

    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "sync_phosgene_powder_blocks");

    public static SyncPhosgenePowderBlocksPacket read(FriendlyByteBuf buf) {
        Mode mode = Mode.byId(buf.readVarInt());
        int size = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) entries.add(new Entry(buf.readLong(), buf.readInt()));
        return new SyncPhosgenePowderBlocksPacket(entries, mode);
    }

    @Override public ResourceLocation id() { return ID; }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(mode.ordinal());
        buf.writeVarInt(entries.size());
        for (Entry e : entries) {
            buf.writeLong(e.position());
            buf.writeInt(e.color());
        }
    }
}
