package com.neutrinodust.useful_ores.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChromiteStickyPistonBlock extends PistonBaseBlock {

   private static final int RETRACT_EVENT_ID = 1;

   public ChromiteStickyPistonBlock(Properties properties) {
      super(true, properties);
   }

   @SuppressWarnings("unchecked")

   @Override
   public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int data) {
      boolean result = super.triggerEvent(state, level, pos, id, data);

      if (!level.isClientSide() && id == RETRACT_EVENT_ID) {
         Direction direction = state.getValue(FACING);

         BlockPos headPos = pos.relative(direction);

         BlockPos stuckPos = pos.relative(direction, 2);

         if (level.getBlockState(headPos).isAir()) {
            BlockState stuckState = level.getBlockState(stuckPos);
            if (stuckState.is(Blocks.OBSIDIAN) || stuckState.is(Blocks.CRYING_OBSIDIAN)) {
               level.setBlockAndUpdate(headPos, stuckState);
               level.removeBlock(stuckPos, false);
            }
         }
      }

      return result;
   }
}

