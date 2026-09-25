package com.neutrinodust.useful_ores.block.rail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class EnderiumPendingLaunchData extends SavedData {

    final Map<UUID, EnderiumRailLaunchEnforcer.PendingLaunch> pending = new ConcurrentHashMap<>();

    private record Entry(
            UUID cartId,
            BlockPos landingPos,
            Direction direction,
            double speed,
            double velocityY,
            int entryStopTicks,
            int exitStopTicks,
            boolean landed,
            int ticksRemaining
    ) {}

    private static final Codec<Entry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            uuidCodec().fieldOf("cart_id").forGetter(Entry::cartId),
            BlockPos.CODEC.fieldOf("landing_pos").forGetter(Entry::landingPos),
            Direction.CODEC.fieldOf("direction").forGetter(Entry::direction),
            Codec.DOUBLE.fieldOf("speed").forGetter(Entry::speed),
            Codec.DOUBLE.fieldOf("velocity_y").forGetter(Entry::velocityY),
            Codec.INT.fieldOf("entry_stop_ticks").forGetter(Entry::entryStopTicks),
            Codec.INT.fieldOf("exit_stop_ticks").forGetter(Entry::exitStopTicks),
            Codec.BOOL.fieldOf("landed").forGetter(Entry::landed),
            Codec.INT.fieldOf("ticks_remaining").forGetter(Entry::ticksRemaining)
    ).apply(instance, Entry::new));

    private static Codec<UUID> uuidCodec() {
        return Codec.STRING.xmap(UUID::fromString, UUID::toString);
    }

    private static final Codec<List<Entry>> LIST_CODEC = ENTRY_CODEC.listOf();

    public static final Codec<EnderiumPendingLaunchData> CODEC = LIST_CODEC.xmap(
            entries -> {
                EnderiumPendingLaunchData data = new EnderiumPendingLaunchData();
                for (Entry e : entries) {
                    EnderiumRailLaunchEnforcer.PendingLaunch launch = new EnderiumRailLaunchEnforcer.PendingLaunch(
                            e.landingPos(), e.direction(), e.speed(), e.velocityY(), e.ticksRemaining());
                    launch.entryStopTicks = e.entryStopTicks();
                    launch.exitStopTicks = e.exitStopTicks();
                    launch.landed = e.landed();
                    data.pending.put(e.cartId(), launch);
                }
                return data;
            },
            data -> {
                List<Entry> entries = new ArrayList<>();
                for (Map.Entry<UUID, EnderiumRailLaunchEnforcer.PendingLaunch> e : data.pending.entrySet()) {
                    EnderiumRailLaunchEnforcer.PendingLaunch l = e.getValue();
                    entries.add(new Entry(e.getKey(), l.landingPos, l.direction, l.speed, l.velocityY,
                            l.entryStopTicks, l.exitStopTicks, l.landed, l.ticksRemaining));
                }
                return entries;
            }
    );

    public static final SavedDataType<EnderiumPendingLaunchData> TYPE = new SavedDataType<>(
            "enderium_pending_launch",
            EnderiumPendingLaunchData::new,
            CODEC,
            null
    );

    public static EnderiumPendingLaunchData get(ServerLevel anyLevel) {
        ServerLevel overworld = anyLevel.getServer().overworld();
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(TYPE);
    }
}

