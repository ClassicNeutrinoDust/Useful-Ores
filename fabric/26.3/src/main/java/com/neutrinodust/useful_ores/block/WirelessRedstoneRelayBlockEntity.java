package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WirelessRedstoneRelayBlockEntity extends BlockEntity {

   public enum LinkMode {
      SINGLE, MULTI, CONNECTION;

      public LinkMode next() {
         return switch (this) {
            case SINGLE -> MULTI;
            case MULTI -> CONNECTION;
            case CONNECTION -> SINGLE;
         };
      }
   }

   private @Nullable BlockPos linkedPos;

   private LinkMode mode = LinkMode.SINGLE;
   private final List<BlockPos> broadcastTargets = new ArrayList<>();

   public enum MeshCombine {

      OR,

      AND,

      LATEST;

      public MeshCombine next() {
         return switch (this) {
            case OR -> AND;
            case AND -> LATEST;
            case LATEST -> OR;
         };
      }
   }

   private final List<BlockPos> upstreamHubs = new ArrayList<>();
   private MeshCombine meshCombine = MeshCombine.OR;

   private boolean selfInputPowered = false;

   private boolean pendingLoadRun = false;

   public WirelessRedstoneRelayBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.WIRELESS_REDSTONE_RELAY, pos, state);
   }

   public LinkMode getMode() { return this.mode; }

   public @Nullable BlockPos getLinkedPos() { return this.linkedPos; }

   public List<BlockPos> getUpstreamHubs() { return this.upstreamHubs; }

   public MeshCombine getMeshCombine() { return this.meshCombine; }

   public void cycleMeshCombine(Level level) {
      if (level.isClientSide()) return;
      this.meshCombine = this.meshCombine.next();
      this.setChanged();
      if (level instanceof ServerLevel serverLevel) {
         mirrorCombineMode(serverLevel);
      }
      resync(level);
   }

   private void mirrorCombineMode(ServerLevel serverLevel) {
      WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
      GlobalPos here = GlobalPos.of(serverLevel.dimension(), this.worldPosition);
      WirelessRelayNetworkData.MeshCombine mode = switch (this.meshCombine) {
         case AND -> WirelessRelayNetworkData.MeshCombine.AND;
         case LATEST -> WirelessRelayNetworkData.MeshCombine.LATEST;
         case OR -> WirelessRelayNetworkData.MeshCombine.OR;
      };
      registry.setCombineMode(here, mode);
   }

   public List<BlockPos> getBroadcastTargets() { return this.broadcastTargets; }

   public void toggleMode(Level level) {
      if (level.isClientSide()) return;
      LinkMode next = this.mode.next();
      if (next == LinkMode.SINGLE) {
         WirelessRelayNetworkData registry = level instanceof ServerLevel serverLevel
               ? WirelessRelayNetworkData.get(serverLevel) : null;
         GlobalPos here = level instanceof ServerLevel serverLevel
               ? GlobalPos.of(serverLevel.dimension(), this.worldPosition) : null;

         for (BlockPos targetPos : List.copyOf(this.broadcastTargets)) {
            if (level.isLoaded(targetPos)
                  && level.getBlockEntity(targetPos) instanceof WirelessRedstoneRelayBlockEntity target) {

               target.upstreamHubs.remove(this.worldPosition);
               target.setChanged();
               target.resync(level);
            }
            if (registry != null && here != null && level instanceof ServerLevel serverLevel) {
               registry.removeEdge(here, GlobalPos.of(serverLevel.dimension(), targetPos));
            }
         }
         this.broadcastTargets.clear();
      }
      this.mode = next;
      this.setChanged();
      resync(level);
      updateForceLoad(level);
   }

   void addOrRemoveBroadcastTarget(Level level, BlockPos targetPos, WirelessRedstoneRelayBlockEntity target) {
      boolean removing = this.broadcastTargets.remove(targetPos);
      if (removing) {
         target.upstreamHubs.remove(this.worldPosition);
      } else {
         this.broadcastTargets.add(targetPos.immutable());
         if (!target.upstreamHubs.contains(this.worldPosition)) {
            target.upstreamHubs.add(this.worldPosition.immutable());
         }
      }
      this.setChanged();
      target.setChanged();

      if (level instanceof ServerLevel serverLevel) {
         GlobalPos parentGlobal = GlobalPos.of(serverLevel.dimension(), this.worldPosition);
         GlobalPos childGlobal = GlobalPos.of(serverLevel.dimension(), targetPos);
         WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
         if (removing) {
            registry.removeEdge(parentGlobal, childGlobal);
         } else {
            registry.addEdge(parentGlobal, childGlobal);
         }
      }

      resync(level);
      target.resync(level);
      updateForceLoad(level);
   }

   void setLinkedPosOnly(@Nullable BlockPos pos) {
      this.linkedPos = pos;
      this.setChanged();
   }

   private static final int FORCE_LOAD_CHUNK_RADIUS = 1;

   private boolean chunkForcedByUs = false;

   void updateForceLoad(Level level) {
      if (!(level instanceof ServerLevel serverLevel)) return;
      boolean alwaysOnAllowed = com.neutrinodust.useful_ores.init.ModConfig.WIRELESS_RELAY_ALWAYS_ON_ORIGINS.get();
      boolean shouldBeForced = alwaysOnAllowed && this.upstreamHubs.isEmpty() && !downstreamTargets().isEmpty();
      int centerChunkX = this.worldPosition.getX() >> 4;
      int centerChunkZ = this.worldPosition.getZ() >> 4;
      if (!shouldBeForced) {

         setForcedArea(serverLevel, centerChunkX, centerChunkZ, false);
         this.chunkForcedByUs = false;
      } else if (!this.chunkForcedByUs) {
         setForcedArea(serverLevel, centerChunkX, centerChunkZ, true);
         this.chunkForcedByUs = true;
      }
   }

   private void setForcedArea(ServerLevel serverLevel, int centerChunkX, int centerChunkZ, boolean forced) {
      for (int dx = -FORCE_LOAD_CHUNK_RADIUS; dx <= FORCE_LOAD_CHUNK_RADIUS; dx++) {
         for (int dz = -FORCE_LOAD_CHUNK_RADIUS; dz <= FORCE_LOAD_CHUNK_RADIUS; dz++) {
            serverLevel.setChunkForced(centerChunkX + dx, centerChunkZ + dz, forced);
         }
      }
   }

   void resync(Level level) {
      if (level == null || level.isClientSide()) return;
      applyOutputNow(level, this.worldPosition, this.getBlockState());
      updateConnectedState(level);
   }

   void updateConnectedState(Level level) {
      if (level == null || level.isClientSide()) return;
      boolean connected = this.linkedPos != null || !this.broadcastTargets.isEmpty() || !this.upstreamHubs.isEmpty();
      BlockState state = level.getBlockState(this.worldPosition);
      if (state.getBlock() instanceof WirelessRedstoneRelayBlock
            && state.getValue(WirelessRedstoneRelayBlock.CONNECTED) != connected) {
         level.setBlock(this.worldPosition, state.setValue(WirelessRedstoneRelayBlock.CONNECTED, connected),
               Block.UPDATE_CLIENTS);
      }
   }

   public void setLinkedPos(@Nullable BlockPos pos) {
      BlockPos oldLinked = this.linkedPos;
      this.linkedPos = pos == null ? null : pos.immutable();
      this.setChanged();

      if (this.level instanceof ServerLevel serverLevel && oldLinked != null && !oldLinked.equals(pos)
            && serverLevel.isLoaded(oldLinked)
            && serverLevel.getBlockEntity(oldLinked) instanceof WirelessRedstoneRelayBlockEntity oldPartner
            && this.worldPosition.equals(oldPartner.linkedPos)) {
         oldPartner.linkedPos = null;
         oldPartner.setChanged();
         oldPartner.updateConnectedState(serverLevel);
      }

      if (this.level != null && !this.level.isClientSide()) {
         applyOutputNow(this.level, this.worldPosition, this.getBlockState());
         updateConnectedState(this.level);
         if (pos != null && this.level.isLoaded(pos)
               && this.level.getBlockEntity(pos) instanceof WirelessRedstoneRelayBlockEntity partnerEntity) {
            partnerEntity.applyOutputNow(this.level, pos, this.level.getBlockState(pos));
            partnerEntity.updateConnectedState(this.level);
         }
         updateForceLoad(this.level);
      }
   }

   public boolean isSelfInputPowered() { return this.selfInputPowered; }

   @Override
   public void preRemoveSideEffects(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
      if (this.level instanceof ServerLevel serverLevel) {
         cleanupNetworkGraph(serverLevel);
      }
   }

   @Override
   public void setRemoved() {
      super.setRemoved();
   }

   void cleanupNetworkGraph(ServerLevel level) {

      if (this.chunkForcedByUs) {
         int centerChunkX = this.worldPosition.getX() >> 4;
         int centerChunkZ = this.worldPosition.getZ() >> 4;
         setForcedArea(level, centerChunkX, centerChunkZ, false);
         this.chunkForcedByUs = false;
      }
      GlobalPos here = GlobalPos.of(level.dimension(), this.worldPosition);
      WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(level);

      if (!this.upstreamHubs.isEmpty()) {
         for (BlockPos hubPos : List.copyOf(this.upstreamHubs)) {
            registry.removeEdge(GlobalPos.of(level.dimension(), hubPos), here);
            if (level.isLoaded(hubPos)
                  && level.getBlockEntity(hubPos) instanceof WirelessRedstoneRelayBlockEntity parent) {
               parent.broadcastTargets.remove(this.worldPosition);
               parent.setChanged();
               parent.updateConnectedState(level);
               parent.updateForceLoad(level);
            }
         }
      }
      for (BlockPos targetPos : this.broadcastTargets) {
         registry.removeEdge(here, GlobalPos.of(level.dimension(), targetPos));
         if (level.isLoaded(targetPos)
               && level.getBlockEntity(targetPos) instanceof WirelessRedstoneRelayBlockEntity child) {

            child.upstreamHubs.remove(this.worldPosition);
            child.setChanged();
            child.updateConnectedState(level);
            child.updateForceLoad(level);
         }
      }
      registry.forgetRelay(here);
   }

   public void onLoad() {
      if (this.level != null && !this.level.isClientSide()) {
         this.pendingLoadRun = true;
      }
   }

   private static void runPendingLoad(Level level, BlockPos pos, BlockState state, WirelessRedstoneRelayBlockEntity relay) {
      if (level instanceof ServerLevel loadServerLevel) {
         
         if (relay.linkedPos != null && loadServerLevel.isLoaded(relay.linkedPos)) {
            if (!(loadServerLevel.getBlockEntity(relay.linkedPos) instanceof WirelessRedstoneRelayBlockEntity partner)
                  || partner.linkedPos == null || !relay.worldPosition.equals(partner.linkedPos)) {
               relay.linkedPos = null;
               relay.setChanged();
            }
         }
         relay.mirrorCombineMode(loadServerLevel);
      }
      relay.onNeighborChanged(level, pos, state);
      relay.applyOutputNow(level, pos, state);
      relay.updateConnectedState(level);
      relay.updateForceLoad(level);

      if (relay.upstreamHubs.isEmpty() && level instanceof ServerLevel serverLevel) {
         WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
         GlobalPos here = GlobalPos.of(serverLevel.dimension(), pos);
         refreshLoaded(serverLevel, registry.reassert(here, relay.selfInputPowered));
      }
   }

   public void onChunkUnloaded() {
      if (this.level != null && !this.level.isClientSide() && this.level instanceof ServerLevel serverLevel) {
         WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
         GlobalPos here = GlobalPos.of(serverLevel.dimension(), this.worldPosition);
         if (this.upstreamHubs.isEmpty()) {

            refreshLoaded(serverLevel, registry.reassert(here, this.selfInputPowered));
         }

      }
   }

   private static final int SAFETY_NET_INTERVAL_TICKS = 20;
   private int safetyNetCountdown = SAFETY_NET_INTERVAL_TICKS;

   private List<BlockPos> downstreamTargets() {
      if (this.mode != LinkMode.SINGLE) return this.broadcastTargets;
      if (this.linkedPos != null) return List.of(this.linkedPos);
      return List.of();
   }

   public void onNeighborChanged(Level level, BlockPos pos, BlockState state) {
      if (level.isClientSide()) return;

      Direction inputFace = WirelessRedstoneRelayBlock.inputSide(state);
      BlockPos inputPos   = pos.relative(inputFace);
      int signal = level.getSignal(inputPos, inputFace);
      if (signal < 15) {
         BlockState inputState = level.getBlockState(inputPos);
         if (inputState.is(net.minecraft.world.level.block.Blocks.REDSTONE_WIRE)) {
            signal = Math.max(signal, inputState.getValue(
               net.minecraft.world.level.block.RedstoneWireBlock.POWER));
         }
      }
      boolean newInput = signal > 0;
      if (newInput != this.selfInputPowered) {
         this.selfInputPowered = newInput;
         this.setChanged();
      }
      publish(level);

      pushUpdateTo(level, this.worldPosition);
      for (BlockPos targetPos : downstreamTargets()) {
         pushUpdateTo(level, targetPos);
      }
   }

   private static void refreshLoaded(ServerLevel serverLevel, Set<GlobalPos> changed) {
      for (GlobalPos p : changed) {
         ServerLevel targetLevel = serverLevel.getServer().getLevel(p.dimension());
         if (targetLevel != null && targetLevel.isLoaded(p.pos())) {
            pushUpdateTo(targetLevel, p.pos());
         }
      }
   }

   static void pushUpdateTo(Level level, BlockPos targetPos) {
      if (!level.isLoaded(targetPos)) return;
      if (!(level.getBlockEntity(targetPos) instanceof WirelessRedstoneRelayBlockEntity targetEntity)) return;

      BlockState targetState = level.getBlockState(targetPos);
      if (!(targetState.getBlock() instanceof WirelessRedstoneRelayBlock)) return;

      boolean targetShouldBeOn = targetEntity.computeDesiredOutput(level);
      if (targetState.getValue(WirelessRedstoneRelayBlock.POWERED) != targetShouldBeOn) {
         level.setBlockAndUpdate(targetPos, targetState.setValue(WirelessRedstoneRelayBlock.POWERED, targetShouldBeOn));
      }
   }

   public void applyScheduledOutput(Level level, BlockPos pos, BlockState state) {
      applyOutputNow(level, pos, state);
   }

   private void applyOutputNow(Level level, BlockPos pos, BlockState state) {
      boolean shouldBePowered = computeDesiredOutput(level);
      if (state.getValue(WirelessRedstoneRelayBlock.POWERED) != shouldBePowered) {
         level.setBlockAndUpdate(pos, state.setValue(WirelessRedstoneRelayBlock.POWERED, shouldBePowered));
      }
      publish(level);
      for (BlockPos targetPos : downstreamTargets()) {
         pushUpdateTo(level, targetPos);
      }
   }

   private boolean computeDesiredOutput(Level level) {
      if (!(level instanceof ServerLevel serverLevel)) return this.selfInputPowered;
      WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
      if (this.mode == LinkMode.SINGLE && this.linkedPos != null) {
         return registry.get(GlobalPos.of(serverLevel.dimension(), this.linkedPos));
      }
      return registry.get(GlobalPos.of(serverLevel.dimension(), this.worldPosition));
   }

   private void publish(Level level) {
      if (!(level instanceof ServerLevel serverLevel)) return;
      WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
      GlobalPos here = GlobalPos.of(serverLevel.dimension(), this.worldPosition);

      if (this.upstreamHubs.isEmpty()) {

         refreshLoaded(serverLevel, registry.publishRoot(here, this.selfInputPowered));
      }

   }

   public static void safetyNetTick(Level level, BlockPos pos, BlockState state, WirelessRedstoneRelayBlockEntity relay) {
      if (level.isClientSide()) return;

      if (relay.pendingLoadRun) {
         relay.pendingLoadRun = false;
         runPendingLoad(level, pos, state, relay);
      }

      if (level instanceof ServerLevel serverLevelForGossip) {
         WirelessRelayGossipTicker.driveTick(serverLevelForGossip.getServer());
      }

      if (relay.upstreamHubs.isEmpty() && level instanceof ServerLevel pollServerLevel) {
         Direction inputFace = WirelessRedstoneRelayBlock.inputSide(state);
         BlockPos inputPos = pos.relative(inputFace);
         int signal = level.getSignal(inputPos, inputFace);
         if (signal < 15) {
            BlockState inputState = level.getBlockState(inputPos);
            if (inputState.is(net.minecraft.world.level.block.Blocks.REDSTONE_WIRE)) {
               signal = Math.max(signal, inputState.getValue(
                  net.minecraft.world.level.block.RedstoneWireBlock.POWER));
            }
         }
         boolean polledInput = signal > 0;
         if (polledInput != relay.selfInputPowered) {
            relay.selfInputPowered = polledInput;
            relay.setChanged();
            WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(pollServerLevel);
            GlobalPos here = GlobalPos.of(pollServerLevel.dimension(), pos);
            refreshLoaded(pollServerLevel, registry.publishRoot(here, polledInput));
            pushUpdateTo(level, pos);
         }
      }

      if (--relay.safetyNetCountdown > 0) return;
      relay.safetyNetCountdown = SAFETY_NET_INTERVAL_TICKS;

      if (relay.linkedPos != null && level.isLoaded(relay.linkedPos)
            && !(level.getBlockEntity(relay.linkedPos) instanceof WirelessRedstoneRelayBlockEntity)) {
         relay.linkedPos = null;
         relay.setChanged();
      }
      if (!relay.upstreamHubs.isEmpty()) {
         boolean removedAny = relay.upstreamHubs.removeIf(hubPos ->
               level.isLoaded(hubPos) && !(level.getBlockEntity(hubPos) instanceof WirelessRedstoneRelayBlockEntity));
         if (removedAny) relay.setChanged();
      }
      if (!relay.broadcastTargets.isEmpty()) {
         relay.broadcastTargets.removeIf(targetPos ->
               level.isLoaded(targetPos) && !(level.getBlockEntity(targetPos) instanceof WirelessRedstoneRelayBlockEntity));
      }
      relay.updateConnectedState(level);

      relay.onNeighborChanged(level, pos, state);
      if (relay.upstreamHubs.isEmpty() && level instanceof ServerLevel serverLevel) {

         WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(serverLevel);
         GlobalPos here = GlobalPos.of(serverLevel.dimension(), pos);
         refreshLoaded(serverLevel, registry.reassert(here, relay.selfInputPowered));
      }
   }

   @Override
   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);
      this.linkedPos = input.read("linked_pos", BlockPos.CODEC).orElse(null);
      this.upstreamHubs.clear();

      List<BlockPos> savedUpstreams = input.read("upstream_hubs", BlockPos.CODEC.listOf()).orElse(null);
      if (savedUpstreams != null) {
         this.upstreamHubs.addAll(savedUpstreams);
      } else {
         input.read("upstream_hub", BlockPos.CODEC).ifPresent(this.upstreamHubs::add);
      }
      this.meshCombine = switch (input.getStringOr("mesh_combine", "OR")) {
         case "AND" -> MeshCombine.AND;
         case "LATEST" -> MeshCombine.LATEST;
         default -> MeshCombine.OR;
      };

      String modeName = input.getStringOr("mode", null);
      if (modeName != null) {
         this.mode = switch (modeName) {
            case "MULTI" -> LinkMode.MULTI;
            case "CONNECTION" -> LinkMode.CONNECTION;
            default -> LinkMode.SINGLE;
         };
      } else {
         this.mode = input.getBooleanOr("multi_mode", false) ? LinkMode.MULTI : LinkMode.SINGLE;
      }
      this.broadcastTargets.clear();
      this.broadcastTargets.addAll(input.read("broadcast_targets", BlockPos.CODEC.listOf()).orElse(List.of()));
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);
      output.storeNullable("linked_pos", BlockPos.CODEC, this.linkedPos);
      if (!this.upstreamHubs.isEmpty()) {
         output.store("upstream_hubs", BlockPos.CODEC.listOf(), List.copyOf(this.upstreamHubs));
      }
      output.putString("mesh_combine", this.meshCombine.name());
      output.putString("mode", this.mode.name());
      if (!this.broadcastTargets.isEmpty()) {
         output.store("broadcast_targets", BlockPos.CODEC.listOf(), List.copyOf(this.broadcastTargets));
      }
   }
}

