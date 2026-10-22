package com.neutrinodust.useful_ores.network;
import java.util.UUID; import net.minecraft.network.FriendlyByteBuf;
public record SpearChargeFramePacket(double velocityX20,double velocityY20,double velocityZ20,UUID target) {
 public static void encode(SpearChargeFramePacket p,FriendlyByteBuf b){b.writeDouble(p.velocityX20());b.writeDouble(p.velocityY20());b.writeDouble(p.velocityZ20());b.writeBoolean(p.target()!=null);if(p.target()!=null)b.writeUUID(p.target());}
 public static SpearChargeFramePacket decode(FriendlyByteBuf b){double x=b.readDouble(),y=b.readDouble(),z=b.readDouble();UUID target=b.readBoolean()?b.readUUID():null;return new SpearChargeFramePacket(x,y,z,target);}
}
