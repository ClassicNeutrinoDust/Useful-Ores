package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.RedstoneClockBlock;
import com.neutrinodust.useful_ores.block.RedstoneClockBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.client.compat.SubmitNodeBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import com.neutrinodust.useful_ores.client.compat.BlockEntityRenderer121X;

public class RedstoneClockGlowRenderer implements BlockEntityRenderer121X<RedstoneClockBlockEntity> {

    private static final Identifier GEM_ON_GLOW_TEXTURE = Identifier.fromNamespaceAndPath(
            "useful_ores", "textures/block/redstone_clock_gem_on_glow.png");

    private static final int R = 255, G = 245, B = 200, A = 235;

    public RedstoneClockGlowRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RedstoneClockBlockEntity clock, float partialTick, PoseStack poseStack,
                        SubmitNodeBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = clock.getBlockState();
        if (!state.hasProperty(RedstoneClockBlock.POWERED) || !state.getValue(RedstoneClockBlock.POWERED)) {
            return;
        }
        GlowCubeUtil.renderCoreGlow(bufferSource, GEM_ON_GLOW_TEXTURE, R, G, B, A);
    }
}

