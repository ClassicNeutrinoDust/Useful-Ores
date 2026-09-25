package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.MeteoriteStructure;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ModStructureTypes {

   public static final StructureType<MeteoriteStructure> METEORITE = Registry.register(
      BuiltInRegistries.STRUCTURE_TYPE,
      Identifier.fromNamespaceAndPath(ModRegisters.MODID, "meteorite"),
      () -> MeteoriteStructure.CODEC
   );

   public static final StructureType<AncientPedestalStructure> ANCIENT_PEDESTAL = Registry.register(
      BuiltInRegistries.STRUCTURE_TYPE,
      Identifier.fromNamespaceAndPath(ModRegisters.MODID, "ancient_pedestal_chamber"),
      () -> AncientPedestalStructure.CODEC
   );

   public static void init() {
   }
}

