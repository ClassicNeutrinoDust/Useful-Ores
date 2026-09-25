package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.block.SuperBeaconBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import java.util.List;









public final class SuperBeaconRenderer implements BlockEntityRenderer<SuperBeaconBlockEntity> {
    public SuperBeaconRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SuperBeaconBlockEntity beacon, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (beacon.getLevel() == null) {
            return;
        }

        List<BeaconBlockEntity.BeaconBeamSection> sections = beacon.getBeamSections();
        if (sections.isEmpty()) {
            return;
        }

        int yOffset = 0;
        long gameTime = beacon.getLevel().getGameTime();
        for (BeaconBlockEntity.BeaconBeamSection section : sections) {
            int height = section.getHeight();
            if (height <= 0) {
                continue;
            }

            int endY = Math.min(yOffset + height, 2048);
            if (yOffset < endY) {
                BeaconRenderer.renderBeaconBeam(
                        poseStack,
                        buffer,
                        BeaconRenderer.BEAM_LOCATION,
                        partialTick,
                        1.0F,
                        gameTime,
                        yOffset,
                        endY,
                        section.getColor(),
                        0.2F,
                        0.25F);
            }

            yOffset = endY;
            if (yOffset >= 2048) {
                break;
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(SuperBeaconBlockEntity beacon) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
