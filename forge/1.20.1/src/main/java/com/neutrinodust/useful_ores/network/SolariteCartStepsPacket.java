package com.neutrinodust.useful_ores.network;

import com.neutrinodust.useful_ores.minecart.SolariteMinecartBehavior.MinecartStep;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record SolariteCartStepsPacket(int entityId, List<MinecartStep> steps, boolean reset) {
    private static final int MAX_STEPS = 64;

    public static void encode(SolariteCartStepsPacket packet, FriendlyByteBuf buffer) {
        if (packet.steps().size() > MAX_STEPS) {
            throw new IllegalArgumentException("Too many Solarite minecart steps: " + packet.steps().size());
        }
        buffer.writeVarInt(packet.entityId());
        buffer.writeBoolean(packet.reset());
        buffer.writeVarInt(packet.steps().size());
        for (MinecartStep step : packet.steps()) {
            if (!isFinite(step)) throw new IllegalArgumentException("Non-finite Solarite minecart step");
            step.write(buffer);
        }
    }

    public static SolariteCartStepsPacket decode(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        boolean reset = buffer.readBoolean();
        int count = buffer.readVarInt();
        if (count < 0 || count > MAX_STEPS) {
            throw new IllegalArgumentException("Invalid minecart step count: " + count);
        }
        List<MinecartStep> steps = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            MinecartStep step = MinecartStep.read(buffer);
            if (!isFinite(step)) throw new IllegalArgumentException("Non-finite Solarite minecart step");
            steps.add(step);
        }
        return new SolariteCartStepsPacket(id, List.copyOf(steps), reset);
    }

    private static boolean isFinite(MinecartStep step) {
        Vec3 position = step.position();
        Vec3 movement = step.movement();
        return Double.isFinite(position.x) && Double.isFinite(position.y) && Double.isFinite(position.z)
                && Double.isFinite(movement.x) && Double.isFinite(movement.y) && Double.isFinite(movement.z)
                && Float.isFinite(step.yRot()) && Float.isFinite(step.xRot())
                && Float.isFinite(step.weight()) && step.weight() >= 0.0F;
    }
}
