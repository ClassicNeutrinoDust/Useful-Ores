package com.neutrinodust.useful_ores.attribution;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "useful_ores");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PAINITE_FURY_TOOL =
            DATA_COMPONENTS.register("painite_fury_tool",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ATTRIBUTED_EFFECT =
            DATA_COMPONENTS.register("attributed_effect",
                    () -> DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                            .build());

    public static DataComponentType<Boolean> painiteFuryToolType() { return PAINITE_FURY_TOOL.get(); }

    public static void init(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}

