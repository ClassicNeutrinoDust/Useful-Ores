package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerSpearUseEffects {
    @Unique
    private LocalPlayer usefulOres$self() {
        return (LocalPlayer) (Object) this;
    }

    @Unique
    private boolean usefulOres$hasEnoughFood() {
        LocalPlayer player = usefulOres$self();
        return player.getAbilities().mayfly || player.getFoodData().getFoodLevel() > 6;
    }

    @Unique
    private boolean usefulOres$mainHandSpear() {
        LocalPlayer player = usefulOres$self();
        return player.getMainHandItem().is(SpearTags.SPEARS);
    }

    @ModifyConstant(
        method = "aiStep",
        constant = @Constant(floatValue = 0.2F),
        require = 0
    )
    private float usefulOres$removeSpearUseSlowdown(float original) {
        LocalPlayer player = usefulOres$self();
        if (player.isUsingItem() && usefulOres$mainHandSpear() && !player.isSpectator()) {
            return 1.0F;
        }
        return original;
    }

    @Inject(method = "canStartSprinting()Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void usefulOres$canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = usefulOres$self();
        if (!usefulOres$mainHandSpear()) {
            return;
        }

        boolean result = !player.isSprinting()
                && player.input.forwardImpulse > 0.0F
                && usefulOres$hasEnoughFood()
                && (!player.isSpectator() || player.getVehicle() != null)
                && (!player.isSwimming() || !player.isUnderWater())
                && (!player.isCrouching() || player.isUnderWater())
                && (!player.isFallFlying() || player.isUnderWater());

        cir.setReturnValue(result);
    }
    @Inject(method = "aiStep", at = @At("TAIL"), require = 1)
    private void usefulOres$legacySpearSprint(CallbackInfo ci) {
        LocalPlayer player = usefulOres$self();
        if (!usefulOres$mainHandSpear() || !player.isUsingItem() || player.isSpectator()) return;
        if (player.input.forwardImpulse <= 0.0F || !usefulOres$hasEnoughFood()) return;
        if (player.isSwimming() && !player.isUnderWater()) return;
        if (player.isCrouching() && !player.isUnderWater()) return;
        if (player.isFallFlying() && !player.isUnderWater()) return;
        player.setSprinting(true);
    }

}
