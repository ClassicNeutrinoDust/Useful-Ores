package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks;
import com.neutrinodust.useful_ores.init.ModRegisters;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class MixinBlockEntityType {

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void usefulOres$acceptColoredCampfires(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == BlockEntityTypes.CAMPFIRE) {
            for (ModRegisters.RegisteredBlock<Block> block : ModColoredCampfireBlocks.CAMPFIRE_BLOCKS.values()) {
                if (state.is(block.get())) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}

