package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class MixinBaseRailBlockPlacement {

    @Inject(method = "setPlacedBy", at = @At("TAIL"))
    private void usefulOres$enforceControllerPriorityOnPlace(
            Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (!(state.getBlock() instanceof BaseRailBlock)) return;
        TitaniumControllerRailBlock.enforceControllerPriority(level, pos);
    }
}

