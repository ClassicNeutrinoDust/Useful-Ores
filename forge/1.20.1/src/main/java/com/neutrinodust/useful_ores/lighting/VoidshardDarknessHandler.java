package com.neutrinodust.useful_ores.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VoidshardDarknessHandler {

    private static final int RADIUS = 6;
    private static final int RADIUS_SQ = RADIUS * RADIUS;

    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int EFFECT_DURATION_TICKS = 1200;
    private static final int REFRESH_BELOW_TICKS = SCAN_INTERVAL_TICKS * 2;
    private final java.util.Map<Player, Integer> affectedPlayers = new java.util.WeakHashMap<>();

    @SubscribeEvent
    public void onSpawnPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity().getType().getCategory() != MobCategory.MONSTER) return;

        if (event.getResult() != MobSpawnEvent.PositionCheck.Result.DEFAULT) return;

        BlockPos pos = BlockPos.containing(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
        if (isNearVoidshardDarkSource(event.getLevel(), pos)) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;

        if (player.level().isClientSide) return;
        if (player.tickCount % SCAN_INTERVAL_TICKS != 0) return;

        boolean nearSource = isNearVoidshardDarkSource(player.level(), player.blockPosition());
        MobEffectInstance current = player.getEffect(MobEffects.DARKNESS);
        if (nearSource) {
            if (current == null || current.getDuration() <= REFRESH_BELOW_TICKS) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.DARKNESS,
                        EFFECT_DURATION_TICKS,
                        0,
                        true,
                        false,
                        true
                ));
                affectedPlayers.put(player, player.tickCount);
            }
        } else {
            Integer appliedAt = affectedPlayers.remove(player);
            if (appliedAt != null && current != null && current.getAmplifier() == 0) {

                int expectedRemaining = Math.max(0, EFFECT_DURATION_TICKS - (player.tickCount - appliedAt));
                if (Math.abs(current.getDuration() - expectedRemaining) <= 2) {
                    player.removeEffect(MobEffects.DARKNESS);
                }
            }
        }
    }

    private boolean isNearVoidshardDarkSource(net.minecraft.world.level.LevelReader level, BlockPos center) {
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

