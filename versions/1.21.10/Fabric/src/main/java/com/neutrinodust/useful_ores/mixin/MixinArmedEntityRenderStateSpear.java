package com.neutrinodust.useful_ores.mixin;
import com.neutrinodust.useful_ores.client.spear.SpearArmedRenderStateAccess;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Recreates the raw hand-stack state that vanilla 1.21.11 added to
 * ArmedEntityRenderState. Minecraft 1.21.10 only stores ItemStackRenderState.
 */
@Mixin(ArmedEntityRenderState.class)
public abstract class MixinArmedEntityRenderStateSpear implements SpearArmedRenderStateAccess {
    @Unique
    private ItemStack usefulOres$rightHandStack = ItemStack.EMPTY;

    @Unique
    private ItemStack usefulOres$leftHandStack = ItemStack.EMPTY;

    @Override
    public ItemStack usefulOres$getRightHandStack() {
        return usefulOres$rightHandStack;
    }

    @Override
    public ItemStack usefulOres$getLeftHandStack() {
        return usefulOres$leftHandStack;
    }

    @Override
    public void usefulOres$setRightHandStack(ItemStack stack) {
        usefulOres$rightHandStack = stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public void usefulOres$setLeftHandStack(ItemStack stack) {
        usefulOres$leftHandStack = stack == null ? ItemStack.EMPTY : stack;
    }

    @Inject(method = "method_65577", at = @At("TAIL"))
    private static void usefulOres$captureRawHandStacks(
            LivingEntity entity,
            ArmedEntityRenderState state,
            ItemModelResolver itemModelResolver,
            CallbackInfo ci
    ) {
        SpearArmedRenderStateAccess access = (SpearArmedRenderStateAccess) (Object) state;
        access.usefulOres$setRightHandStack(entity.getMainHandItem());
        access.usefulOres$setLeftHandStack(entity.getOffhandItem());
    }
}
