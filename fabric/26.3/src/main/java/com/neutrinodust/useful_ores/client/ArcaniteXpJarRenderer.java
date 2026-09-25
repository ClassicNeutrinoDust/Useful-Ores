package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.neutrinodust.useful_ores.client.compat.BlockEntityRenderer121X;
import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import com.neutrinodust.useful_ores.client.compat.SubmitNodeBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ArcaniteXpJarRenderer implements BlockEntityRenderer121X<ArcaniteXpJarBlockEntity> {

    private static final Identifier ORB_TEXTURE =
            Identifier.fromNamespaceAndPath("useful_ores", "textures/entity/arcanite_xp_orb.png");

    private static final RenderType RENDER_TYPE = RenderTypes.entityTranslucentCull(ORB_TEXTURE);

    private static final int FULL_BRIGHT = 0xF000F0;

    private static final int MAX_ORBS = 80;
    private static final float ORB_HALF_SIZE = 0.045f;

    private static final float X0 = 4f / 16f, X1 = 12f / 16f;
    private static final float Y0 = 1f / 16f, Y1 = 11f / 16f;
    private static final float Z0 = 4f / 16f, Z1 = 12f / 16f;

    private static final Quaternionf SCRATCH_ROT = new Quaternionf();
    private static final Vector3f    SCRATCH_P   = new Vector3f();
    private static final Vector3f    SCRATCH_N   = new Vector3f();

    public ArcaniteXpJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ArcaniteXpJarBlockEntity be, float partialTick, PoseStack poseStack,
                        SubmitNodeBufferSource bufferSource, int packedLight, int packedOverlay) {
        int stored = be.getStoredXp();
        if (stored <= 0) {
            return;
        }

        float fraction = clamp01(stored / (float) ArcaniteXpJarBlockEntity.MAX_XP);
        int orbCount = Math.max(1, Math.round(fraction * MAX_ORBS));

        long gameTime = be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        float t = gameTime + partialTick;

        RenderType renderType = RENDER_TYPE;
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        Quaternionf camRotation = camera.rotation();

        final int LAYERS = 4;
        final int ORBS_PER_LAYER = (int) Math.ceil(MAX_ORBS / (float) LAYERS);
        final int GRID_COLS = Math.max(1, (int) Math.ceil(Math.sqrt(ORBS_PER_LAYER)));
        final int GRID_ROWS = Math.max(1, (int) Math.ceil(ORBS_PER_LAYER / (float) GRID_COLS));

        float usableW = (X1 - X0) - ORB_HALF_SIZE * 2f;
        float usableD = (Z1 - Z0) - ORB_HALF_SIZE * 2f;
        float cellW = usableW / GRID_COLS;
        float cellD = usableD / GRID_ROWS;

        float pileHeightAvailable = Math.max(0.01f, (Y1 - Y0) - ORB_HALF_SIZE * 2f);
        float layerStep = pileHeightAvailable / LAYERS;

        for (int i = 0; i < orbCount; i++) {
            long seed = i * 2654435761L + 12345L;
            float jitterX = (hashFloat(seed, 1) - 0.5f) * 0.6f;
            float jitterZ = (hashFloat(seed, 2) - 0.5f) * 0.6f;
            float jitterY = hashFloat(seed, 3);
            float phase = hashFloat(seed, 4) * (float) (Math.PI * 2.0);
            float spinSpeed = 1.5f + hashFloat(seed, 5) * 2.5f;

            int layer = i / ORBS_PER_LAYER;
            int indexInLayer = i % ORBS_PER_LAYER;
            int gx = indexInLayer % GRID_COLS;
            int gz = indexInLayer / GRID_COLS;

            float px = X0 + ORB_HALF_SIZE + (gx + 0.5f + jitterX) * cellW;
            float pz = Z0 + ORB_HALF_SIZE + (gz + 0.5f + jitterZ) * cellD;
            float baseY = Y0 + ORB_HALF_SIZE + layer * layerStep + jitterY * layerStep * 0.5f;
            float bob = (float) Math.sin(t * 0.06f + phase) * 0.008f;
            float py = baseY + bob;

            float rollDeg = (t * spinSpeed + phase * 57.3f) % 360f;

            float rr = (t * 0.5f + phase * 6f) / 2.0f;
            int rc = (int) ((Mth.sin(rr) + 1.0f) * 0.5f * 255.0f);
            int bc = (int) ((Mth.sin(rr + (float) (Math.PI * 4.0 / 3.0)) + 1.0f) * 0.1f * 255.0f);

            drawOrbBillboard(buffer, camRotation, px, py, pz, ORB_HALF_SIZE, rollDeg, rc, bc, FULL_BRIGHT);
        }
    }

    private static float hashFloat(long seed, int salt) {
        long h = seed * 6364136223846793005L + salt * 1442695040888963407L;
        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        return (h & 0xFFFFFF) / (float) 0x1000000;
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    private static void drawOrbBillboard(VertexConsumer buffer, Quaternionf camRotation,
                                          float cx, float cy, float cz, float halfSize, float rollDeg,
                                          int rc, int bc, int light) {

        Quaternionf rot = SCRATCH_ROT.set(camRotation).rotateZ((float) Math.toRadians(rollDeg));

        SCRATCH_N.set(0f, 1f, 0f).rotate(rot);
        float nx = SCRATCH_N.x(), ny = SCRATCH_N.y(), nz = SCRATCH_N.z();

        SCRATCH_P.set(-halfSize, -halfSize, 0f).rotate(rot);
        vertex(buffer, SCRATCH_P, cx, cy, cz, 0f, 1f, nx, ny, nz, rc, bc, light);
        SCRATCH_P.set(halfSize, -halfSize, 0f).rotate(rot);
        vertex(buffer, SCRATCH_P, cx, cy, cz, 1f, 1f, nx, ny, nz, rc, bc, light);
        SCRATCH_P.set(halfSize, halfSize, 0f).rotate(rot);
        vertex(buffer, SCRATCH_P, cx, cy, cz, 1f, 0f, nx, ny, nz, rc, bc, light);
        SCRATCH_P.set(-halfSize, halfSize, 0f).rotate(rot);
        vertex(buffer, SCRATCH_P, cx, cy, cz, 0f, 0f, nx, ny, nz, rc, bc, light);
    }

    private static void vertex(VertexConsumer buffer, Vector3f local, float cx, float cy, float cz,
                                float u, float v, float nx, float ny, float nz, int rc, int bc, int light) {
        buffer.addVertex(local.x() + cx, local.y() + cy, local.z() + cz)
                .setColor(rc, 255, bc, 128).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }
}

