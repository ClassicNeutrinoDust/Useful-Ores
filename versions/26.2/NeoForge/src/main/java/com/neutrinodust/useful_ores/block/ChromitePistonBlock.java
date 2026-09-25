package com.neutrinodust.useful_ores.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

public class ChromitePistonBlock extends PistonBaseBlock {
   public static final MapCodec<ChromitePistonBlock> CODEC = simpleCodec(ChromitePistonBlock::new);

   public ChromitePistonBlock(Properties properties) {
      super(false, properties);
   }

   @SuppressWarnings("unchecked")
   @Override
   public MapCodec<PistonBaseBlock> codec() {
      return (MapCodec<PistonBaseBlock>) (MapCodec<?>) CODEC;
   }
}

