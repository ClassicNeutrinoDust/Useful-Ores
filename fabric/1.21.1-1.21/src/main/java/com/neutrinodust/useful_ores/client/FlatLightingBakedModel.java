package com.neutrinodust.useful_ores.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


public final class FlatLightingBakedModel implements BakedModel {
    private static final Map<BakedModel, FlatLightingBakedModel> FLAT_CACHE =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<BakedModel, FlatLightingBakedModel> GUI_CACHE =
            Collections.synchronizedMap(new IdentityHashMap<>());

    private final BakedModel wrapped;
    private final boolean removeDirectionalQuadShading;
    private final Map<List<BakedQuad>, List<BakedQuad>> quadCache =
            Collections.synchronizedMap(new IdentityHashMap<>());

    private FlatLightingBakedModel(BakedModel wrapped, boolean removeDirectionalQuadShading) {
        this.wrapped = wrapped;
        this.removeDirectionalQuadShading = removeDirectionalQuadShading;
    }

    public static BakedModel wrap(BakedModel model, boolean removeDirectionalQuadShading) {
        if (model == null || model instanceof FlatLightingBakedModel) return model;
        Map<BakedModel, FlatLightingBakedModel> cache = removeDirectionalQuadShading ? GUI_CACHE : FLAT_CACHE;
        return cache.computeIfAbsent(model, m -> new FlatLightingBakedModel(m, removeDirectionalQuadShading));
    }

    @Override public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random) {
        List<BakedQuad> original = wrapped.getQuads(state, face, random);
        if (!removeDirectionalQuadShading || original.isEmpty()) return original;
        return quadCache.computeIfAbsent(original, FlatLightingBakedModel::removeShading);
    }
    @Override public boolean useAmbientOcclusion() { return wrapped.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return wrapped.isGui3d(); }
    @Override public boolean usesBlockLight() { return false; }
    @Override public boolean isCustomRenderer() { return wrapped.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return wrapped.getParticleIcon(); }
    @Override public ItemTransforms getTransforms() { return wrapped.getTransforms(); }
    @Override public ItemOverrides getOverrides() { return wrapped.getOverrides(); }

    private static List<BakedQuad> removeShading(List<BakedQuad> original) {
        List<BakedQuad> transformed = new ArrayList<>(original.size());
        boolean changed = false;
        for (BakedQuad quad : original) {
            if (!quad.isShade()) { transformed.add(quad); continue; }
            transformed.add(new BakedQuad(quad.getVertices().clone(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), false));
            changed = true;
        }
        return changed ? Collections.unmodifiableList(transformed) : original;
    }
}
