package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlock;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;

public class WirelessRedstoneRelayGlowRenderer implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<WirelessRedstoneRelayBlockEntity> {

    private static final ResourceLocation GEM_ON_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/block/wireless_redstone_relay_gem_on2.png");

    private static final int R = 210, G = 230, B = 255, A = 235;

    public WirelessRedstoneRelayGlowRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WirelessRedstoneRelayBlockEntity relay, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        BlockState state = relay.getBlockState();
        if (!state.hasProperty(WirelessRedstoneRelayBlock.POWERED) || !state.getValue(WirelessRedstoneRelayBlock.POWERED)) {
            return;
        }
        GlowCubeUtil.renderCoreGlow(bufferSource, poseStack.last(), GEM_ON_TEXTURE, R, G, B, A);
    }
}

