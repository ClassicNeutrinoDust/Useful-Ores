package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class MixinLevelNeighborNotify {

    @Inject(method = "updateNeighborsAt", at = @At("TAIL"))
    private void usefulOres$enforceControllerPriorityOnNeighborNotify(
            BlockPos pos, Block blockType, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (level.isClientSide()) return;

        usefulOres$checkAndEnforce(level, pos);
        for (Direction direction : Direction.values()) {
            usefulOres$checkAndEnforce(level, pos.relative(direction));
        }
    }

    private static void usefulOres$checkAndEnforce(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof BaseRailBlock) {
            TitaniumControllerRailBlock.enforceControllerPriority(level, pos);
        }
    }
}

