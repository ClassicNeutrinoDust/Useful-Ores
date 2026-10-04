package com.neutrinodust.useful_ores.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class XorGateBlock extends LogicGateBlock {
   public static final MapCodec<XorGateBlock> CODEC = simpleCodec(XorGateBlock::new);

   public XorGateBlock(Properties properties) {
      super(properties);
   }

   @Override
   public MapCodec<XorGateBlock> codec() { return CODEC; }

   @Override
   protected boolean computeOutput(Level level, BlockPos pos, BlockState state) {
      boolean a = readInput(level, pos, inputA(state));
      boolean b = readInput(level, pos, inputB(state));
      return a ^ b;
   }
}

