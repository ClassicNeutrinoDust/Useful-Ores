package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.MeteoritePiece;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModStructurePieceTypes {
   public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
      DeferredRegister.create(Registries.STRUCTURE_PIECE, "useful_ores");

   public static final RegistryObject<StructurePieceType> METEORITE =
      STRUCTURE_PIECE_TYPES.register("meteorite", ModStructurePieceTypes::meteoriteType);

   public static final RegistryObject<StructurePieceType> ANCIENT_PEDESTAL =
      STRUCTURE_PIECE_TYPES.register("ancient_pedestal_chamber", ModStructurePieceTypes::ancientPedestalType);

   private static StructurePieceType meteoriteType() {
      return (context, tag) -> new MeteoritePiece(METEORITE.get(), tag);
   }

   private static StructurePieceType ancientPedestalType() {
      return (context, tag) -> new AncientPedestalPiece(ANCIENT_PEDESTAL.get(), tag);
   }

   public static void init(IEventBus bus) {
      STRUCTURE_PIECE_TYPES.register(bus);
   }
}

