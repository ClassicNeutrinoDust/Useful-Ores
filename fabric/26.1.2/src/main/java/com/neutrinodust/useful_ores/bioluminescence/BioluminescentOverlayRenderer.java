package com.neutrinodust.useful_ores.bioluminescence;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;

public class BioluminescentOverlayRenderer {

    private static final Identifier OVERLAY_TEXTURE = Identifier.fromNamespaceAndPath(
            "useful_ores", "textures/misc/bioluminescent_overlay.png");

    private static final float  INFLATE           = 0.002f;
    private static final int    ALPHA             = 235;
    private static final double MAX_DIST          = 48.0;
    private static final int    FULL_BRIGHT       = 0xF000F0;
    private static final float  ROTATE_SPEED      = 0.6f;

    private static final float[]               UV   = new float[8];

    private static final Vector3f              PPOS = new Vector3f();

    private static final Vector3f              PNRM = new Vector3f();

    private static final BlockPos.MutableBlockPos MPOS = new BlockPos.MutableBlockPos();

    public static void onRenderAfterTranslucent(LevelRenderContext context) {
        long[] glowing = ClientGlowingBlocks.snapshotArray();
        if (glowing.length == 0) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        Vec3 camPos = context.levelState().cameraRenderState.pos;

        float angle = (System.nanoTime() / 1_000_000_000.0f) * ROTATE_SPEED;
        float cosA  = (float) Math.cos(angle);
        float sinA  = (float) Math.sin(angle);

        double rd       = Math.min(512.0, Math.max(MAX_DIST,
                                   mc.options.renderDistance().get() * 16.0 + 16.0));
        double rdSq     = rd * rd;
        double halfRdSq = (rd * 0.5) * (rd * 0.5);

        PoseStack poseStack = context.poseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        RenderType renderType = RenderTypes.entityTranslucentEmissive(OVERLAY_TEXTURE);
        VertexConsumer buffer = buffers.getBuffer(renderType);

        for (long packed : glowing) {
            BlockPos pos = BlockPos.of(packed);

            double dx = pos.getX() + 0.5 - camPos.x;
            double dy = pos.getY() + 0.5 - camPos.y;
            double dz = pos.getZ() + 0.5 - camPos.z;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > rdSq) continue;

            int alpha = ALPHA;
            if (distSq > halfRdSq) {
                float t = (float) ((distSq - halfRdSq) / (rdSq - halfRdSq));
                alpha = Math.round(ALPHA * (1.0f - t));
                if (alpha <= 0) continue;
            }

            float ox = (float) (pos.getX() - camPos.x);
            float oy = (float) (pos.getY() - camPos.y);
            float oz = (float) (pos.getZ() - camPos.z);

            for (Direction dir : Direction.values()) {

                MPOS.setWithOffset(pos, dir);
                BlockState neighbour = level.getBlockState(MPOS);
                if (!neighbour.isAir() && neighbour.canOcclude()) continue;

                drawGlowFace(buffer, poseStack, ox, oy, oz, dir, cosA, sinA, alpha);
            }
        }

        buffers.endBatch(renderType);
    }

    private static void drawGlowFace(VertexConsumer buf, PoseStack ps,
                                      float ox, float oy, float oz,
                                      Direction dir, float cosA, float sinA, int alpha) {
        PoseStack.Pose pose = ps.last();

        float mn = -INFLATE;
        float mx = 1.0f + INFLATE;

        float x0 = ox + mn, x1 = ox + mx;
        float y0 = oy + mn, y1 = oy + mx;
        float z0 = oz + mn, z1 = oz + mx;

        switch (dir) {
            case UP    -> quad(buf, pose, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0,
                               255, 255, 255, alpha,  0f,  1f,  0f, cosA, sinA);
            case DOWN  -> quad(buf, pose, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1,
                               255, 255, 255, alpha,  0f, -1f,  0f, cosA, sinA);
            case SOUTH -> quad(buf, pose, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1,
                               255, 255, 255, alpha,  0f,  0f,  1f, cosA, sinA);
            case NORTH -> quad(buf, pose, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0,
                               255, 255, 255, alpha,  0f,  0f, -1f, cosA, sinA);
            case EAST  -> quad(buf, pose, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0,
                               255, 255, 255, alpha,  1f,  0f,  0f, cosA, sinA);
            case WEST  -> quad(buf, pose, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1,
                               255, 255, 255, alpha, -1f,  0f,  0f, cosA, sinA);
        }
    }

    private static void computeUVs(float cosA, float sinA) {
        float[] us = {0f, 1f, 1f, 0f};
        float[] vs = {0f, 0f, 1f, 1f};
        for (int i = 0; i < 4; i++) {
            float cu = us[i] - 0.5f, cv = vs[i] - 0.5f;
            UV[i * 2]     = cu * cosA - cv * sinA + 0.5f;
            UV[i * 2 + 1] = cu * sinA + cv * cosA + 0.5f;
        }
    }

    private static void quad(VertexConsumer buf, PoseStack.Pose pose,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int r, int g, int b, int a,
                              float nx, float ny, float nz,
                              float cosA, float sinA) {
        computeUVs(cosA, sinA);

        PNRM.set(nx, ny, nz);
        pose.normal().transform(PNRM);

        int overlay = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

        pose.pose().transformPosition(x1, y1, z1, PPOS);
        buf.addVertex(PPOS.x, PPOS.y, PPOS.z).setColor(r, g, b, a).setUv(UV[0], UV[1])
           .setOverlay(overlay).setLight(FULL_BRIGHT).setNormal(PNRM.x, PNRM.y, PNRM.z);

        pose.pose().transformPosition(x2, y2, z2, PPOS);
        buf.addVertex(PPOS.x, PPOS.y, PPOS.z).setColor(r, g, b, a).setUv(UV[2], UV[3])
           .setOverlay(overlay).setLight(FULL_BRIGHT).setNormal(PNRM.x, PNRM.y, PNRM.z);

        pose.pose().transformPosition(x3, y3, z3, PPOS);
        buf.addVertex(PPOS.x, PPOS.y, PPOS.z).setColor(r, g, b, a).setUv(UV[4], UV[5])
           .setOverlay(overlay).setLight(FULL_BRIGHT).setNormal(PNRM.x, PNRM.y, PNRM.z);

        pose.pose().transformPosition(x4, y4, z4, PPOS);
        buf.addVertex(PPOS.x, PPOS.y, PPOS.z).setColor(r, g, b, a).setUv(UV[6], UV[7])
           .setOverlay(overlay).setLight(FULL_BRIGHT).setNormal(PNRM.x, PNRM.y, PNRM.z);
    }
}

