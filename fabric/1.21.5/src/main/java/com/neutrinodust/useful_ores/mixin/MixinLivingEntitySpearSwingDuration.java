package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.client.spear.SpearAnimationProfile;
import com.neutrinodust.useful_ores.client.spear.SpearSuperLog;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class MixinLivingEntitySpearSwingDuration {
    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void usefulOres$spearSwingDuration(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack main = self.getMainHandItem();
        ItemStack off = self.getOffhandItem();
        ItemStack spear = main.is(SpearTags.SPEARS) ? main : (off.is(SpearTags.SPEARS) ? off : ItemStack.EMPTY);
        if (!spear.isEmpty()) {
            
            
            
            
            int duration = Math.max(1, Math.round(SpearAnimationProfile.forStack(spear).swingAnimationSeconds() * 20.0F));
            cir.setReturnValue(duration);
            cir.cancel();
            SpearSuperLog.swingDuration(self.level().getGameTime(), self.getName().getString(), duration);
        }
    }
}
