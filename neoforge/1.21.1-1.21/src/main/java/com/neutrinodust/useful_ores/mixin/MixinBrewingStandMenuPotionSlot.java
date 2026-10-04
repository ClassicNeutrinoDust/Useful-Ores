package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class MixinBrewingStandMenuPotionSlot {

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void usefulOres$allowVial(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL.get())
                || stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL_SPLASH.get())
                || stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL_LINGERING.get())) {
            cir.setReturnValue(true);
        }
    }
}

