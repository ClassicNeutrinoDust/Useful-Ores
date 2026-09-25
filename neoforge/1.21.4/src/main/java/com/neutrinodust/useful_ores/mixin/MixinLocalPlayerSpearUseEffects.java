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
    private static boolean usefulOres$usingBackportSpear(LocalPlayer player) {
        ItemStack useStack = player.getUseItem();
        return player.isUsingItem()
                && useStack != null
                && useStack.getItem() instanceof BackportSpearItem;
    }

    




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
