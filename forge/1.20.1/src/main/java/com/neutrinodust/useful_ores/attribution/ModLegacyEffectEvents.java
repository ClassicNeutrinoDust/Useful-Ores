package com.neutrinodust.useful_ores.attribution;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModLegacyEffectEvents {
    private static final double WIND_BURST_RADIUS = 3.5D;
    public ModLegacyEffectEvents() {}

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level) || event.getAmount() <= 0.0F) return;
        MobEffectInstance infested = victim.getEffect(ModLegacyEffects.INFESTED.get());
        
        if (infested == null || victim instanceof Silverfish || victim.getRandom().nextFloat() >= 0.10F) return;
        int count = 1 + victim.getRandom().nextInt(2);
        Vec3 centre = victim.getBoundingBox().getCenter();
        Vec3 facing = victim.getLookAngle().normalize().scale(0.18D);
        for (int i = 0; i < count; i++) {
            Silverfish silverfish = EntityType.SILVERFISH.create(level);
            if (silverfish == null) continue;
            silverfish.moveTo(centre.x, centre.y, centre.z, victim.getYRot(), 0.0F);
            if (!level.addFreshEntity(silverfish)) continue;
            silverfish.setDeltaMovement(facing.x + (victim.getRandom().nextDouble() - 0.5D) * 0.08D,
                    0.12D + victim.getRandom().nextDouble() * 0.08D,
                    facing.z + (victim.getRandom().nextDouble() - 0.5D) * 0.08D);
            silverfish.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        if (victim.hasEffect(ModLegacyEffects.OOZING.get()) && !(victim instanceof Slime)) spawnSlimes(level, victim);
        if (victim.hasEffect(ModLegacyEffects.WEAVING.get())) spawnCobwebs(level, victim);
        if (victim.hasEffect(ModLegacyEffects.WIND_CHARGED.get())) windBurst(level, victim);
    }

    private static void spawnSlimes(ServerLevel level, LivingEntity victim) {
        
        AABB area = new AABB(victim.getX() - 2.5D, victim.getY() - 2.5D, victim.getZ() - 2.5D,
                victim.getX() + 2.5D, victim.getY() + 2.5D, victim.getZ() + 2.5D);
        int cap = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
        int existing = level.getEntitiesOfClass(Slime.class, area).size();
        if (cap > 0 && existing > cap) return;

        for (int i = 0; i < 2; i++) {
            if (cap > 0 && level.getEntitiesOfClass(Slime.class, area).size() >= cap) break;
            Slime slime = EntityType.SLIME.create(level);
            if (slime == null) continue;
            slime.setSize(2, true); 
            slime.moveTo(victim.getX() + (level.getRandom().nextDouble() - 0.5D), victim.getY(),
                    victim.getZ() + (level.getRandom().nextDouble() - 0.5D), level.getRandom().nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(slime);
        }
    }

    private static void spawnCobwebs(ServerLevel level, LivingEntity victim) {

        if (!(victim instanceof net.minecraft.world.entity.player.Player)
                && !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;

        BlockPos origin = victim.blockPosition();
        int targetCount = 2 + level.getRandom().nextInt(2);
        java.util.List<BlockPos> candidates = new java.util.ArrayList<>(27);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) candidates.add(origin.offset(dx, dy, dz));
            }
        }
        
        for (int i = candidates.size() - 1; i > 0; i--) {
            int j = level.getRandom().nextInt(i + 1);
            BlockPos temp = candidates.get(i);
            candidates.set(i, candidates.get(j));
            candidates.set(j, temp);
        }

        int placed = 0;
        for (BlockPos pos : candidates) {
            if (placed >= targetCount) break;
            if (!level.isEmptyBlock(pos)) continue;
            BlockPos below = pos.below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) continue;
            if (level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3)) placed++;
        }
    }

    private static void windBurst(ServerLevel level, LivingEntity victim) {
        Vec3 centre = victim.position().add(0.0D, victim.getBbHeight() * 0.5D, 0.0D);
        AABB area = new AABB(centre, centre).inflate(WIND_BURST_RADIUS);
        for (Entity target : level.getEntities(victim, area, e -> e.isAlive() && e != victim)) {
            Vec3 delta = target.position().subtract(centre);
            double distance = Math.max(0.25D, delta.length());
            if (distance > WIND_BURST_RADIUS) continue;
            Vec3 impulse = delta.scale((1.0D - distance / WIND_BURST_RADIUS) * 1.15D / distance);
            target.push(impulse.x, Math.max(0.15D, impulse.y + 0.20D), impulse.z);
            target.hurtMarked = true;
        }
        triggerWindBurstBlocks(level, centre);
        level.sendParticles(ParticleTypes.POOF, centre.x, centre.y, centre.z, 24, 0.8D, 0.6D, 0.8D, 0.04D);
        level.playSound(null, victim.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, 1.5F);
    }

    private static void triggerWindBurstBlocks(ServerLevel level, Vec3 centre) {
        int minX = net.minecraft.util.Mth.floor(centre.x - WIND_BURST_RADIUS);
        int maxX = net.minecraft.util.Mth.floor(centre.x + WIND_BURST_RADIUS);
        int minY = net.minecraft.util.Mth.floor(centre.y - WIND_BURST_RADIUS);
        int maxY = net.minecraft.util.Mth.floor(centre.y + WIND_BURST_RADIUS);
        int minZ = net.minecraft.util.Mth.floor(centre.z - WIND_BURST_RADIUS);
        int maxZ = net.minecraft.util.Mth.floor(centre.z + WIND_BURST_RADIUS);
        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            double dx = pos.getX() + 0.5D - centre.x;
            double dy = pos.getY() + 0.5D - centre.y;
            double dz = pos.getZ() + 0.5D - centre.z;
            if (dx * dx + dy * dy + dz * dz > WIND_BURST_RADIUS * WIND_BURST_RADIUS) continue;
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;

            if (state.getBlock() instanceof BaseFireBlock) {
                level.removeBlock(pos, false);
                level.levelEvent(null, 1009, pos, 0);
                continue;
            }

            if (state.getBlock() instanceof CampfireBlock || state.getBlock() instanceof CandleBlock
                    || state.getBlock() instanceof CandleCakeBlock) {
                if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
                    level.setBlock(pos, state.setValue(BlockStateProperties.LIT, false), 3);
                    level.levelEvent(null, 1009, pos, 0);
                }
                continue;
            }

            if (state.getBlock() instanceof ButtonBlock || state.getBlock() instanceof LeverBlock
                    || state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock
                    || state.getBlock() instanceof FenceGateBlock || state.getBlock() instanceof BellBlock) {
                BlockHitResult hit = new BlockHitResult(
                        new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D),
                        Direction.UP, pos, false);
                state.use(level, null, InteractionHand.MAIN_HAND, hit);
            }
        }
    }

}
