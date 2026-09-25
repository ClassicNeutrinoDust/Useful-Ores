package com.neutrinodust.useful_ores.client.compat;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.MeshData.DrawState;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;

public class SubmitNodeBufferSource implements MultiBufferSource {
    private final Map<RenderType, BufferBuilder> builders = new LinkedHashMap<>();
    private final Map<RenderType, ByteBufferBuilder> memory = new LinkedHashMap<>();
    private SubmitNodeCollector liveCollector;
    private PoseStack basePose;
    private CameraRenderState cameraState;

    public void bindLive(SubmitNodeCollector collector, PoseStack basePose) {
        this.liveCollector = collector;
        this.basePose = basePose;
    }

    public void setCameraState(CameraRenderState cs) {
        this.cameraState = cs;
    }

    public CameraRenderState cameraState() {
        return this.cameraState;
    }

    public SubmitNodeCollector liveCollector() {
        return this.liveCollector;
    }

    public PoseStack basePose() {
        return this.basePose;
    }

    public VertexConsumer getBuffer(RenderType renderType) {
        return this.builders.computeIfAbsent(renderType, type -> {
            ByteBufferBuilder bytes = new ByteBufferBuilder(Math.max(786, type.bufferSize() / 16));
            this.memory.put(type, bytes);
            return new BufferBuilder(bytes, type.mode(), type.format());
        });
    }

    public void flushInto(SubmitNodeCollector collector, PoseStack poseStack) {
        this.builders.forEach((type, builder) -> {
            MeshData mesh = builder.build();
            ByteBufferBuilder bytes = this.memory.get(type);
            if (mesh == null) {
                if (bytes != null) {
                    bytes.close();
                }
            } else {
                collector.submitCustomGeometry(poseStack, type, (pose, consumer) -> {
                    replay(mesh, consumer, pose);
                    mesh.close();
                    if (bytes != null) {
                        bytes.close();
                    }
                });
            }
        });
        this.builders.clear();
        this.memory.clear();
    }

    private static void replay(MeshData mesh, VertexConsumer consumer, PoseStack.Pose pose) {
        DrawState drawState = mesh.drawState();
        VertexFormat format = drawState.format();
        int stride = format.getVertexSize();
        ByteBuffer vertexData = mesh.vertexBuffer().duplicate().order(ByteOrder.nativeOrder());
        boolean hasColor = format.contains(VertexFormatElement.COLOR);
        boolean hasUv0 = format.contains(VertexFormatElement.UV0);
        boolean hasUv1 = format.contains(VertexFormatElement.UV1);
        boolean hasUv2 = format.contains(VertexFormatElement.UV2);
        boolean hasNormal = format.contains(VertexFormatElement.NORMAL);
        boolean hasLineWidth = format.contains(VertexFormatElement.LINE_WIDTH);
        int posOffset = format.getOffset(VertexFormatElement.POSITION);
        int colorOffset = hasColor ? format.getOffset(VertexFormatElement.COLOR) : 0;
        int uv0Offset = hasUv0 ? format.getOffset(VertexFormatElement.UV0) : 0;
        int uv1Offset = hasUv1 ? format.getOffset(VertexFormatElement.UV1) : 0;
        int uv2Offset = hasUv2 ? format.getOffset(VertexFormatElement.UV2) : 0;
        int normalOffset = hasNormal ? format.getOffset(VertexFormatElement.NORMAL) : 0;
        int lineWidthOffset = hasLineWidth ? format.getOffset(VertexFormatElement.LINE_WIDTH) : 0;

        for (int i = 0; i < drawState.vertexCount(); i++) {
            int base = i * stride;

            org.joml.Vector3f pos = new org.joml.Vector3f(
                    vertexData.getFloat(base + posOffset),
                    vertexData.getFloat(base + posOffset + 4),
                    vertexData.getFloat(base + posOffset + 8));
            pose.pose().transformPosition(pos);
            consumer.addVertex(pos.x(), pos.y(), pos.z());
            if (hasColor) {
                consumer.setColor(
                        vertexData.get(base + colorOffset) & 255,
                        vertexData.get(base + colorOffset + 1) & 255,
                        vertexData.get(base + colorOffset + 2) & 255,
                        vertexData.get(base + colorOffset + 3) & 255
                );
            }
            if (hasUv0) {
                consumer.setUv(vertexData.getFloat(base + uv0Offset), vertexData.getFloat(base + uv0Offset + 4));
            }
            if (hasUv1) {
                consumer.setUv1(vertexData.getShort(base + uv1Offset), vertexData.getShort(base + uv1Offset + 2));
            }
            if (hasUv2) {
                consumer.setUv2(vertexData.getShort(base + uv2Offset), vertexData.getShort(base + uv2Offset + 2));
            }
            if (hasNormal) {
                org.joml.Vector3f normal = new org.joml.Vector3f(
                        vertexData.get(base + normalOffset) / 127.0F,
                        vertexData.get(base + normalOffset + 1) / 127.0F,
                        vertexData.get(base + normalOffset + 2) / 127.0F);
                pose.normal().transform(normal);
                consumer.setNormal(normal.x(), normal.y(), normal.z());
            }
            if (hasLineWidth) {
                consumer.setLineWidth(vertexData.getFloat(base + lineWidthOffset));
            }
        }
    }
}

