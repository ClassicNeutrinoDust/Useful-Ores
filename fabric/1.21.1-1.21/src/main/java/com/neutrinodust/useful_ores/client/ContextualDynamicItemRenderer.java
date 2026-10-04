package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


public final class ContextualDynamicItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ContextualDynamicItemRenderer INSTANCE = new ContextualDynamicItemRenderer();
    private MeteorStaffItemRenderer meteor;
    private NyxiumniteStaffItemRenderer nyx;
    private ContextualDynamicItemRenderer() {}

    @Override
    public void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int overlay) {
        if (isFlatContext(context)) {
            BakedModel flat = ContextualItemModelSelector.resolve(stack, context);
            if (flat != null) {
                Minecraft.getInstance().getItemRenderer().render(
                        stack, context, false, poseStack, bufferSource,
                        context == ItemDisplayContext.GUI ? LightTexture.FULL_BRIGHT : light,
                        overlay, flat);
                return;
            }
        }

        if (stack.is(com.neutrinodust.useful_ores.init.ModItems.METEOR_STAFF.get())) {
            if (meteor == null) meteor = new MeteorStaffItemRenderer();
            meteor.renderByItem(stack, context, poseStack, bufferSource, light, overlay);
            return;
        }
        if (stack.is(com.neutrinodust.useful_ores.init.ModItems.NYXIUMNITE_STAFF.get())) {
            if (nyx == null) nyx = new NyxiumniteStaffItemRenderer();
            nyx.renderByItem(stack, context, poseStack, bufferSource, light, overlay);
        }
    }

    private static boolean isFlatContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.GUI
                || context == ItemDisplayContext.GROUND
                || context == ItemDisplayContext.FIXED;
    }
}
