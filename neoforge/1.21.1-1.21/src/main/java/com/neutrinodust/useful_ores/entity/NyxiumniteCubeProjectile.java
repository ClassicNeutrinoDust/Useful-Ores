package com.neutrinodust.useful_ores.entity;

import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.particle.ColoredFlameOptions;
import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NyxiumniteCubeProjectile extends Projectile {

   private static final int MAX_LIFETIME_TICKS = 200;
   private static final int TRAIL_COLOR = 0x6A11CB;

   private GlobalPos targetPedestal;
   private int ticksAlive = 0;
   private boolean spent = false;

   public NyxiumniteCubeProjectile(EntityType<? extends NyxiumniteCubeProjectile> type, Level level) {
      super(type, level);
   }

   public NyxiumniteCubeProjectile(Level level, LivingEntity shooter, EntityType<? extends NyxiumniteCubeProjectile> type,
                                    GlobalPos targetPedestal) {
      super(type, level);
      this.targetPedestal = targetPedestal;
      setOwner(shooter);
      setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
   }

   @Override
   public void tick() {
      super.tick();
      if (spent) return;
      ticksAlive++;

      if (level().isClientSide() && ticksAlive % 2 == 0) {
         level().addParticle(new ColoredFlameOptions(TRAIL_COLOR, 0.35F), getX(), getY(), getZ(), 0, 0, 0);
      }

      Vec3 start = position();
      Vec3 end = start.add(getDeltaMovement());

      BlockHitResult blockHit = level().clip(new ClipContext(
            start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
      if (blockHit.getType() != HitResult.Type.MISS) {
         land(blockHit);
         return;
      }

      setPos(end.x, end.y, end.z);
      setDeltaMovement(getDeltaMovement().scale(0.995).add(0, -0.01, 0));

      for (net.minecraft.world.entity.Entity entity : level().getEntities(this, getBoundingBox().expandTowards(getDeltaMovement()).inflate(0.8))) {
         if (entity == getOwner()) continue;
         if (entity instanceof LivingEntity) {
            landAt(entity.blockPosition());
            return;
         }
      }

      if (ticksAlive > MAX_LIFETIME_TICKS) {
         landAt(blockPosition());
      }
   }

   private void land(BlockHitResult hit) {
      BlockPos landingSpot = hit.getBlockPos().relative(hit.getDirection());
      landAt(landingSpot);
   }

   private void landAt(BlockPos pos) {
      if (spent) return;
      spent = true;
      setDeltaMovement(Vec3.ZERO);

      if (level().isClientSide()) {
         discard();
         return;
      }

      ServerLevel serverLevel = (ServerLevel) level();

      BlockPos.MutableBlockPos ground = pos.mutable();
      for (int i = 0; i < 4; i++) {
         if (!serverLevel.getBlockState(ground.below()).isAir()) break;
         ground.move(0, -1, 0);
      }

      BlockPos finalGround = avoidPedestalOverlap(serverLevel, ground.immutable());

      openPortal(serverLevel, finalGround);

      serverLevel.playSound(null, finalGround, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 0.6F);
      serverLevel.sendParticles(ParticleTypes.PORTAL, finalGround.getX() + 0.5, finalGround.getY() + 0.3, finalGround.getZ() + 0.5,
            30, 0.6, 0.2, 0.6, 0.06);

      discard();
   }

   private static final int PEDESTAL_CLEARANCE = 3;

   private BlockPos avoidPedestalOverlap(ServerLevel level, BlockPos original) {
      if (!hasPedestalNearby(level, original, PEDESTAL_CLEARANCE)) return original;

      for (int radius = 1; radius <= 6; radius++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
               BlockPos.MutableBlockPos candidate = original.mutable().move(dx, 0, dz);

               for (int i = 0; i < 4; i++) {
                  if (!level.getBlockState(candidate.below()).isAir()) break;
                  candidate.move(0, -1, 0);
               }
               BlockPos candidatePos = candidate.immutable();
               if (!hasPedestalNearby(level, candidatePos, PEDESTAL_CLEARANCE)) {
                  return candidatePos;
               }
            }
         }
      }
      return original;
   }

   private static boolean hasPedestalNearby(ServerLevel level, BlockPos centre, int clearance) {
      BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
      for (int dx = -clearance; dx <= clearance; dx++) {
         for (int dy = -1; dy <= 2; dy++) {
            for (int dz = -clearance; dz <= clearance; dz++) {
               check.setWithOffset(centre, dx, dy, dz);
               if (level.getBlockState(check).getBlock()
                     instanceof com.neutrinodust.useful_ores.pedestal.AncientPedestalBlock) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   private void openPortal(ServerLevel level, BlockPos pos) {
      if (targetPedestal == null) return;
      if (!level.getBlockState(pos).isAir() && !level.getBlockState(pos).canBeReplaced()) {

         pos = pos.above();
      }
      level.setBlockAndUpdate(pos, com.neutrinodust.useful_ores.init.ModItems.DARK_WORMHOLE_PORTAL.get().defaultBlockState());
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof DarkWormholePortalBlockEntity portalBE) {
         portalBE.setTargetPedestal(targetPedestal);
      }
   }

   @Override
   protected void onHitEntity(EntityHitResult result) {
      landAt(result.getEntity().blockPosition());
   }

   @Override
   protected void onHitBlock(BlockHitResult result) {
      land(result);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {

   }
}

