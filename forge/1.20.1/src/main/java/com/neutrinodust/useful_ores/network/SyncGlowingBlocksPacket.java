package com.neutrinodust.useful_ores.network;
import java.util.ArrayList; import java.util.List; import net.minecraft.network.FriendlyByteBuf;
public record SyncGlowingBlocksPacket(List<Long> positions,Mode mode) {
 public enum Mode { FULL, ADD, REMOVE; static Mode byId(int i){return i>=0&&i<values().length?values()[i]:FULL;} }
 public static void encode(SyncGlowingBlocksPacket p,FriendlyByteBuf b){b.writeVarInt(p.mode().ordinal());b.writeVarInt(p.positions().size());for(long l:p.positions())b.writeLong(l);}
 public static SyncGlowingBlocksPacket decode(FriendlyByteBuf b){Mode m=Mode.byId(b.readVarInt());int n=b.readVarInt();if(n<0||n>65536)throw new IllegalArgumentException("Invalid glowing position count: "+n);List<Long> out=new ArrayList<>(n);for(int i=0;i<n;i++)out.add(b.readLong());return new SyncGlowingBlocksPacket(out,m);}
}
