package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public class MixinBrewingStandMenuIngredientsSlot {

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void usefulOres$allowSperryliteNugget(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ModItems.SPERRYLITE_ITEMS.get(2).get())) {
            cir.setReturnValue(true);
        }
    }
}

