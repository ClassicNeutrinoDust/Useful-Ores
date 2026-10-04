package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.MeteoritePiece;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalPiece;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class ModStructurePieceTypes {

   public static final StructurePieceType METEORITE = Registry.register(
      BuiltInRegistries.STRUCTURE_PIECE,
      ResourceLocation.fromNamespaceAndPath(ModRegisters.MODID, "meteorite"),
      (StructurePieceType) MeteoritePiece::new
   );

   public static final StructurePieceType ANCIENT_PEDESTAL = Registry.register(
      BuiltInRegistries.STRUCTURE_PIECE,
      ResourceLocation.fromNamespaceAndPath(ModRegisters.MODID, "ancient_pedestal_chamber"),
      (StructurePieceType) AncientPedestalPiece::new
   );

   public static void init() {
   }
}

