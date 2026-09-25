package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.item.BackportSpearItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge 1.21.4: exact backport of the 1.21.5 spear movement behavior.
 *
 * The supplied 1.21.4 client bytecode shows that LocalPlayer.aiStep() performs
 * the held-item slowdown as a conditional block immediately after ClientInput.tick():
 *
 *   if (isUsingItem() && !isFallFlying()) {
 *       input.forwardImpulse *= 0.2F;
 *       input.leftImpulse  *= 0.2F;
 *   }
 *
 * The reference 1.21.5 implementation instead replaces modifyInput() and omits
 * this generic 0.2x branch for spears. On 1.21.4 there is no modifyInput().
 * Therefore the exact 1.21.4 equivalent is to redirect the isUsingItem() call
 * belonging to that slowdown condition and return false only for an actively
 * charging BackportSpearItem. This removes the slowdown rather than trying to
 * compensate for it before/after the input update.
 *
 * A second redirect handles shouldStopSprinting(): vanilla 1.21.4 also checks
 * isUsingItem() there and would otherwise cancel sprint while charging.
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

    /** True only when the active use item is our actual NeoForge spear class. */
    @Unique
    private static boolean usefulOres$usingBackportSpear(LocalPlayer player) {
        ItemStack useStack = player.getUseItem();
        return player.isUsingItem()
                && useStack != null
                && useStack.getItem() instanceof BackportSpearItem;
    }

    /**
     * Exact interception of the 1.21.4 aiStep() slowdown predicate.
     * There is one isUsingItem() invocation in aiStep(), directly guarding the
     * two 0.2F impulse multiplications in the supplied client jar.
     */
    @Redirect(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        ),
        require = 1
    )
    private boolean usefulOres$skipSpearUseSlowdown(LocalPlayer player) {
        if (usefulOres$usingBackportSpear(player) && !player.isSpectator()) {
            return false;
        }
        return player.isUsingItem();
    }

    /**
     * Keep an already-running sprint alive while charging a spear. Other
     * should-stop conditions remain untouched because only the use-item check
     * is redirected.
     */
    @Redirect(
        method = "shouldStopSprinting",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        ),
        require = 1
    )
    private boolean usefulOres$ignoreSpearUseForSprintStop(LocalPlayer player) {
        if (usefulOres$usingBackportSpear(player) && !player.isSpectator()) {
            return false;
        }
        return player.isUsingItem();
    }

    @Unique
    private boolean usefulOres$mainHandSpear() {
        LocalPlayer player = usefulOres$self();
        return player.getMainHandItem().getItem() instanceof BackportSpearItem
                || player.getMainHandItem().is(SpearTags.SPEARS);
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
