package com.neutrinodust.useful_ores.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class VoidshardDarknessHandler {

    public static final VoidshardDarknessHandler INSTANCE = new VoidshardDarknessHandler();

    private static final int RADIUS = 6;
    private static final int RADIUS_SQ = RADIUS * RADIUS;

    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int EFFECT_DURATION_TICKS = 30;

    public void onPlayerTick(Player player) {
        if (player.tickCount % SCAN_INTERVAL_TICKS != 0) return;

        if (isNearVoidshardDarkSource(player.level(), player.blockPosition())) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.DARKNESS,
                    EFFECT_DURATION_TICKS,
                    0,
                    true,
                    false,
                    true
            ));
        }
    }

    public boolean isNearVoidshardDarkSource(net.minecraft.world.level.LevelReader level, BlockPos center) {
        boolean hasLantern = ModVoidshardLightBlocks.VOIDSHARD_LANTERN != null;
        boolean hasTorch = ModVoidshardLightBlocks.VOIDSHARD_TORCH != null;
        boolean hasWallTorch = ModVoidshardLightBlocks.VOIDSHARD_WALL_TORCH != null;
        boolean hasCampfire = com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks.VOIDSHARD_CAMPFIRE != null;
        if (!hasLantern && !hasTorch && !hasWallTorch && !hasCampfire) return false;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > RADIUS_SQ) continue;
                    cursor.setWithOffset(center, dx, dy, dz);
                    BlockState state = level.getBlockState(cursor);
                    if (hasLantern && state.is(ModVoidshardLightBlocks.VOIDSHARD_LANTERN.get())) return true;
                    if (hasTorch && state.is(ModVoidshardLightBlocks.VOIDSHARD_TORCH.get())) return true;
                    if (hasWallTorch && state.is(ModVoidshardLightBlocks.VOIDSHARD_WALL_TORCH.get())) return true;

                    if (hasCampfire && state.is(com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks.VOIDSHARD_CAMPFIRE.get())
                            && state.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)
                            && state.getValue(net.minecraft.world.level.block.CampfireBlock.LIT)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

