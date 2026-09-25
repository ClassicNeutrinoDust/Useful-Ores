package com.neutrinodust.useful_ores.entity;

import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.particle.ColoredFlameOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MeteoriteProjectile extends Projectile {

   private static final int LAND_BURN_TICKS = 30;
   private static final int MAX_LIFETIME_TICKS = 200;
   private static final float EXPLOSION_POWER = 6.5F;
   private static final double BLAST_DAMAGE_RADIUS = 8.0;
   private static final float DIRECT_HIT_DAMAGE = 80.0F;
   private static final int FIRE_SECONDS = 5;
   private static final int TRAIL_COLOR = 0xFF4D00;
   private static final double CRATER_RADIUS = 4.5;

   private int ticksAlive = 0;
   private boolean landed = false;
   private int landedTicks = 0;

   public MeteoriteProjectile(EntityType<? extends MeteoriteProjectile> type, Level level) {
      super(type, level);
   }

   public MeteoriteProjectile(Level level, LivingEntity shooter, EntityType<? extends MeteoriteProjectile> type) {
      super(type, level);
      setOwner(shooter);
      setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
   }

   @Override
   public void tick() {
      super.tick();
      ticksAlive++;

      if (level().isClientSide() && ticksAlive % 2 == 0) {
         double jitter = 0.08;
         double ox = (random.nextDouble() - 0.5) * jitter;
         double oy = (random.nextDouble() - 0.5) * jitter;
         double oz = (random.nextDouble() - 0.5) * jitter;
         level().addParticle(new ColoredFlameOptions(TRAIL_COLOR, 0.4F),
            getX() + ox, getY() + oy, getZ() + oz, 0.0D, 0.0D, 0.0D);
      }

      if (landed) {
         landedTicks++;
         if (!level().isClientSide() && landedTicks >= LAND_BURN_TICKS) {
            explode();
         }
         return;
      }

      Vec3 velocity = getDeltaMovement();
      Vec3 startPos = position();
      Vec3 endPos = startPos.add(velocity);

      BlockHitResult blockHit = level().clip(new ClipContext(
         startPos, endPos,
         ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
      if (blockHit.getType() != HitResult.Type.MISS) {
         land(blockHit.getLocation());
         return;
      }

      setPos(endPos.x, endPos.y, endPos.z);
      setDeltaMovement(velocity.scale(0.99));

      AABB searchBox = getBoundingBox().expandTowards(velocity).inflate(1.0);
      for (Entity entity : level().getEntities(this, searchBox)) {
         if (entity == getOwner()) continue;
         if (entity instanceof LivingEntity) {
            explode();
            return;
         }
      }

      if (ticksAlive > MAX_LIFETIME_TICKS) {
         explode();
      }
   }

   private void land(Vec3 hitLocation) {
      setPos(hitLocation.x, hitLocation.y, hitLocation.z);
      setDeltaMovement(Vec3.ZERO);
      landed = true;
      landedTicks = 0;
   }

   private void explode() {
      if (level().isClientSide()) return;

      Level level = level();
      Vec3 impactPos = position();

      level.explode(this, impactPos.x, impactPos.y, impactPos.z, EXPLOSION_POWER,
         true, Level.ExplosionInteraction.MOB);

      for (Entity entity : level.getEntities(this,
            new AABB(impactPos, impactPos).inflate(BLAST_DAMAGE_RADIUS))) {
         if (!(entity instanceof LivingEntity living)) continue;

         double distance = entity.distanceTo(this);
         if (distance > BLAST_DAMAGE_RADIUS) continue;

         double falloff = 1.0 - (distance / BLAST_DAMAGE_RADIUS);
         float damage = (float) (DIRECT_HIT_DAMAGE * falloff * falloff);

         DamageSource source = level.damageSources().explosion(this,
            getOwner() instanceof LivingEntity le ? le : null);
         living.hurt(source, damage);
         living.igniteForSeconds(FIRE_SECONDS);
      }

      carveCrater(level, BlockPos.containing(impactPos));

      discard();
   }

   private void carveCrater(Level level, BlockPos center) {
      RandomSource random = level.getRandom();

      BlockState scorchedStone = ModItems.SCORCHED_STONE.get().defaultBlockState();
      BlockState scorchedDirt = ModItems.SCORCHED_DIRT.get().defaultBlockState();
      BlockState ash = ModItems.METEORITE_ASH_BLOCK.get().defaultBlockState();

      int reach = (int) Math.ceil(CRATER_RADIUS);
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

      for (int dx = -reach; dx <= reach; dx++) {
         for (int dy = -reach; dy <= reach; dy++) {
            for (int dz = -reach; dz <= reach; dz++) {
               double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
               if (dist > CRATER_RADIUS) continue;

               pos.setWithOffset(center, dx, dy, dz);
               BlockState state = level.getBlockState(pos);
               if (state.isAir()) continue;

               double edgeFactor = dist / CRATER_RADIUS;
               if (edgeFactor > 0.7 && random.nextDouble() < (edgeFactor - 0.7) * 3.0) continue;

               FluidState fluid = state.getFluidState();
               if (!fluid.isEmpty()) {

                  continue;
               }

               if (isWoodLike(state)) {
                  level.setBlockAndUpdate(pos, random.nextFloat() < 0.6F ? Blocks.AIR.defaultBlockState() : ash);
               } else if (isSoftGround(state)) {
                  level.setBlockAndUpdate(pos, scorchedDirt);
               } else if (isStoneLike(state)) {
                  level.setBlockAndUpdate(pos, scorchedStone);
               }

            }
         }
      }
   }

   private static boolean isWoodLike(BlockState state) {
      return state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) || state.is(BlockTags.LEAVES)
         || state.is(BlockTags.WOODEN_FENCES) || state.is(BlockTags.WOODEN_DOORS)
         || state.is(BlockTags.WOODEN_SLABS) || state.is(BlockTags.WOODEN_STAIRS);
   }

   private static boolean isSoftGround(BlockState state) {
      return state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)
         || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.MUD);
   }

   private static boolean isStoneLike(BlockState state) {
      return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.STONE_ORE_REPLACEABLES)
         || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES) || state.is(BlockTags.MINEABLE_WITH_PICKAXE);
   }

   @Override
   protected void onHitEntity(EntityHitResult result) {
      explode();
   }

   @Override
   protected void onHitBlock(BlockHitResult result) {
      if (!landed) {
         land(result.getLocation());
      }
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {

   }
}

