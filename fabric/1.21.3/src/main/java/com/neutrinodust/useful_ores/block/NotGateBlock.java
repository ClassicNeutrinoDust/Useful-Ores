package com.neutrinodust.useful_ores.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NotGateBlock extends LogicGateBlock {
   public static final MapCodec<NotGateBlock> CODEC = simpleCodec(NotGateBlock::new);

   public NotGateBlock(Properties properties) {
      super(properties);
   }

   @Override
   public MapCodec<NotGateBlock> codec() { return CODEC; }

   @Override
   protected boolean computeOutput(Level level, BlockPos pos, BlockState state) {
      boolean a = readInput(level, pos, inputA(state));
      return !a;
   }
}

