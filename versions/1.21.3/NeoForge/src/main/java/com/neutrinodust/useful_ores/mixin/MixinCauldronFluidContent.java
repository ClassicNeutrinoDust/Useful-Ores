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

/**
 * NeoForge 21.3.97 compatibility boundary.
 *
 * The supplied runtime crashes inside NeoForge's own CauldronFluidContent with an
 * exact duplicate registration of vanilla minecraft:cauldron. Useful Ores does not
 * register cauldrons. This adapter only makes an identical registration idempotent;
 * conflicting registrations are still rejected by NeoForge's original code.
 */
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
