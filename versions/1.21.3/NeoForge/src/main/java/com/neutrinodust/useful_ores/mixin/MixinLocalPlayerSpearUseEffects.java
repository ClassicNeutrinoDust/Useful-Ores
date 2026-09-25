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

/**
 * NeoForge/Minecraft 1.21.3 spear-use movement compatibility.
 *
 * 1.21.3 does not contain LocalPlayer#shouldStopSprinting(). The previous
 * backport inherited a 1.21.4-only redirect into that method, which causes a
 * fatal Mixin validation failure at runtime.
 *
 * For 1.21.3 the movement slowdown is handled by the generic 0.2F input
 * multiplier in LocalPlayer#aiStep(). We therefore modify only that constant
 * while the player is actively using a Useful Ores spear.
 */
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

    /**
     * Cancel the normal 0.2x use-item movement slowdown only while charging a
     * spear. require=0 keeps the mixin non-fatal if a future mapping/build
     * changes constant emission, although 1.21.3 contains the relevant factor.
     */
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

    /**
     * Allow sprinting to start/continue while the spear is held in the main
     * hand.  This replaces the 1.21.4-specific shouldStopSprinting redirect
     * that does not exist in Minecraft 1.21.3.
     */
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
