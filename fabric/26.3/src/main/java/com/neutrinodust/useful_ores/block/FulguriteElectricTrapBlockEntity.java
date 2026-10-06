package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class FulguriteElectricTrapBlockEntity extends BlockEntity implements com.geckolib.animatable.GeoAnimatable {

   public static final int MAX_RANGE = 3;
   private static final int FIELD_CHECK_INTERVAL = 5;
   private static final double MOB_DAMAGE = 4.0;
   private static final double PLAYER_DAMAGE = 2.0;

   private static final int LIGHTNING_CHANCE_DENOMINATOR = 900;

   private boolean lightningUnlocked = false;
   private boolean redstoneSuppressed = false;
   private int range = 1;
   private int fieldTickCounter = 0;

   public FulguriteElectricTrapBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.FULGURITE_ELECTRIC_TRAP, pos, state);
   }

   public boolean isLightningUnlocked() { return this.lightningUnlocked; }
   public int getRange() { return this.range; }

   public void setLightningUnlocked(boolean unlocked) {
      this.lightningUnlocked = unlocked;
      this.setChanged();
      if (this.level != null) refreshActiveState();
   }

   public void setRange(int range) {
      this.range = Math.max(1, Math.min(MAX_RANGE, range));
      this.setChanged();
   }

   public boolean isFieldActive() {
      return this.lightningUnlocked && !this.redstoneSuppressed;
   }

   private void refreshActiveState() {
      if (!(this.level instanceof ServerLevel serverLevel)) return;
      BlockState state = this.getBlockState();
      if (!state.hasProperty(FulguriteElectricTrapBlock.ACTIVE)) return;
      boolean shouldBeActive = isFieldActive();
      if (state.getValue(FulguriteElectricTrapBlock.ACTIVE) != shouldBeActive) {
         serverLevel.setBlockAndUpdate(this.worldPosition, state.setValue(FulguriteElectricTrapBlock.ACTIVE, shouldBeActive));
      }
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, FulguriteElectricTrapBlockEntity trap) {
      if (!(level instanceof ServerLevel serverLevel)) return;

      boolean powered = level.hasNeighborSignal(pos);
      if (powered != trap.redstoneSuppressed) {
         trap.redstoneSuppressed = powered;
         trap.refreshActiveState();
      }

      if (!trap.lightningUnlocked) {
         trap.tryGetStruckByLightning(serverLevel, pos);
         return;
      }

      if (!trap.isFieldActive()) {
         trap.clearRippleRings(serverLevel);
         return;
      }

      trap.tickRippleRings(serverLevel, pos);

      if (++trap.fieldTickCounter < FIELD_CHECK_INTERVAL) return;
      trap.fieldTickCounter = 0;
      trap.zapField(serverLevel, pos, state);
   }

   private void tryGetStruckByLightning(ServerLevel level, BlockPos pos) {
      if (!level.isThundering()) return;
      BlockPos above = pos.above();
      if (!level.canSeeSky(above)) return;
      if (level.getRandom().nextInt(LIGHTNING_CHANCE_DENOMINATOR) != 0) return;

      LightningBolt bolt = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
      Vec3 strikeVec = Vec3.atBottomCenterOf(above);
      bolt.setPos(strikeVec.x, strikeVec.y, strikeVec.z);
      bolt.setVisualOnly(true);
      level.addFreshEntity(bolt);

      this.lightningUnlocked = true;
      this.setChanged();
      refreshActiveState();
      level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.6F);
      for (int i = 0; i < 40; i++) {
         double dx = (level.getRandom().nextDouble() - 0.5) * 1.5;
         double dz = (level.getRandom().nextDouble() - 0.5) * 1.5;
         level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5 + dx, pos.getY() + 0.6, pos.getZ() + 0.5 + dz,
            1, 0, 0.1, 0, 0.02);
      }
   }

   private static final double PULL_STRENGTH = 0.18;

   private void zapField(ServerLevel level, BlockPos pos, BlockState state) {

      DamageSource lightningDamage = level.damageSources().lightningBolt();
      double centerX = pos.getX() + 0.5;
      double centerY = pos.getY() + 0.6;
      double centerZ = pos.getZ() + 0.5;

      AABB fieldBox = new AABB(
         pos.getX() - this.range, pos.getY(), pos.getZ() - this.range,
         pos.getX() + this.range + 1, pos.getY() + 1.5, pos.getZ() + this.range + 1);
      List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, fieldBox);
      for (LivingEntity victim : victims) {
         if (victim instanceof Player player) {

            player.hurt(lightningDamage, (float) PLAYER_DAMAGE);
         } else {

            victim.hurt(lightningDamage, (float) MOB_DAMAGE);
            victim.setDeltaMovement(0, victim.getDeltaMovement().y, 0);
         }

         double toCenterX = centerX - victim.getX();
         double toCenterZ = centerZ - victim.getZ();
         double dist = Math.sqrt(toCenterX * toCenterX + toCenterZ * toCenterZ);
         if (dist > 0.15) {
            double pull = PULL_STRENGTH * Mth.clamp(dist / Math.max(1, this.range), 0.3, 1.0);
            Vec3 motion = victim.getDeltaMovement();
            victim.setDeltaMovement(
               motion.x + (toCenterX / dist) * pull,
               motion.y,
               motion.z + (toCenterZ / dist) * pull);
            victim.syncVelocity = true;
         }

         level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
            victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(),
            6, 0.2, 0.3, 0.2, 0.01);
      }

      for (int dx = -this.range; dx <= this.range; dx++) {
         for (int dz = -this.range; dz <= this.range; dz++) {
            double tileX = pos.getX() + dx + 0.5;
            double tileZ = pos.getZ() + dz + 0.5;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
               tileX, pos.getY() + 0.1, tileZ, 2, 0.35, 0.05, 0.35, 0.01);
         }
      }

      var random = level.getRandom();
      for (int i = 0; i < 2; i++) {
         double px = centerX + (random.nextDouble() - 0.5) * 0.6;
         double py = centerY + 0.4 + random.nextDouble() * 0.5;
         double pz = centerZ + (random.nextDouble() - 0.5) * 0.6;
         level.sendParticles(new DustParticleOptions(LIGHTNING_WHITE_CORE, 0.7F), px, py, pz, 1, 0.0, 0.02, 0.0, 0.0);
      }
   }

   private final List<UUID> rippleRingIds = new java.util.ArrayList<>();

   private static final int RIPPLE_COUNT = 3;

   private static final int RIPPLE_PERIOD_TICKS = 80;

   private static final int RIPPLE_INTERPOLATION_TICKS = 3;

   private int rippleTick = 0;

   private void tickRippleRings(ServerLevel level, BlockPos pos) {
      double centerX = pos.getX() + 0.5;
      double centerZ = pos.getZ() + 0.5;
      double groundY = pos.getY() + 0.02;

      ensureRippleRingsExist(level, pos, groundY, centerX, centerZ);

      this.rippleTick++;
      int phaseStep = RIPPLE_PERIOD_TICKS / RIPPLE_COUNT;

      for (int i = 0; i < this.rippleRingIds.size(); i++) {
         UUID id = this.rippleRingIds.get(i);
         if (!(level.getEntity(id) instanceof Display.ItemDisplay display)) continue;

         int phaseTick = (this.rippleTick + i * phaseStep) % RIPPLE_PERIOD_TICKS;
         double progress = phaseTick / (double) RIPPLE_PERIOD_TICKS;

         double growPortion = 0.90;
         double radius;
         if (progress < growPortion) {
            radius = (progress / growPortion) * this.range;
         } else {
            double shrink = (progress - growPortion) / (1.0 - growPortion);
            radius = (1.0 - shrink) * this.range;
         }

         updateRingTransform(display, radius);
      }
   }

   private void ensureRippleRingsExist(ServerLevel level, BlockPos pos, double groundY, double centerX, double centerZ) {

      this.rippleRingIds.removeIf(id -> level.getEntity(id) == null);
      sweepOrphanRings(level, pos);

      while (this.rippleRingIds.size() < RIPPLE_COUNT) {
         Display.ItemDisplay display = new Display.ItemDisplay(EntityTypes.ITEM_DISPLAY, level);

         display.setPos(centerX, groundY, centerZ);
         display.setNoGravity(true);
         display.setPermanentlyInvulnerable(true);
         display.setGlowingTag(false);

         invokeSetterByParamType(display, ItemStack.class, new ItemStack(com.neutrinodust.useful_ores.init.ModItems.ELECTRIC_RING.get()));

         invokeSetterByParamType(display, net.minecraft.world.item.ItemDisplayContext.class, net.minecraft.world.item.ItemDisplayContext.FIXED);

         invokeIntSetterByName(display, "setTransformationInterpolationDuration", RIPPLE_INTERPOLATION_TICKS);
         invokeIntSetterByName(display, "setInterpolationDelay", 0);

         updateRingTransform(display, 0.0);

         level.addFreshEntity(display);
         this.rippleRingIds.add(display.getUUID());
      }
   }

   private void updateRingTransform(Display.ItemDisplay display, double radius) {

      invokeIntSetterByName(display, "setInterpolationDelay", 0);

      float scale = (float) Math.max(radius * 2.0, 0.0001);

      Transformation transform = new Transformation(
         new Vector3f(0f, 0f, 0f),
         new Quaternionf().rotateX((float) Math.toRadians(90)),
         new Vector3f(scale, scale, 1f),
         new Quaternionf()
      );
      invokeSetterByParamType(display, Transformation.class, transform);
   }

   private void clearRippleRings(ServerLevel level) {
      for (UUID id : this.rippleRingIds) {
         if (level.getEntity(id) != null) {
            level.getEntity(id).discard();
         }
      }
      this.rippleRingIds.clear();
      this.rippleTick = 0;
   }

   private static void invokeIntSetterByName(Object target, String methodName, int value) {
      Class<?> cls = target.getClass();
      while (cls != null) {
         try {
            java.lang.reflect.Method m = cls.getDeclaredMethod(methodName, int.class);
            m.setAccessible(true);
            m.invoke(target, value);
            return;
         } catch (NoSuchMethodException ignored) {
            cls = cls.getSuperclass();
         } catch (ReflectiveOperationException ignored) {
            return;
         }
      }
   }

   private void sweepOrphanRings(ServerLevel level, BlockPos pos) {
      AABB area = new AABB(pos).inflate(MAX_RANGE + 2, 2, MAX_RANGE + 2);
      for (Display.ItemDisplay display : level.getEntitiesOfClass(Display.ItemDisplay.class, area)) {
         if (this.rippleRingIds.contains(display.getUUID())) continue;
         if (!(invokeGetterByReturnType(display, ItemStack.class) instanceof ItemStack stack)) continue;
         if (stack.getItem() != com.neutrinodust.useful_ores.init.ModItems.ELECTRIC_RING.get()) continue;

         // Ring displays are spawned exactly at the trap center. Only remove an
         // untracked ring if it belongs to this trap's position; this prevents
         // neighboring traps from deleting each other's displays.
         BlockPos ringPos = BlockPos.containing(display.getX(), display.getY(), display.getZ());
         if (ringPos.equals(pos)) {
            display.discard();
         }
      }
   }

   private static Object invokeGetterByReturnType(Object target, Class<?> returnType) {
      Class<?> cls = target.getClass();
      while (cls != null) {
         for (java.lang.reflect.Method m : cls.getDeclaredMethods()) {
            if (m.getParameterCount() == 0 && returnType.isAssignableFrom(m.getReturnType())) {
               try {
                  m.setAccessible(true);
                  return m.invoke(target);
               } catch (ReflectiveOperationException ignored) {

               }
            }
         }
         cls = cls.getSuperclass();
      }
      return null;
   }

   private static void invokeSetterByParamType(Object target, Class<?> paramType, Object value) {
      Class<?> cls = target.getClass();
      while (cls != null) {
         for (java.lang.reflect.Method m : cls.getDeclaredMethods()) {
            if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == paramType) {
               try {
                  m.setAccessible(true);
                  m.invoke(target, value);
                  return;
               } catch (ReflectiveOperationException ignored) {

               }
            }
         }
         cls = cls.getSuperclass();
      }
      throw new IllegalStateException("No setter found for parameter type " + paramType + " on " + target.getClass());
   }

   private static final int LIGHTNING_WHITE_CORE = 0xFFFFEE;

   @Override
   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);
      output.putBoolean("LightningUnlocked", this.lightningUnlocked);
      output.putInt("Range", this.range);
   }

   @Override
   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);
      this.lightningUnlocked = input.getBooleanOr("LightningUnlocked", false);
      this.range = Math.max(1, Math.min(MAX_RANGE, input.getIntOr("Range", 1)));
   }

   @Override
   public void setRemoved() {
      super.setRemoved();
      if (this.level instanceof ServerLevel serverLevel) {
         clearRippleRings(serverLevel);
      }
   }

   private final com.geckolib.animatable.instance.AnimatableInstanceCache geoCache =
      com.geckolib.util.GeckoLibUtil.createInstanceCache(this);

   @Override
   public com.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
      return geoCache;
   }

   @Override
   public void registerControllers(com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar controllers) {

   }
}

