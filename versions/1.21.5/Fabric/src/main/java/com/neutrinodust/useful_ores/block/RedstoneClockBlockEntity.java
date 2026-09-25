package com.neutrinodust.useful_ores.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneClockBlockEntity extends BlockEntity {

   public static final int[] PERIODS_TICKS = { 2, 4, 10, 20, 40, 60, 100, 200 };

   private int periodIndex = 3;
   private int ticksUntilToggle = halfPeriod();

   public RedstoneClockBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.REDSTONE_CLOCK, pos, state);
   }

   private int halfPeriod() {
      return Math.max(1, PERIODS_TICKS[this.periodIndex] / 2);
   }

   public int getPeriodTicks() { return PERIODS_TICKS[this.periodIndex]; }

   public void cyclePeriod(Level level, BlockPos pos, BlockState state) {
      this.periodIndex = (this.periodIndex + 1) % PERIODS_TICKS.length;
      this.ticksUntilToggle = halfPeriod();
      this.setChanged();

      if (!level.isClientSide()) {
         level.setBlockAndUpdate(pos, state
            .setValue(RedstoneClockBlock.POWERED, false)
            .setValue(RedstoneClockBlock.PERIOD_INDEX, this.periodIndex));
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, RedstoneClockBlockEntity clock) {
      if (level.isClientSide()) return;

      if (--clock.ticksUntilToggle > 0) return;
      clock.ticksUntilToggle = clock.halfPeriod();

      boolean newPowered = !state.getValue(RedstoneClockBlock.POWERED);
      level.setBlockAndUpdate(pos, state.setValue(RedstoneClockBlock.POWERED, newPowered));
   }

   @Override
   protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
      super.loadAdditional(input, registries);
      this.periodIndex = clampIndex(input.getIntOr("period_index", 3));
      this.ticksUntilToggle = halfPeriod();
   }

   @Override
   protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
      super.saveAdditional(output, registries);
      output.putInt("period_index", this.periodIndex);
   }

   private static int clampIndex(int index) {
      return Math.max(0, Math.min(PERIODS_TICKS.length - 1, index));
   }
}

