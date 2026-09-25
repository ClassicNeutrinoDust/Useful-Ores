package com.neutrinodust.useful_ores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NorGateBlock extends LogicGateBlock {

   public NorGateBlock(Properties properties) {
      super(properties);
   }


   @Override
   protected boolean computeOutput(Level level, BlockPos pos, BlockState state) {
      boolean a = readInput(level, pos, inputA(state));
      boolean b = readInput(level, pos, inputB(state));
      return !(a || b);
   }
}

