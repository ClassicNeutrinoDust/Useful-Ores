package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Native custom-renderer boundary for GeckoLib-backed staffs only. */
public final class ContextualStaticItemRenderer extends BlockEntityWithoutLevelRenderer {
    private MeteorStaffItemRenderer meteor;
    private NyxiumniteStaffItemRenderer nyx;

    public ContextualStaticItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (isFlatContext(context)) {
            BakedModel flat = ContextualItemModelSelector.resolve(stack, context);
            if (flat != null) {
                Minecraft.getInstance().getItemRenderer().render(
                        stack, context, false, poseStack, bufferSource,
                        context == ItemDisplayContext.GUI ? LightTexture.FULL_BRIGHT : packedLight,
                        packedOverlay, flat);
                return;
            }
        }

        if (stack.is(com.neutrinodust.useful_ores.init.ModItems.METEOR_STAFF.get())) {
            if (meteor == null) meteor = new MeteorStaffItemRenderer();
            meteor.renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
        } else if (stack.is(com.neutrinodust.useful_ores.init.ModItems.NYXIUMNITE_STAFF.get())) {
            if (nyx == null) nyx = new NyxiumniteStaffItemRenderer();
            nyx.renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private static boolean isFlatContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.GUI
                || context == ItemDisplayContext.GROUND
                || context == ItemDisplayContext.FIXED;
    }
}
