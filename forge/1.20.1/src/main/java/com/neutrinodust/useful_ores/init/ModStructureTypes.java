package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.MeteoriteStructure;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModStructureTypes {
   public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
      DeferredRegister.create(Registries.STRUCTURE_TYPE, "useful_ores");

   public static final RegistryObject<StructureType<MeteoriteStructure>> METEORITE =
      STRUCTURE_TYPES.register("meteorite", () -> (StructureType<MeteoriteStructure>) () -> MeteoriteStructure.CODEC);

   public static final RegistryObject<StructureType<AncientPedestalStructure>> ANCIENT_PEDESTAL =
      STRUCTURE_TYPES.register("ancient_pedestal_chamber",
         () -> (StructureType<AncientPedestalStructure>) () -> AncientPedestalStructure.CODEC);

   public static void init(IEventBus bus) {
      STRUCTURE_TYPES.register(bus);
   }
}

