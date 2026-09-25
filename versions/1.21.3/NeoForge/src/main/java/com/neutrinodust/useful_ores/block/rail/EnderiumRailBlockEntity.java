package com.neutrinodust.useful_ores.block.rail;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class EnderiumRailBlockEntity extends BlockEntity {

   private static final long COOLDOWN_TICKS = 20;

   private record ArrivalCooldown(BlockPos railPos, long expiresAt) {}
   private static final Map<UUID, ArrivalCooldown> RECENTLY_TELEPORTED = new WeakHashMap<>();

   private @Nullable BlockPos linkedPos;

   private @Nullable Direction exitDirection;

   public EnderiumRailBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.ENDERIUM_RAIL.get(), pos, state);
   }

   public Direction getExitDirection(BlockState state) {
      Direction[] options = EnderiumRailBlock.axisDirections(state);
      if (this.exitDirection == null || (this.exitDirection != options[0] && this.exitDirection != options[1])) {
         this.exitDirection = options[0];
      }
      return this.exitDirection;
   }

   public Direction cycleExitDirection(BlockState state) {
      Direction[] options = EnderiumRailBlock.axisDirections(state);
      Direction current = getExitDirection(state);
      this.exitDirection = (current == options[0]) ? options[1] : options[0];
      this.setChanged();
      return this.exitDirection;
   }

   public @Nullable Direction getRawExitDirection() {
      return this.exitDirection;
   }

   public @Nullable GlobalPos getLinkedPos() {
      if (this.linkedPos == null || this.level == null) return null;
      return GlobalPos.of(this.level.dimension(), this.linkedPos);
   }

   public void setLinkedPos(BlockPos pos) {
      this.linkedPos = pos.immutable();
      this.setChanged();
   }

   public void clearLinkedPos() {
      this.linkedPos = null;
      this.setChanged();
   }

   public boolean isLinked() {
      return this.linkedPos != null;
   }

   public static boolean isOnCooldown(UUID entityId, BlockPos railPos, long gameTime) {
      ArrivalCooldown cooldown = RECENTLY_TELEPORTED.get(entityId);
      if (cooldown == null || gameTime >= cooldown.expiresAt()) {
         RECENTLY_TELEPORTED.remove(entityId);
         return false;
      }
      return cooldown.railPos().equals(railPos);
   }

   public static boolean hasRecentTeleport(UUID entityId, long gameTime) {
      ArrivalCooldown cooldown = RECENTLY_TELEPORTED.get(entityId);
      if (cooldown == null || gameTime >= cooldown.expiresAt()) {
         RECENTLY_TELEPORTED.remove(entityId);
         return false;
      }
      return true;
   }

   public static void markTeleported(UUID entityId, BlockPos arrivalRailPos, long gameTime) {
      RECENTLY_TELEPORTED.put(entityId, new ArrivalCooldown(arrivalRailPos.immutable(), gameTime + COOLDOWN_TICKS));
   }

   @Override
   protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
      super.loadAdditional(input, registries);
      this.linkedPos = NbtCompat.readCodec(input, "linked_pos", BlockPos.CODEC).orElse(null);
      this.exitDirection = NbtCompat.readCodec(input, "exit_direction", Direction.CODEC).orElse(null);
   }

   @Override
   protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
      super.saveAdditional(output, registries);
      NbtCompat.storeNullable(output, "linked_pos", BlockPos.CODEC, this.linkedPos);
      NbtCompat.storeNullable(output, "exit_direction", Direction.CODEC, this.exitDirection);
   }
}

