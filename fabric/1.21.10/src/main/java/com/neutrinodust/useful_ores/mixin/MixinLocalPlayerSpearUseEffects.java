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












@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerSpearUseEffects {
    @Shadow
    protected abstract boolean method_46743(); 

    @Shadow
    protected abstract boolean method_48301(Entity vehicle); 

    @Unique
    private LocalPlayer usefulOres$self() {
        return (LocalPlayer) (Object) this;
    }

    @Unique
    private boolean usefulOres$mainHandSpear() {
        return usefulOres$self().getMainHandItem().is(SpearTags.SPEARS);
    }

    




    @Inject(
        method = "method_67270(Lnet/minecraft/class_241;)Lnet/minecraft/class_241;",
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

        
        
        if (player.isUsingItem()
                && !player.isSpectator()
                && !usefulOres$mainHandSpear()) {
            vec = new Vec2(vec.x * 0.2F, vec.y * 0.2F);
        }

        
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

    
    @Inject(method = "method_48300()Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void usefulOres$canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = usefulOres$self();
        if (!usefulOres$mainHandSpear()) {
            return;
        }

        
        
        
        boolean result = !player.isSprinting()
                && player.input.getMoveVector().y > 0.0F
                && method_46743()
                && (!player.isSpectator()
                    || (player.getVehicle() != null && method_48301(player.getVehicle())))
                && (!player.isSwimming() || !player.isUnderWater())
                && (!player.isMovingSlowly() || !player.isUnderWater())
                && (!player.isFallFlying() || !player.isUnderWater());

        cir.setReturnValue(result);
    }

    








}