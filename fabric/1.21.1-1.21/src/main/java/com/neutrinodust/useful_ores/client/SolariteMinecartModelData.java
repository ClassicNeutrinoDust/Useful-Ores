package com.neutrinodust.useful_ores.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class SolariteMinecartModelData {

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        PartDefinition bone = parts.addOrReplaceChild("bone", CubeListBuilder.create(),
            PartPose.offsetAndRotation(0f, 5.5f, 0f,
                (float) Math.toRadians(0), (float) Math.toRadians(90), (float) Math.toRadians(180)));

        bone.addOrReplaceChild("cube1", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0f, 0f, 0f, 16f, 8f, 2f),
            PartPose.offsetAndRotation(-5f, 2f, -8f,
                (float) Math.toRadians(0), (float) Math.toRadians(-90), (float) Math.toRadians(0)));

        bone.addOrReplaceChild("cube2", CubeListBuilder.create()
                .texOffs(0, 10)
                .addBox(-1f, 0f, 0f, 16f, 8f, 2f),
            PartPose.offsetAndRotation(9f, 2f, -7f,
                (float) Math.toRadians(0), (float) Math.toRadians(-90), (float) Math.toRadians(0)));

        bone.addOrReplaceChild("cube3", CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(-8f, -4f, 0f, 16f, 8f, 2f),
            PartPose.offsetAndRotation(1f, 6f, -8f,
                (float) Math.toRadians(0), (float) Math.toRadians(-180), (float) Math.toRadians(0)));

        bone.addOrReplaceChild("cube4", CubeListBuilder.create()
                .texOffs(0, 30)
                .addBox(-8f, -4f, 0f, 16f, 8f, 2f),
            PartPose.offsetAndRotation(1f, 6f, 10f,
                (float) Math.toRadians(0), (float) Math.toRadians(-180), (float) Math.toRadians(0)));

        bone.addOrReplaceChild("cube5", CubeListBuilder.create()
                .texOffs(0, 40)
                .addBox(-11f, -8f, -1f, 20f, 16f, 2f),
            PartPose.offsetAndRotation(1f, 1f, 1f,
                (float) Math.toRadians(0), (float) Math.toRadians(-90), (float) Math.toRadians(90)));

        bone.addOrReplaceChild("cube6", CubeListBuilder.create()
                .texOffs(0, 58)
                .addBox(-5f, -1f, -1f, 12f, 5f, 2f),
            PartPose.offsetAndRotation(0f, 5f, -11f,
                (float) Math.toRadians(0), (float) Math.toRadians(0), (float) Math.toRadians(0)));

        return LayerDefinition.create(mesh, 44, 65);
    }

    private SolariteMinecartModelData() {}
}

