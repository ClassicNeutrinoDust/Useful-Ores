package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * V99.9: compile-correct movement/sprint backport of the known-working Accurate Spears
 * 1.21.5 implementation.
 *
 * Critical detail: Minecraft's movement input modifier itself contains the
 * generic isUsingItem() -> 0.2x input rule. We therefore replace that method at
 * its intermediary selector and reproduce the rest of vanilla's calculation,
 * skipping ONLY the generic 0.2x branch for a spear. This avoids both the silent
 * require=0 failure and the accidental loss of vanilla normalization/attribute
 * scaling caused by returning the raw input vector.
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerSpearUseEffects {
    @Shadow
    protected abstract boolean hasEnoughFoodToSprint();

    @Shadow
    protected abstract boolean vehicleCanSprint(Entity vehicle);

    @Unique
    private LocalPlayer usefulOres$self() {
        return (LocalPlayer) (Object) this;
    }

    @Unique
    private boolean usefulOres$mainHandSpear() {
        return usefulOres$self().getMainHandItem().is(SpearTags.SPEARS);
    }

    /**
     * Exact structural replacement of Accurate Spears' movement-factor injection.
     * The movement-input hook targets LocalPlayer's
     * applyMovementSpeedFactors/modifyInput method in 1.21.x.
     */
    @Inject(
        method = "modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private void usefulOres$modifyInput(Vec2 input, CallbackInfoReturnable<Vec2> cir) {
        if (input.lengthSquared() == 0.0F) {
            cir.setReturnValue(input);
            return;
        }

        LocalPlayer player = usefulOres$self();
        Vec2 vec = new Vec2(input.x * 0.98F, input.y * 0.98F);

        // This is the only part intentionally removed for spears: vanilla's
        // generic 0.2x held-item slowdown.
        if (player.isUsingItem()
                && !player.isSpectator()
                && !usefulOres$mainHandSpear()) {
            vec = new Vec2(vec.x * 0.2F, vec.y * 0.2F);
        }

        // Preserve vanilla's slow/sneak movement multiplier exactly.
        if (player.isMovingSlowly()) {
            float speed = (float) player.getAttributeValue(Attributes.MOVEMENT_SPEED);
            vec = new Vec2(vec.x * speed, vec.y * speed);
        }

        cir.setReturnValue(usefulOres$applyDirectionalMovementSpeedFactors(vec));
    }

    @Unique
    private static Vec2 usefulOres$applyDirectionalMovementSpeedFactors(Vec2 vec) {
        float length = vec.length();
        if (length <= 0.0F) {
            return vec;
        }

        Vec2 normalized = new Vec2(vec.x / length, vec.y / length);
        float directional = usefulOres$getDirectionalMovementSpeedMultiplier(normalized);
        float finalLength = Math.min(length * directional, 1.0F);
        return new Vec2(normalized.x * finalLength, normalized.y * finalLength);
    }

    @Unique
    private static float usefulOres$getDirectionalMovementSpeedMultiplier(Vec2 vec) {
        float x = Math.abs(vec.x);
        float y = Math.abs(vec.y);
        float ratio = y > x ? x / y : y / x;
        return (float) (Math.cos(Math.atan(ratio)) + Math.sin(Math.atan(ratio)));
    }

    /** Keep the reference mod's nuanced canStartSprinting behavior. */
    @Inject(method = "canStartSprinting()Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void usefulOres$canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = usefulOres$self();
        if (!usefulOres$mainHandSpear()) {
            return;
        }

        // Accurate Spears' reference conditions translated to stable 1.21.x names:
        // already-sprinting is not a start request; forward input, food, and the
        // normal water/swim/fall-flying restrictions still apply.
        boolean result = !player.isSprinting()
                && player.input.getMoveVector().y > 0.0F
                && hasEnoughFoodToSprint()
                && (!player.isSpectator()
                    || (player.getVehicle() != null && vehicleCanSprint(player.getVehicle())))
                && (!player.isSwimming() || !player.isUnderWater())
                && (!player.isMovingSlowly() || !player.isUnderWater())
                && (!player.isFallFlying() || !player.isUnderWater());

        cir.setReturnValue(result);
    }

    /**
     * Do not force canSprint() to true. The native sprint state should remain in
     * charge/use control; this avoids the V99.x regression where forcing the gate
     * interacted badly with LocalPlayer's normal sprint state machine.
     *
     * The important movement correction is the movement-input hook above: it removes only
     * the held-item 0.2x factor for a spear while preserving the remainder of
     * vanilla's movement calculation.
     */
}