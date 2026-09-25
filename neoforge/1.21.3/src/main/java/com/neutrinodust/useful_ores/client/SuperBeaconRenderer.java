package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.block.SuperBeaconBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.core.BlockPos;

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
        int maxBeamHeight = Math.max(0, beacon.getLevel().getMaxY() - beacon.getBlockPos().getY());
        long gameTime = beacon.getLevel().getGameTime();
        for (BeaconBlockEntity.BeaconBeamSection section : sections) {
            int height = section.getHeight();
            if (height <= 0) {
                continue;
            }

            int endY = Math.min(yOffset + height, maxBeamHeight);
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
            if (yOffset >= maxBeamHeight) {
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

    





    public net.minecraft.world.phys.AABB getRenderBoundingBox(SuperBeaconBlockEntity beacon) {
        BlockPos pos = beacon.getBlockPos();
        double maxY = beacon.getLevel() != null
                ? beacon.getLevel().getMaxY()
                : pos.getY() + 1024.0D;
        return new net.minecraft.world.phys.AABB(
                pos.getX() - 1.0D, pos.getY(), pos.getZ() - 1.0D,
                pos.getX() + 2.0D, maxY, pos.getZ() + 2.0D);
    }
}
