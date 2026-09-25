package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.util.NbtCompat;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DarkWormholePortalBlockEntity extends BlockEntity implements software.bernie.geckolib.animatable.GeoAnimatable {

    private static final int LIFETIME_TICKS = 300;
    private static final double PULL_RADIUS_XZ = 1.5;
    private static final double PULL_RADIUS_Y = 2.0;
    private static final double PULL_STRENGTH = 0.11;
    private static final double SUCK_DISTANCE = 0.55;

    private static final int MAX_ENTITIES_PER_TICK = 24;

    private static final int STUCK_GIVEUP_TICKS = 100;

    private GlobalPos targetPedestal;
    private int age = 0;

    private boolean destinationChunkForced = false;

    private final Map<UUID, Long> recentlyArrived = new HashMap<>();
    private static final int ARRIVAL_GRACE_TICKS = 40;

    private final Map<UUID, Integer> stuckTicks = new HashMap<>();

    public DarkWormholePortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DARK_WORMHOLE_PORTAL.get(), pos, state);
    }

    public void setTargetPedestal(GlobalPos target) {
        this.targetPedestal = target;
        this.setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DarkWormholePortalBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        be.age++;

        be.sweepTrackingMaps(serverLevel.getGameTime());

        if (be.age >= LIFETIME_TICKS) {
            be.releaseDestinationChunk(serverLevel);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 40, 0.6, 0.3, 0.6, 0.05);
            serverLevel.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.7F, 0.5F);
            serverLevel.removeBlock(pos, false);
            return;
        }

        if (be.targetPedestal == null) return;

        be.keepDestinationChunkLoaded(serverLevel);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.1;
        double cz = pos.getZ() + 0.5;

        AABB pullBox = new AABB(
                cx - PULL_RADIUS_XZ, cy - 1.0, cz - PULL_RADIUS_XZ,
                cx + PULL_RADIUS_XZ, cy + PULL_RADIUS_Y, cz + PULL_RADIUS_XZ);

        int processed = 0;
        Set<UUID> stillPresent = new HashSet<>();

        for (LivingEntity living : serverLevel.getEntitiesOfClass(LivingEntity.class, pullBox)) {
            if (!(living instanceof Mob)) continue;
            if (!living.isAlive()) continue;

            if (processed >= MAX_ENTITIES_PER_TICK) break;

            UUID id = living.getUUID();
            stillPresent.add(id);

            Long graceUntil = be.recentlyArrived.get(id);
            if (graceUntil != null) {
                if (serverLevel.getGameTime() < graceUntil) continue;
                be.recentlyArrived.remove(id);
            }

            processed++;

            Vec3 toCentre = new Vec3(cx - living.getX(), cy - living.getY(), cz - living.getZ());
            double dist = toCentre.length();

            if (dist <= SUCK_DISTANCE) {
                boolean teleported = be.teleportToPedestal(serverLevel, living, be.targetPedestal);
                if (teleported) {
                    be.stuckTicks.remove(id);
                    continue;
                }

            }

            int stuck = be.stuckTicks.merge(id, 1, Integer::sum);
            if (stuck >= STUCK_GIVEUP_TICKS) {

                be.stuckTicks.remove(id);
                be.recentlyArrived.put(id, serverLevel.getGameTime() + ARRIVAL_GRACE_TICKS * 4L);
                continue;
            }

            Vec3 pull = toCentre.normalize().scale(PULL_STRENGTH);
            living.setDeltaMovement(living.getDeltaMovement().add(pull));
            living.hurtMarked = true;

            if (be.age % 5 == 0) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(),
                        3, 0.15, 0.15, 0.15, 0.02);
            }
        }

        be.stuckTicks.keySet().retainAll(stillPresent);
    }

    private void sweepTrackingMaps(long now) {
        if (!this.recentlyArrived.isEmpty()) {
            this.recentlyArrived.values().removeIf(expiry -> now >= expiry);
        }
    }

    private void keepDestinationChunkLoaded(ServerLevel sourceLevel) {
        if (destinationChunkForced || targetPedestal == null) return;
        ServerLevel targetLevel = sourceLevel.getServer().getLevel(targetPedestal.dimension());
        if (targetLevel == null) return;
        BlockPos p=targetPedestal.pos();
        ChunkPos chunk=new ChunkPos(p.getX() >> 4,p.getZ() >> 4);
        targetLevel.setChunkForced(chunk.x, chunk.z, true);
        targetLevel.getChunkAt(targetPedestal.pos());
        destinationChunkForced = true;
    }

    private void releaseDestinationChunk(ServerLevel sourceLevel) {
        if (!destinationChunkForced || targetPedestal == null) return;
        ServerLevel targetLevel = sourceLevel.getServer().getLevel(targetPedestal.dimension());
        if (targetLevel != null) {
            BlockPos p=targetPedestal.pos();
        ChunkPos chunk=new ChunkPos(p.getX() >> 4,p.getZ() >> 4);
            targetLevel.setChunkForced(chunk.x, chunk.z, false);
        }
        destinationChunkForced = false;
    }

    private boolean teleportToPedestal(ServerLevel fromLevel, LivingEntity mob, GlobalPos target) {

        ServerLevel targetLevel = fromLevel.getServer().getLevel(target.dimension());
        if (targetLevel == null) return false;
        targetLevel.getChunkAt(target.pos());
        if (!targetLevel.getBlockState(target.pos()).is(ModItems.ANCIENT_PEDESTAL.get())) return false;

        double tx = target.pos().getX() + 0.5;
        double ty = target.pos().getY() + 1.1;
        double tz = target.pos().getZ() + 0.5;

        fromLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                mob.getX(), mob.getY() + 0.5, mob.getZ(), 16, 0.3, 0.3, 0.3, 0.05);
        fromLevel.playSound(null, mob.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.6F, 1.0F);

        if (targetLevel == fromLevel) {

            mob.setPos(tx, ty, tz);
            mob.setDeltaMovement(Vec3.ZERO);
            mob.setOldPosAndRot();
        } else {

            mob.setDeltaMovement(Vec3.ZERO);
            mob.teleportTo(targetLevel, tx, ty, tz, java.util.Set.of(), mob.getYRot(), mob.getXRot(), false);
        }

        targetLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, tx, ty, tz, 20, 0.3, 0.3, 0.3, 0.05);
        targetLevel.playSound(null, target.pos(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.6F, 1.0F);

        this.recentlyArrived.put(mob.getUUID(), fromLevel.getGameTime() + ARRIVAL_GRACE_TICKS);
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        output.putInt("Age", age);
        NbtCompat.storeNullable(output, "TargetPedestal", GlobalPos.CODEC, targetPedestal);
    }

    @Override
    protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        age = NbtCompat.getIntOr(input, "Age", 0);
        targetPedestal = NbtCompat.readCodec(input, "TargetPedestal", GlobalPos.CODEC).orElse(null);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    private final software.bernie.geckolib.animatable.instance.AnimatableInstanceCache geoCache =
            software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache(this);

    @Override
    public software.bernie.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public double getTick(Object object) {
        return this.level != null ? this.level.getGameTime() : 0.0D;
    }
}

