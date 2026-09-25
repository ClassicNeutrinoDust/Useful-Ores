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

/** Makes the 1.21.8/1.21.7 vanilla swing clock use the spear's 1.21.8/1.21.7 swing duration. */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntitySpearSwingDuration {
    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void usefulOres$spearSwingDuration(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack main = self.getMainHandItem();
        ItemStack off = self.getOffhandItem();
        ItemStack spear = main.is(SpearTags.SPEARS) ? main : (off.is(SpearTags.SPEARS) ? off : ItemStack.EMPTY);
        if (!spear.isEmpty()) {
            // V95e: setReturnValue() without cancel() is a no-op in Mixin - this
            // override was never actually taking effect, so every spear jab was
            // still using vanilla's default swing duration instead of the
            // material-tuned SpearAnimationProfile duration.
            int duration = Math.max(1, Math.round(SpearAnimationProfile.forStack(spear).swingAnimationSeconds() * 20.0F));
            cir.setReturnValue(duration);
            cir.cancel();
            SpearSuperLog.swingDuration(self.level().getGameTime(), self.getName().getString(), duration);
        }
    }
}
