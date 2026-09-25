package com.neutrinodust.useful_ores.farseeker;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFarseekerComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "useful_ores");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FARSEEKER =
            DATA_COMPONENTS.register("farseeker",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());

    public static boolean has(net.minecraft.world.item.ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(FARSEEKER.get()));
    }

    public static void init(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}

