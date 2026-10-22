package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public record SyncLockedChestsPacket(List<Long> positions, Mode mode) implements ModPacket {

    public enum Mode {
        FULL, ADD, REMOVE;
        static Mode byId(int id) {
            Mode[] values = values();
            return id >= 0 && id < values.length ? values[id] : FULL;
        }
    }

    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "sync_locked_chests");

    public static SyncLockedChestsPacket read(FriendlyByteBuf buf) {
        Mode mode = Mode.byId(buf.readVarInt());
        int size = buf.readVarInt();
        List<Long> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) list.add(buf.readLong());
        return new SyncLockedChestsPacket(list, mode);
    }

    @Override public ResourceLocation id() { return ID; }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(mode.ordinal());
        buf.writeVarInt(positions.size());
        for (long l : positions) buf.writeLong(l);
    }
}
