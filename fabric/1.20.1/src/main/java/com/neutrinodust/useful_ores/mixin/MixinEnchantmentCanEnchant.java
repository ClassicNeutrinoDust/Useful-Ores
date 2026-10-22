package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.item.SpearItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class MixinEnchantmentCanEnchant {
    @Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
    private void usefulOres$modToolRules(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item item = stack.getItem();
        if (!"useful_ores".equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) return;
        Enchantment self = (Enchantment) (Object) this;
        if (item instanceof SpearItem) {
            if (self == Enchantments.SMITE || self == Enchantments.BANE_OF_ARTHROPODS || self == Enchantments.SWEEPING_EDGE) {
                cir.setReturnValue(false);
            }
        } else if (item instanceof AxeItem) {
            if (self == Enchantments.KNOCKBACK || self == Enchantments.MOB_LOOTING) {
                cir.setReturnValue(true);
            }
        }
    }
}
