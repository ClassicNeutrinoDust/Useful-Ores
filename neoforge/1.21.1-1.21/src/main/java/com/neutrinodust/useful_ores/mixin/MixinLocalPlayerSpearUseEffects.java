package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;












@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerSpearUseEffects {
    @Shadow
    protected abstract boolean hasEnoughFoodToStartSprinting();

    @Shadow
    protected abstract boolean vehicleCanSprint(Entity vehicle);

    @Unique
    private LocalPlayer usefulOres$self() {
        return (LocalPlayer) (Object) this;
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

    




    @Inject(method = "canStartSprinting()Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void usefulOres$canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = usefulOres$self();
        if (!usefulOres$mainHandSpear()) {
            return;
        }

        boolean result = !player.isSprinting()
                && player.input.getMoveVector().y > 0.0F
                && hasEnoughFoodToStartSprinting()
                && (!player.isSpectator()
                    || (player.getVehicle() != null && vehicleCanSprint(player.getVehicle())))
                && (!player.isSwimming() || !player.isUnderWater())
                && (!player.isMovingSlowly() || player.isUnderWater())
                && (!player.isFallFlying() || player.isUnderWater());

        cir.setReturnValue(result);
    }
}
