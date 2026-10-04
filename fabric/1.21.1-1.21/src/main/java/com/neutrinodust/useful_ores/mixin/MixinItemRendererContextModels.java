package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.client.ContextualItemModelSelector;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;









@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererContextModels {
    private static final String RENDER_METHOD =
            "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Z"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II"
                    + "Lnet/minecraft/client/resources/model/BakedModel;)V";

    private static final ThreadLocal<Context> USEFUL_ORES$CONTEXT = new ThreadLocal<>();

    @Inject(method = RENDER_METHOD, at = @At("HEAD"), require = 1, remap = true)
    private void usefulOres$capture(ItemStack stack, ItemDisplayContext displayContext,
                                    boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers,
                                    int light, int overlay, BakedModel model, CallbackInfo ci) {
        USEFUL_ORES$CONTEXT.set(new Context(stack, displayContext));
    }

    




    @ModifyVariable(
            method = RENDER_METHOD,
            at = @At(value = "LOAD", ordinal = 0),
            argsOnly = true,
            ordinal = 0,
            require = 1,
            remap = true)
    private BakedModel usefulOres$select(BakedModel original) {
        Context c = USEFUL_ORES$CONTEXT.get();
        return c == null ? original : ContextualItemModelSelector.select(original, c.stack(), c.context());
    }

    @Inject(method = RENDER_METHOD, at = @At("RETURN"), require = 1, remap = true)
    private void usefulOres$clear(ItemStack stack, ItemDisplayContext displayContext,
                                  boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers,
                                  int light, int overlay, BakedModel model, CallbackInfo ci) {
        USEFUL_ORES$CONTEXT.remove();
    }

    private record Context(ItemStack stack, ItemDisplayContext context) {}
}
