package com.neutrinodust.useful_ores.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChromitePistonHeadBlock extends PistonHeadBlock {

   public ChromitePistonHeadBlock(Properties properties) {
      super(properties);
   }

   @SuppressWarnings("unchecked")

   @Override
   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos basePos = pos.relative(state.getValue(FACING).getOpposite());
      BlockState base = level.getBlockState(basePos);

      boolean fittingChromiteBase =
         (base.getBlock() instanceof ChromitePistonBlock || base.getBlock() instanceof ChromiteStickyPistonBlock)
            && base.getValue(PistonBaseBlock.EXTENDED)
            && base.getValue(DirectionalBlock.FACING) == state.getValue(FACING);

      if (fittingChromiteBase) {
         return true;
      }

      return base.is(Blocks.MOVING_PISTON) && base.getValue(DirectionalBlock.FACING) == state.getValue(FACING);
   }
}

