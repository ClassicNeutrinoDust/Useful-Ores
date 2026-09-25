package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks;
import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import com.neutrinodust.useful_ores.particle.PhosgeneBubbleParticleOptions;
import com.neutrinodust.useful_ores.particle.PhosgeneEffectParticleOptions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PhosgenePowderClientEvents {
    private static int ticks;
    private static final double MAX_PARTICLE_DIST = 40.0;
    private static final double MAX_PARTICLE_DIST_SQ = MAX_PARTICLE_DIST * MAX_PARTICLE_DIST;
    private static final int MAX_SPAWN_PER_PASS = 96;

    private PhosgenePowderClientEvents() {}

    public static void register() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(ModParticleTypes.PHOSGENE_MIST, PhosgeneColoredParticle.MistProvider::new);
        registry.register(ModParticleTypes.PHOSGENE_BUBBLE, PhosgeneColoredParticle.BubbleProvider::new);
        ClientTickEvents.END_CLIENT_TICK.register(PhosgenePowderClientEvents::tick);
    }

    private static void tick(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;
        if (++ticks % 5 != 0) return;

        Vec3 camera = mc.gameRenderer.getMainCamera().position();
        RandomSource random = mc.level.getRandom();
        int spawned = 0;
        List<Map.Entry<Long, Integer>> fields =
                new ArrayList<>(ClientPhosgenePowderBlocks.snapshot().entrySet());
        if (fields.isEmpty()) return;

        int start = (ticks / 5 * MAX_SPAWN_PER_PASS) % fields.size();
        int checked = 0;

        while (checked < fields.size() && spawned < MAX_SPAWN_PER_PASS) {
            var entry = fields.get((start + checked) % fields.size());
            checked++;
            BlockPos pos = BlockPos.of(entry.getKey());
            double dx = pos.getX() + 0.5 - camera.x;
            double dy = pos.getY() + 1.0 - camera.y;
            double dz = pos.getZ() + 0.5 - camera.z;
            if (dx * dx + dy * dy + dz * dz > MAX_PARTICLE_DIST_SQ) continue;

            int color = entry.getValue();
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.01;
            double z = pos.getZ() + 0.5;
            boolean bubble = random.nextFloat() < 0.18F;
            ParticleOptions options = bubble
                    ? new PhosgeneBubbleParticleOptions(color, 0.26F + random.nextFloat() * 0.08F)
                    : new PhosgeneEffectParticleOptions(color, 0.36F + random.nextFloat() * 0.14F);
            mc.level.addParticle(options, x, y, z, 0.0, 0.0, 0.0);
            spawned++;
        }
    }
}

