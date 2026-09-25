package com.neutrinodust.useful_ores.mixin;
import com.neutrinodust.useful_ores.client.spear.SpearArmedRenderStateAccess;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class MixinItemInHandLayerSpear {
    @Inject(method="method_4192",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/item/ItemStackRenderState;method_65604(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void usefulOres$applySpearPose(ArmedEntityRenderState state,ItemStackRenderState itemState,HumanoidArm arm,PoseStack pose,SubmitNodeCollector collector,int light,CallbackInfo ci){
        SpearArmedRenderStateAccess a=(SpearArmedRenderStateAccess)(Object)state;
        ItemStack stack=arm==HumanoidArm.RIGHT?a.usefulOres$getRightHandStack():a.usefulOres$getLeftHandStack();
        if(stack==null||stack.isEmpty()||!stack.is(SpearTags.SPEARS)) return;
        if(state instanceof HumanoidRenderState h){
            if(h.attackTime>0.0F && h.mainArm==arm) VanillaSpearAnimations.thirdPersonAttackItem(state,pose,h.attackTime,stack);
            float time=h.ticksUsingItem;
            if(time!=0.0F) VanillaSpearAnimations.thirdPersonUseItem(state,pose,time,h.attackTime,arm,stack);
        }
    }
}
