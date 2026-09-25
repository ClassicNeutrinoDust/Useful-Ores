package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.MeteoritePiece;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructurePieceTypes {
   public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
      DeferredRegister.create(Registries.STRUCTURE_PIECE, "useful_ores");

   public static final DeferredHolder<StructurePieceType, StructurePieceType> METEORITE =
      STRUCTURE_PIECE_TYPES.register("meteorite", () -> MeteoritePiece::new);

   public static final DeferredHolder<StructurePieceType, StructurePieceType> ANCIENT_PEDESTAL =
      STRUCTURE_PIECE_TYPES.register("ancient_pedestal_chamber", () -> AncientPedestalPiece::new);

   public static void init(IEventBus bus) {
      STRUCTURE_PIECE_TYPES.register(bus);
   }
}

