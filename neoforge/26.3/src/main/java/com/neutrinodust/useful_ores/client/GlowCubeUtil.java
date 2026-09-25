package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.neutrinodust.useful_ores.client.compat.SubmitNodeBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

final class GlowCubeUtil {

    private static final int FULL_BRIGHT = 0xF000F0;

    private static final float CORE_MIN = 6f / 16f;
    private static final float CORE_MAX = 10f / 16f;
    private static final float CORE_MIN_Y = 2f / 16f;
    private static final float CORE_MAX_Y = 8f / 16f;

    private static final float INFLATE = 0.015f;

    private static final float SIDE_U0 = 6f / 16f, SIDE_V0 = 8f / 16f, SIDE_U1 = 10f / 16f, SIDE_V1 = 14f / 16f;

    private static final float CAP_U0 = 6f / 16f, CAP_V0 = 6f / 16f, CAP_U1 = 10f / 16f, CAP_V1 = 10f / 16f;

    private GlowCubeUtil() {
    }

    static void renderCoreGlow(SubmitNodeBufferSource bufferSource, Identifier gemOnTexture,
                                int r, int g, int b, int a) {
        RenderType renderType = RenderTypes.entityTranslucentEmissive(gemOnTexture);
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        float x0 = CORE_MIN - INFLATE, x1 = CORE_MAX + INFLATE;
        float y0 = CORE_MIN_Y - INFLATE, y1 = CORE_MAX_Y + INFLATE;
        float z0 = CORE_MIN - INFLATE, z1 = CORE_MAX + INFLATE;

        quad(buffer, r, g, b, a,
                x0, y0, z0, x0, y0, z1, x1, y0, z1, x1, y0, z0,
                0, -1, 0, CAP_U0, CAP_V0, CAP_U1, CAP_V1);

        quad(buffer, r, g, b, a,
                x0, y1, z1, x0, y1, z0, x1, y1, z0, x1, y1, z1,
                0, 1, 0, CAP_U0, CAP_V0, CAP_U1, CAP_V1);

        quad(buffer, r, g, b, a,
                x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0,
                0, 0, -1, SIDE_U0, SIDE_V0, SIDE_U1, SIDE_V1);

        quad(buffer, r, g, b, a,
                x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1,
                0, 0, 1, SIDE_U0, SIDE_V0, SIDE_U1, SIDE_V1);

        quad(buffer, r, g, b, a,
                x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0,
                1, 0, 0, SIDE_U0, SIDE_V0, SIDE_U1, SIDE_V1);

        quad(buffer, r, g, b, a,
                x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1,
                -1, 0, 0, SIDE_U0, SIDE_V0, SIDE_U1, SIDE_V1);
    }

    private static void quad(VertexConsumer buffer, int r, int g, int b, int a,
                              float x1, float y1, float z1, float x2, float y2, float z2,
                              float x3, float y3, float z3, float x4, float y4, float z4,
                              float nx, float ny, float nz,
                              float u0, float v0, float u1, float v1) {
        buffer.addVertex(x1, y1, z1).setColor(r, g, b, a).setUv(u0, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
        buffer.addVertex(x2, y2, z2).setColor(r, g, b, a).setUv(u0, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
        buffer.addVertex(x3, y3, z3).setColor(r, g, b, a).setUv(u1, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
        buffer.addVertex(x4, y4, z4).setColor(r, g, b, a).setUv(u1, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
    }
}

