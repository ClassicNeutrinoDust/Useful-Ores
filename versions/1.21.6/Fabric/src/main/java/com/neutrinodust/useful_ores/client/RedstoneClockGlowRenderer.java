package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.RedstoneClockBlock;
import com.neutrinodust.useful_ores.block.RedstoneClockBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneClockGlowRenderer implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<RedstoneClockBlockEntity> {

    private static final ResourceLocation GEM_ON_GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/block/redstone_clock_gem_on_glow.png");

    private static final int R = 255, G = 245, B = 200, A = 235;

    public RedstoneClockGlowRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RedstoneClockBlockEntity clock, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        BlockState state = clock.getBlockState();
        if (!state.hasProperty(RedstoneClockBlock.POWERED) || !state.getValue(RedstoneClockBlock.POWERED)) {
            return;
        }
        GlowCubeUtil.renderCoreGlow(bufferSource, poseStack.last(), GEM_ON_GLOW_TEXTURE, R, G, B, A);
    }
}

