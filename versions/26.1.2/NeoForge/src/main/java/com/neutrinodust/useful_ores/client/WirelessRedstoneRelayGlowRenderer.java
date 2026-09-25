package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlock;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import com.neutrinodust.useful_ores.client.compat.BlockEntityRenderer121X;

public class WirelessRedstoneRelayGlowRenderer implements BlockEntityRenderer121X<WirelessRedstoneRelayBlockEntity> {

    private static final Identifier GEM_ON_TEXTURE = Identifier.fromNamespaceAndPath(
            "useful_ores", "textures/block/wireless_redstone_relay_gem_on2.png");

    private static final int R = 210, G = 230, B = 255, A = 235;

    public WirelessRedstoneRelayGlowRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WirelessRedstoneRelayBlockEntity relay, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = relay.getBlockState();
        if (!state.hasProperty(WirelessRedstoneRelayBlock.POWERED) || !state.getValue(WirelessRedstoneRelayBlock.POWERED)) {
            return;
        }
        GlowCubeUtil.renderCoreGlow(bufferSource, GEM_ON_TEXTURE, R, G, B, A);
    }
}

