package com.neutrinodust.useful_ores.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;









@Mixin(CauldronFluidContent.class)
public final class MixinCauldronFluidContent {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true, remap = false)
    private static void usefulOres$skipExactDuplicate(
            Block block, Fluid fluid, int totalAmount, IntegerProperty levelProperty, CallbackInfo ci) {
        CauldronFluidContent existing = CauldronFluidContent.getForBlock(block);
        if (existing != null
                && existing.fluid == fluid
                && existing.totalAmount == totalAmount
                && Objects.equals(existing.levelProperty, levelProperty)) {
            ci.cancel();
        }
    }
}
