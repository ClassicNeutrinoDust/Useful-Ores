package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.neutrinodust.useful_ores.minecart.SolariteMinecartBehavior.MinecartStep;
import java.util.ArrayList;
import java.util.List;

public record SolariteCartStepsPacket(int entityId, List<MinecartStep> steps, boolean reset) implements ModPacket {
   public static final ResourceLocation ID = new ResourceLocation("useful_ores", "solarite_cart_steps");

   public static SolariteCartStepsPacket read(FriendlyByteBuf buf) {
      int id = buf.readVarInt();
      boolean reset = buf.readBoolean();
      int n = buf.readVarInt();
      List<MinecartStep> steps = new ArrayList<>(n);
      for (int i = 0; i < n; i++) steps.add(MinecartStep.read(buf));
      return new SolariteCartStepsPacket(id, steps, reset);
   }

   @Override public ResourceLocation id() { return ID; }

   @Override
   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(entityId);
      buf.writeBoolean(reset);
      buf.writeVarInt(steps.size());
      for (MinecartStep s : steps) s.write(buf);
   }
}
