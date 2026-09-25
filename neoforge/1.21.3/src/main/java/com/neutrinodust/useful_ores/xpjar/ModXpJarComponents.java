package com.neutrinodust.useful_ores.xpjar;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModXpJarComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "useful_ores");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> JAR_XP =
            DATA_COMPONENTS.register("jar_xp",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .build());

    public static void init(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}

