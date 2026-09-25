package com.neutrinodust.useful_ores.phosgene;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class PhosgenePowderOverlayRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "useful_ores", "textures/misc/phosgene_powder_overlay.png");

    private static final int ALPHA = 105;
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float INFLATE = 0.0015f;
    private static final double MAX_DIST = 48.0;

    private static final Vector3f NORMAL = new Vector3f();

    private PhosgenePowderOverlayRenderer() {}

    public static void onCollectSubmits(LevelRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        var fields = ClientPhosgenePowderBlocks.snapshot();
        if (fields.isEmpty()) return;

        var collector = context.submitNodeCollector();
        var renderType = RenderTypes.entityTranslucentEmissive(TEXTURE);
        var camera = context.levelState().cameraRenderState.pos;
        double maxDistSq = MAX_DIST * MAX_DIST;

        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
        PoseStack poseStack = context.poseStack();

        for (var entry : fields.entrySet()) {
            BlockPos pos = BlockPos.of(entry.getKey());

            double cx = pos.getX() + 0.5;
            double cy = pos.getY() + 0.5;
            double cz = pos.getZ() + 0.5;
            double dx = cx - camera.x;
            double dy = cy - camera.y;
            double dz = cz - camera.z;
            if (dx * dx + dy * dy + dz * dz > maxDistSq) continue;

            int color = entry.getValue();
            int r = (color >>> 16) & 255;
            int g = (color >>> 8) & 255;
            int b = color & 255;

            boolean[] visible = new boolean[6];
            boolean anyVisible = false;
            for (Direction face : Direction.values()) {
                neighborPos.setWithOffset(pos, face);
                BlockState neighbor = level.getBlockState(neighborPos);
                visible[face.ordinal()] = neighbor.isAir() || !neighbor.canOcclude();
                anyVisible |= visible[face.ordinal()];
            }
            if (!anyVisible) continue;

            poseStack.pushPose();
            poseStack.translate(
                    pos.getX() - camera.x,
                    pos.getY() - camera.y,
                    pos.getZ() - camera.z);

            collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                for (Direction face : Direction.values()) {
                    if (visible[face.ordinal()]) {
                        drawFace(buffer, pose, face, r, g, b);
                    }
                }
            });

            poseStack.popPose();
        }
    }

    private static void drawFace(VertexConsumer buffer, PoseStack.Pose pose,
                                 Direction face, int r, int g, int b) {
        float min = -INFLATE;
        float max = 1.0f + INFLATE;

        float x0 = min;
        float x1 = max;
        float y0 = min;
        float y1 = max;
        float z0 = min;
        float z1 = max;

        switch (face) {
            case UP -> quad(buffer, pose,
                    x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0,
                    0, 1, 0, r, g, b);
            case DOWN -> quad(buffer, pose,
                    x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1,
                    0, -1, 0, r, g, b);
            case SOUTH -> quad(buffer, pose,
                    x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1,
                    0, 0, 1, r, g, b);
            case NORTH -> quad(buffer, pose,
                    x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0,
                    0, 0, -1, r, g, b);
            case EAST -> quad(buffer, pose,
                    x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0,
                    1, 0, 0, r, g, b);
            case WEST -> quad(buffer, pose,
                    x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1,
                    -1, 0, 0, r, g, b);
        }
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float x4, float y4, float z4,
                             float nx, float ny, float nz,
                             int r, int g, int b) {
        NORMAL.set(nx, ny, nz);
        int overlay = OverlayTexture.NO_OVERLAY;

        vertex(buffer, pose, x1, y1, z1, r, g, b, 0, 0, overlay);
        vertex(buffer, pose, x2, y2, z2, r, g, b, 1, 0, overlay);
        vertex(buffer, pose, x3, y3, z3, r, g, b, 1, 1, overlay);
        vertex(buffer, pose, x4, y4, z4, r, g, b, 0, 1, overlay);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z,
                               int r, int g, int b, float u, float v, int overlay) {
        buffer.addVertex(pose, x, y, z)
                .setColor(r, g, b, ALPHA)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(FULL_BRIGHT)
                .setNormal(NORMAL.x, NORMAL.y, NORMAL.z);
    }
}

