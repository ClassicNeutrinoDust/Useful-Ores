package com.neutrinodust.useful_ores.client.compat;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.MeshData.DrawState;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class SubmitNodeBufferSource {
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

    private static final int DEFAULT_BUFFER_CAPACITY = 2048;

    public VertexConsumer getBuffer(RenderType renderType) {
        return this.builders.computeIfAbsent(renderType, type -> {
            ByteBufferBuilder bytes = new ByteBufferBuilder(DEFAULT_BUFFER_CAPACITY);
            this.memory.put(type, bytes);
            return new BufferBuilder(bytes, type.primitiveTopology(), type.format());
        });
    }

    public void flushInto(SubmitNodeCollector collector, PoseStack poseStack) {
        OrderedSubmitNodeCollector ordered = collector.order(0);
        this.builders.forEach((type, builder) -> {
            MeshData mesh = builder.build();
            ByteBufferBuilder bytes = this.memory.get(type);
            if (mesh == null) {
                if (bytes != null) {
                    bytes.close();
                }
            } else {
                ordered.submitCustomGeometry(poseStack, type, (pose, consumer) -> {
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
        boolean hasColor = format.contains(DefaultVertexFormat.COLOR_SEMANTIC_NAME);
        boolean hasUv0 = format.contains(DefaultVertexFormat.UV0_SEMANTIC_NAME);
        boolean hasUv1 = format.contains(DefaultVertexFormat.UV1_SEMANTIC_NAME);
        boolean hasUv2 = format.contains(DefaultVertexFormat.UV2_SEMANTIC_NAME);
        boolean hasNormal = format.contains(DefaultVertexFormat.NORMAL_SEMANTIC_NAME);
        boolean hasLineWidth = format.contains(DefaultVertexFormat.LINE_WIDTH_SEMANTIC_NAME);
        int posOffset = format.getElement(DefaultVertexFormat.POSITION_SEMANTIC_NAME).offset();
        int colorOffset = hasColor ? format.getElement(DefaultVertexFormat.COLOR_SEMANTIC_NAME).offset() : 0;
        int uv0Offset = hasUv0 ? format.getElement(DefaultVertexFormat.UV0_SEMANTIC_NAME).offset() : 0;
        int uv1Offset = hasUv1 ? format.getElement(DefaultVertexFormat.UV1_SEMANTIC_NAME).offset() : 0;
        int uv2Offset = hasUv2 ? format.getElement(DefaultVertexFormat.UV2_SEMANTIC_NAME).offset() : 0;
        int normalOffset = hasNormal ? format.getElement(DefaultVertexFormat.NORMAL_SEMANTIC_NAME).offset() : 0;
        int lineWidthOffset = hasLineWidth ? format.getElement(DefaultVertexFormat.LINE_WIDTH_SEMANTIC_NAME).offset() : 0;

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

