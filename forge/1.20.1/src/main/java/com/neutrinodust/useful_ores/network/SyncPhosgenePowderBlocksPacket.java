package com.neutrinodust.useful_ores.network;
import java.util.ArrayList; import java.util.List; import net.minecraft.network.FriendlyByteBuf;
public record SyncPhosgenePowderBlocksPacket(List<Entry> entries,Mode mode) {
 public record Entry(long position,int color) {}
 public enum Mode { FULL, ADD, REMOVE; static Mode byId(int i){return i>=0&&i<values().length?values()[i]:FULL;} }
 public static void encode(SyncPhosgenePowderBlocksPacket p,FriendlyByteBuf b){b.writeVarInt(p.mode().ordinal());b.writeVarInt(p.entries().size());for(Entry e:p.entries()){b.writeLong(e.position());b.writeInt(e.color());}}
 public static SyncPhosgenePowderBlocksPacket decode(FriendlyByteBuf b){Mode m=Mode.byId(b.readVarInt());int n=b.readVarInt();if(n<0||n>65536)throw new IllegalArgumentException("Invalid phosgene position count: "+n);List<Entry> out=new ArrayList<>(n);for(int i=0;i<n;i++)out.add(new Entry(b.readLong(),b.readInt()));return new SyncPhosgenePowderBlocksPacket(out,m);}
}
