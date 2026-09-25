package com.neutrinodust.useful_ores.barrier;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class NyxiumDarkBarrierBlockEntity extends BlockEntity implements com.geckolib.animatable.GeoAnimatable {

   private static final int SCAN_INTERVAL_TICKS = 1;
   private static final int CONVERSION_DELAY_TICKS = 60;
   private static final int MAX_PENDING_JOBS = 64;
   private static final int EJECT_GRACE_TICKS = 100;
   private static final int MAX_SAVED_EJECTED_ENTITIES = 256;

   private static final double TRIGGER_CENTER_Y_OFFSET = 1.0;
   private static final double TRIGGER_WIDTH = 2.2;
   private static final double TRIGGER_HEIGHT = 1.3;
   private static final double TRIGGER_DEPTH = 0.5;
   private static final double CAPTURE_BROADPHASE_MARGIN = 2.0;

   private static final double EJECT_DISTANCE = 0.68;
   private static final double EJECT_VERTICAL_SPEED = 0.20;
   private static final double EJECT_HORIZONTAL_SPEED_MIN = 0.35;
   private static final double EJECT_HORIZONTAL_SPEED_MAX = 0.55;

   private record PendingConversion(List<ItemStack> results, long readyAtGameTime) {}

   private final List<PendingConversion> pending = new ArrayList<>();
   private final Map<UUID, Long> recentlyEjected = new HashMap<>();
   private final Map<net.minecraft.world.item.Item, ItemReversion.Fraction> materialResidue = new HashMap<>();

   public NyxiumDarkBarrierBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.NYXIUM_DARK_BARRIER.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, NyxiumDarkBarrierBlockEntity be) {
      if (!(level instanceof ServerLevel serverLevel)) return;

      long now = serverLevel.getGameTime();
      be.recentlyEjected.entrySet().removeIf(entry -> now >= entry.getValue());

      if (now % SCAN_INTERVAL_TICKS == 0 && be.pending.size() < MAX_PENDING_JOBS) {
         scanAndQueue(serverLevel, pos, state, be);
      }

      releaseReadyConversions(serverLevel, pos, be, now);
   }

   private static void scanAndQueue(ServerLevel level, BlockPos pos, BlockState state,
                                    NyxiumDarkBarrierBlockEntity be) {
      AABB triggerBox = buildTriggerBox(pos, state);
      
      
      
      
      
      AABB captureBox = triggerBox.inflate(CAPTURE_BROADPHASE_MARGIN);
      for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, captureBox)) {
         if (be.pending.size() >= MAX_PENDING_JOBS) break;
         if (!itemEntity.isAlive()) continue;
         if (be.recentlyEjected.containsKey(itemEntity.getUUID())) continue;

         ItemStack current = itemEntity.getItem();
         if (current.isEmpty()) continue;
         if (!sweptBoundsIntersects(itemEntity, triggerBox)) continue;

         ItemReversion.ReversionResult reversion =
            ItemReversion.revert(current, level, be.materialResidue);
         if (reversion.outputs().isEmpty() && reversion.residue().equals(be.materialResidue)) continue;

         
         be.materialResidue.clear();
         be.materialResidue.putAll(reversion.residue());

         
         if (reversion.outputs().size() == 1
               && reversion.outputs().get(0).getItem() == current.getItem()
               && reversion.outputs().get(0).getCount() == current.getCount()) {
            continue;
         }

         
         
         itemEntity.discard();

         if (!reversion.outputs().isEmpty()) {
            long readyAt = level.getGameTime() + CONVERSION_DELAY_TICKS;
            be.pending.add(new PendingConversion(copyStacks(reversion.outputs()), readyAt));
         }
         be.setChanged();

         level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT,
            SoundSource.BLOCKS, 0.4F, 0.6F);
         level.sendParticles(ParticleTypes.REVERSE_PORTAL,
            itemEntity.getX(), itemEntity.getY() + 0.2, itemEntity.getZ(),
            12, 0.25, 0.25, 0.25, 0.03);
      }
   }

   private static boolean sweptBoundsIntersects(ItemEntity entity, AABB target) {
      AABB current = entity.getBoundingBox();
      var motion = entity.getDeltaMovement();

      
      
      
      double minX = Math.min(current.minX - motion.x, current.minX + motion.x);
      double minY = Math.min(current.minY - motion.y, current.minY + motion.y);
      double minZ = Math.min(current.minZ - motion.z, current.minZ + motion.z);
      double maxX = Math.max(current.maxX - motion.x, current.maxX + motion.x);
      double maxY = Math.max(current.maxY - motion.y, current.maxY + motion.y);
      double maxZ = Math.max(current.maxZ - motion.z, current.maxZ + motion.z);

      return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(0.04).intersects(target);
   }

   private static List<ItemStack> copyStacks(List<ItemStack> input) {
      List<ItemStack> copy = new ArrayList<>(input.size());
      for (ItemStack stack : input) {
         if (!stack.isEmpty()) copy.add(stack.copy());
      }
      return copy;
   }

   private static void releaseReadyConversions(ServerLevel level, BlockPos pos,
                                               NyxiumDarkBarrierBlockEntity be, long now) {
      if (be.pending.isEmpty()) return;

      Iterator<PendingConversion> it = be.pending.iterator();
      boolean releasedAny = false;
      while (it.hasNext()) {
         PendingConversion conversion = it.next();
         if (now < conversion.readyAtGameTime()) continue;

         it.remove();
         releasedAny = true;
         spit(level, pos, be, conversion.results());
      }
      if (releasedAny) be.setChanged();
   }

   private static AABB buildTriggerBox(BlockPos pos, BlockState state) {
      double cx = pos.getX() + 0.5;
      double cy = pos.getY() + TRIGGER_CENTER_Y_OFFSET;
      double cz = pos.getZ() + 0.5;

      Direction facing = state.hasProperty(NyxiumDarkBarrierBlock.FACING)
         ? state.getValue(NyxiumDarkBarrierBlock.FACING)
         : Direction.NORTH;

      double halfWidth = TRIGGER_WIDTH / 2.0;
      double halfHeight = TRIGGER_HEIGHT / 2.0;
      double halfDepth = TRIGGER_DEPTH / 2.0;

      double halfX = facing.getAxis() == Direction.Axis.Z ? halfWidth : halfDepth;
      double halfZ = facing.getAxis() == Direction.Axis.X ? halfWidth : halfDepth;

      return new AABB(cx - halfX, cy - halfHeight, cz - halfZ,
         cx + halfX, cy + halfHeight, cz + halfZ);
   }

   



   private static void spit(ServerLevel level, BlockPos pos, NyxiumDarkBarrierBlockEntity be,
                            List<ItemStack> stacks) {
      BlockState state = be.getBlockState();
      Direction facing = state.hasProperty(NyxiumDarkBarrierBlock.FACING)
         ? state.getValue(NyxiumDarkBarrierBlock.FACING)
         : Direction.NORTH;

      double x = pos.getX() + 0.5 + facing.getStepX() * EJECT_DISTANCE;
      double y = pos.getY() + TRIGGER_CENTER_Y_OFFSET;
      double z = pos.getZ() + 0.5 + facing.getStepZ() * EJECT_DISTANCE;
      RandomSource random = level.getRandom();
      long graceExpiry = level.getGameTime() + EJECT_GRACE_TICKS;

      for (ItemStack stack : stacks) {
         ItemStack remaining = stack.copy();
         int maxStackSize = Math.max(1, remaining.getMaxStackSize());

         while (!remaining.isEmpty()) {
            int amount = Math.min(maxStackSize, remaining.getCount());
            ItemStack piece = remaining.copyWithCount(amount);
            remaining.shrink(amount);

            ItemEntity out = new ItemEntity(level, x, y, z, piece);
            double lateralAngle = (random.nextDouble() - 0.5) * Math.PI;
            double horizontalSpeed = EJECT_HORIZONTAL_SPEED_MIN
               + random.nextDouble() * (EJECT_HORIZONTAL_SPEED_MAX - EJECT_HORIZONTAL_SPEED_MIN);
            double forward = horizontalSpeed * 0.9;
            double lateral = horizontalSpeed * 0.35 * Math.sin(lateralAngle);

            double vx = facing.getStepX() * forward + (facing.getAxis() == Direction.Axis.Z ? lateral : 0.0);
            double vz = facing.getStepZ() * forward + (facing.getAxis() == Direction.Axis.X ? lateral : 0.0);
            out.setDeltaMovement(vx, EJECT_VERTICAL_SPEED + random.nextDouble() * 0.10, vz);
            out.setDefaultPickUpDelay();
            level.addFreshEntity(out);

            
            
            be.recentlyEjected.put(out.getUUID(), graceExpiry);
         }
      }

      level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT,
         SoundSource.BLOCKS, 0.4F, 1.7F);
      level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z,
         10, 0.2, 0.2, 0.2, 0.02);
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);

      List<ItemStack> flat = new ArrayList<>();
      output.putInt("PendingCount", pending.size());
      long now = level != null ? level.getGameTime() : 0L;
      for (int i = 0; i < pending.size(); i++) {
         PendingConversion conversion = pending.get(i);
         output.putInt("PendingDelay" + i, (int) Math.max(0, conversion.readyAtGameTime() - now));
         output.putInt("PendingSize" + i, conversion.results().size());
         flat.addAll(conversion.results());
      }

      NonNullList<ItemStack> items = NonNullList.withSize(flat.size(), ItemStack.EMPTY);
      for (int i = 0; i < flat.size(); i++) items.set(i, flat.get(i));
      ContainerHelper.saveAllItems(output, items);

      output.putInt("ResidueCount", materialResidue.size());
      int residueIndex = 0;
      for (Map.Entry<net.minecraft.world.item.Item, ItemReversion.Fraction> entry : materialResidue.entrySet()) {
         if (residueIndex >= 512) break;
         output.putString("ResidueItem" + residueIndex,
            BuiltInRegistries.ITEM.getKey(entry.getKey()).toString());
         output.putLong("ResidueNumerator" + residueIndex, entry.getValue().numerator());
         output.putLong("ResidueDenominator" + residueIndex, entry.getValue().denominator());
         residueIndex++;
      }

      output.putInt("EjectedCount", Math.min(MAX_SAVED_EJECTED_ENTITIES, recentlyEjected.size()));
      int ejectedIndex = 0;
      for (Map.Entry<UUID, Long> entry : recentlyEjected.entrySet()) {
         if (ejectedIndex >= MAX_SAVED_EJECTED_ENTITIES) break;
         output.putString("EjectedUuid" + ejectedIndex, entry.getKey().toString());
         output.putLong("EjectedExpiry" + ejectedIndex, entry.getValue());
         ejectedIndex++;
      }
   }

   @Override
   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);

      pending.clear();
      materialResidue.clear();
      recentlyEjected.clear();

      int count = input.getIntOr("PendingCount", 0);
      int[] sizes = new int[Math.max(0, Math.min(count, MAX_PENDING_JOBS))];
      int total = 0;
      for (int i = 0; i < sizes.length; i++) {
         sizes[i] = Math.max(0, input.getIntOr("PendingSize" + i, 0));
         total += sizes[i];
      }

      NonNullList<ItemStack> items = NonNullList.withSize(total, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(input, items);

      long now = level != null ? level.getGameTime() : 0L;
      int cursor = 0;
      for (int i = 0; i < sizes.length; i++) {
         int delay = Math.max(0, input.getIntOr("PendingDelay" + i, 0));
         List<ItemStack> group = new ArrayList<>(sizes[i]);
         for (int j = 0; j < sizes[i] && cursor < items.size(); j++, cursor++) {
            ItemStack stack = items.get(cursor);
            if (!stack.isEmpty()) group.add(stack);
         }
         if (!group.isEmpty()) pending.add(new PendingConversion(group, now + delay));
      }

      int residueCount = Math.max(0, Math.min(input.getIntOr("ResidueCount", 0), 512));
      for (int i = 0; i < residueCount; i++) {
         String itemName = input.getStringOr("ResidueItem" + i, "");
         long numerator = input.getLongOr("ResidueNumerator" + i, 0L);
         long denominator = input.getLongOr("ResidueDenominator" + i, 1L);
         if (itemName.isEmpty() || numerator <= 0 || denominator <= 0) continue;

         try {
            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.parse(itemName);
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(id);
            if (item != null && item != net.minecraft.world.item.Items.AIR) {
               materialResidue.put(item, new ItemReversion.Fraction(numerator, denominator));
            }
         } catch (Exception ignored) {
            
         }
      }

      int ejectedCount = Math.max(0, Math.min(input.getIntOr("EjectedCount", 0), MAX_SAVED_EJECTED_ENTITIES));
      for (int i = 0; i < ejectedCount; i++) {
         String uuidText = input.getStringOr("EjectedUuid" + i, "");
         long expiry = input.getLongOr("EjectedExpiry" + i, 0L);
         if (uuidText.isEmpty() || expiry <= now) continue;
         try {
            recentlyEjected.put(UUID.fromString(uuidText), expiry);
         } catch (IllegalArgumentException ignored) {
            
         }
      }
   }

   @Override
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
      return this.saveCustomOnly(registries);
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
