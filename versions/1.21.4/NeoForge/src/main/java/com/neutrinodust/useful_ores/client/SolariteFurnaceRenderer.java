package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlock;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class SolariteFurnaceRenderer<T extends SolariteFurnaceBlockEntity> implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<T> {

    private static final int VIEW_DISTANCE = 256;

    private static final ResourceLocation TEXTURE_OFF = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_off.png");
    private static final ResourceLocation TEXTURE_ON = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_on.png");

    private static final ResourceLocation GLOW_MASK = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_glow_mask.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    private static final float TW = 64f, TH = 32f;

    public SolariteFurnaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public boolean shouldRenderOffScreen(T furnace) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return VIEW_DISTANCE;
    }

    @Override
    public void render(T furnace, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = furnace.getBlockState();
        Direction facing = state.hasProperty(SolariteFurnaceBlock.FACING)
                ? state.getValue(SolariteFurnaceBlock.FACING) : Direction.NORTH;
        boolean lit = state.hasProperty(SolariteFurnaceBlock.LIT) && state.getValue(SolariteFurnaceBlock.LIT);

        int light = computeLight(furnace, lit, packedLight);
        ResourceLocation texture = lit ? TEXTURE_ON : TEXTURE_OFF;
        RenderType renderType = RenderType.entityCutoutNoCull(texture);
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        double angleRad = Math.toRadians(facing.toYRot());
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);

        renderCube(buffer, poseStack, light, packedOverlay, cos, sin);

        if (lit) {

            float wave = (float) ((Math.sin((furnace.getLevel().getGameTime() + partialTick) * 0.10) + 1.0) * 0.5);
            int r = (int) (110 + wave * 65);
            int g = (int) (190 + wave * 55);
            int b = 255;
            int alpha = (int) (125 + wave * 95);
            VertexConsumer glow = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(GLOW_MASK));
            renderCube(glow, poseStack, FULL_BRIGHT, packedOverlay, cos, sin, r, g, b, alpha);
        }
    }

    private void renderCube(VertexConsumer buffer, PoseStack poseStack, int light, int overlay, float cos, float sin) {
        renderCube(buffer, poseStack, light, overlay, cos, sin, 255, 255, 255, 255);
    }

    private void renderCube(VertexConsumer buffer, PoseStack poseStack, int light, int overlay, float cos, float sin,
                            int red, int green, int blue, int alpha) {
        float y0 = 0f, y1 = 2f;

        quad(buffer, poseStack.last(), light, overlay, red, green, blue, alpha,
                rx(-1, -1, cos, sin), y0, rz(-1, -1, cos, sin),
                rx(-1, 1, cos, sin), y0, rz(-1, 1, cos, sin),
                rx(1, 1, cos, sin), y0, rz(1, 1, cos, sin),
                rx(1, -1, cos, sin), y0, rz(1, -1, cos, sin),
                0, -1, 0, 0 / TW, 0 / TH, 16 / TW, 16 / TH);

        quad(buffer, poseStack.last(), light, overlay, red, green, blue, alpha,
                rx(-1, 1, cos, sin), y1, rz(-1, 1, cos, sin),
                rx(-1, -1, cos, sin), y1, rz(-1, -1, cos, sin),
                rx(1, -1, cos, sin), y1, rz(1, -1, cos, sin),
                rx(1, 1, cos, sin), y1, rz(1, 1, cos, sin),
                0, 1, 0, 16 / TW, 0 / TH, 32 / TW, 16 / TH);

        quad(buffer, poseStack.last(), light, overlay, red, green, blue, alpha,
                rx(1, -1, cos, sin), y1, rz(1, -1, cos, sin),
                rx(-1, -1, cos, sin), y1, rz(-1, -1, cos, sin),
                rx(-1, -1, cos, sin), y0, rz(-1, -1, cos, sin),
                rx(1, -1, cos, sin), y0, rz(1, -1, cos, sin),
                rnx(0, -1, cos, sin), 0, rnz(0, -1, cos, sin),
                0 / TW, 16 / TH, 16 / TW, 32 / TH);

        quad(buffer, poseStack.last(), light, overlay, red, green, blue, alpha,
                rx(-1, 1, cos, sin), y1, rz(-1, 1, cos, sin),
                rx(1, 1, cos, sin), y1, rz(1, 1, cos, sin),
                rx(1, 1, cos, sin), y0, rz(1, 1, cos, sin),
                rx(-1, 1, cos, sin), y0, rz(-1, 1, cos, sin),
                rnx(0, 1, cos, sin), 0, rnz(0, 1, cos, sin),
                32 / TW, 0 / TH, 48 / TW, 16 / TH);

        quad(buffer, poseStack.last(), light, overlay, red, green, blue, alpha,
                rx(1, 1, cos, sin), y1, rz(1, 1, cos, sin),
                rx(1, -1, cos, sin), y1, rz(1, -1, cos, sin),
                rx(1, -1, cos, sin), y0, rz(1, -1, cos, sin),
                rx(1, 1, cos, sin), y0, rz(1, 1, cos, sin),
                rnx(1, 0, cos, sin), 0, rnz(1, 0, cos, sin),
                16 / TW, 16 / TH, 32 / TW, 32 / TH);

        quad(buffer, poseStack.last(), light, overlay, red, green, blue, alpha,
                rx(-1, -1, cos, sin), y1, rz(-1, -1, cos, sin),
                rx(-1, 1, cos, sin), y1, rz(-1, 1, cos, sin),
                rx(-1, 1, cos, sin), y0, rz(-1, 1, cos, sin),
                rx(-1, -1, cos, sin), y0, rz(-1, -1, cos, sin),
                rnx(-1, 0, cos, sin), 0, rnz(-1, 0, cos, sin),
                16 / TW, 16 / TH, 32 / TW, 32 / TH);
    }

    private static float rx(float lx, float lz, float cos, float sin) {
        return (lx * cos - lz * sin) + 1f;
    }

    private static float rz(float lx, float lz, float cos, float sin) {
        return (lx * sin + lz * cos) + 1f;
    }

    private static float rnx(float lx, float lz, float cos, float sin) {
        return lx * cos - lz * sin;
    }

    private static float rnz(float lx, float lz, float cos, float sin) {
        return lx * sin + lz * cos;
    }

    private void quad(VertexConsumer buffer, PoseStack.Pose pose, int light, int overlay, int red, int green, int blue, int alpha,
                       float x1, float y1, float z1, float x2, float y2, float z2,
                       float x3, float y3, float z3, float x4, float y4, float z4,
                       float nx, float ny, float nz,
                       float u0, float v0, float u1, float v1) {
        buffer.addVertex(pose, x1, y1, z1).setColor(red, green, blue, alpha).setUv(u0, v0)
                .setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, x2, y2, z2).setColor(red, green, blue, alpha).setUv(u1, v0)
                .setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, x3, y3, z3).setColor(red, green, blue, alpha).setUv(u1, v1)
                .setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, x4, y4, z4).setColor(red, green, blue, alpha).setUv(u0, v1)
                .setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
    }

    private int computeLight(SolariteFurnaceBlockEntity furnace, boolean lit, int fallback) {
        Level level = furnace.getLevel();
        if (level == null) return fallback;
        BlockPos origin = furnace.getBlockPos();
        int blockLight = 0, skyLight = 0;
        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                for (int dz = 0; dz <= 1; dz++) {
                    BlockPos sample = origin.offset(dx, dy, dz);
                    blockLight = Math.max(blockLight, level.getBrightness(LightLayer.BLOCK, sample));
                    skyLight = Math.max(skyLight, level.getBrightness(LightLayer.SKY, sample));
                }
            }
        }
        if (lit) blockLight = 15;
        return LightTexture.pack(blockLight, skyLight);
    }
}

