package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class WirelessRelayNetworkData extends SavedData {

   public enum TransportMode {

      INSTANT,

      GOSSIP,

      BEACON,

      STORE_AND_FORWARD
   }

   public enum MeshCombine {
      OR, AND, LATEST
   }

   private static final int REGION_SIZE_BLOCKS = 64;

   private final AtomicLong logicalClock = new AtomicLong(1);

   public record VersionedValue(boolean value, long version) {
      static final VersionedValue DEFAULT = new VersionedValue(false, 0);
   }

   private final Map<GlobalPos, VersionedValue> values = new HashMap<>();

   private final Map<GlobalPos, List<GlobalPos>> downstream = new HashMap<>();

   private final Map<GlobalPos, List<GlobalPos>> upstreamsOf = new HashMap<>();

   private final Map<GlobalPos, MeshCombine> combineOf = new HashMap<>();

   private final Deque<GlobalPos> gossipQueue = new ArrayDeque<>();

   private final Set<GlobalPos> queued = new HashSet<>();

   private final Set<GlobalPos> knownRoots = new HashSet<>();

   private static final int GOSSIP_BUDGET_PER_TICK = 256;

   private record ValueEntry(ResourceKey<Level> dimension, BlockPos pos, boolean on, long version) {}
   private record EdgeEntry(ResourceKey<Level> parentDim, BlockPos parentPos, ResourceKey<Level> childDim, BlockPos childPos) {}

   private static final Codec<ValueEntry> VALUE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
         ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(ValueEntry::dimension),
         BlockPos.CODEC.fieldOf("pos").forGetter(ValueEntry::pos),
         Codec.BOOL.fieldOf("on").forGetter(ValueEntry::on),
         Codec.LONG.optionalFieldOf("version", 0L).forGetter(ValueEntry::version)
   ).apply(instance, ValueEntry::new));

   private static final Codec<EdgeEntry> EDGE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
         ResourceKey.codec(Registries.DIMENSION).fieldOf("parent_dim").forGetter(EdgeEntry::parentDim),
         BlockPos.CODEC.fieldOf("parent_pos").forGetter(EdgeEntry::parentPos),
         ResourceKey.codec(Registries.DIMENSION).fieldOf("child_dim").forGetter(EdgeEntry::childDim),
         BlockPos.CODEC.fieldOf("child_pos").forGetter(EdgeEntry::childPos)
   ).apply(instance, EdgeEntry::new));

   private record CombineEntry(ResourceKey<Level> dimension, BlockPos pos, String mode) {}

   private static final Codec<CombineEntry> COMBINE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
         ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(CombineEntry::dimension),
         BlockPos.CODEC.fieldOf("pos").forGetter(CombineEntry::pos),
         Codec.STRING.fieldOf("mode").forGetter(CombineEntry::mode)
   ).apply(instance, CombineEntry::new));

   private record Storage(List<ValueEntry> values, List<EdgeEntry> edges, List<CombineEntry> combines, long logicalClock) {}

   private static final Codec<Storage> STORAGE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
         VALUE_CODEC.listOf().fieldOf("values").forGetter(Storage::values),
         EDGE_CODEC.listOf().fieldOf("edges").forGetter(Storage::edges),
         COMBINE_CODEC.listOf().optionalFieldOf("combines", List.of()).forGetter(Storage::combines),
         Codec.LONG.optionalFieldOf("logical_clock", 1L).forGetter(Storage::logicalClock)
   ).apply(instance, Storage::new));

   public static final Codec<WirelessRelayNetworkData> CODEC = STORAGE_CODEC.xmap(
         storage -> {
            WirelessRelayNetworkData data = new WirelessRelayNetworkData();
            for (ValueEntry e : storage.values()) {
               data.values.put(GlobalPos.of(e.dimension(), e.pos()), new VersionedValue(e.on(), e.version()));
            }
            for (EdgeEntry e : storage.edges()) {
               GlobalPos parent = GlobalPos.of(e.parentDim(), e.parentPos());
               GlobalPos child = GlobalPos.of(e.childDim(), e.childPos());
               data.downstream.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);

               data.upstreamsOf.computeIfAbsent(child, k -> new ArrayList<>()).add(parent);
            }
            for (CombineEntry e : storage.combines()) {
               MeshCombine mode = switch (e.mode()) {
                  case "AND" -> MeshCombine.AND;
                  case "LATEST" -> MeshCombine.LATEST;
                  default -> MeshCombine.OR;
               };
               data.combineOf.put(GlobalPos.of(e.dimension(), e.pos()), mode);
            }
            data.logicalClock.set(Math.max(1L, storage.logicalClock()));
            return data;
         },
         data -> {
            List<ValueEntry> values = data.values.entrySet().stream()
                  .map(e -> new ValueEntry(e.getKey().dimension(), e.getKey().pos(), e.getValue().value(), e.getValue().version()))
                  .toList();
            List<EdgeEntry> edges = new ArrayList<>();
            for (Map.Entry<GlobalPos, List<GlobalPos>> entry : data.downstream.entrySet()) {
               GlobalPos parent = entry.getKey();
               for (GlobalPos child : entry.getValue()) {
                  edges.add(new EdgeEntry(parent.dimension(), parent.pos(), child.dimension(), child.pos()));
               }
            }
            List<CombineEntry> combines = data.combineOf.entrySet().stream()
                  .map(e -> new CombineEntry(e.getKey().dimension(), e.getKey().pos(), e.getValue().name()))
                  .toList();
            return new Storage(values, edges, combines, data.logicalClock.get());
         }
   );

   public static final SavedData.Factory<WirelessRelayNetworkData> TYPE = new SavedData.Factory<>(
            WirelessRelayNetworkData::new,
            (tag, registries) -> NbtCompat.loadCodec(tag, CODEC, WirelessRelayNetworkData::new)
    );

   public static WirelessRelayNetworkData get(ServerLevel anyLevel) {
      ServerLevel overworld = anyLevel.getServer().overworld();
      DimensionDataStorage storage = overworld.getDataStorage();
      return storage.computeIfAbsent(TYPE, "wireless_relay_network");
   }

   public long nextVersion() {
      return this.logicalClock.incrementAndGet();
   }

   public boolean get(GlobalPos pos) {
      return this.values.getOrDefault(pos, VersionedValue.DEFAULT).value();
   }

   public long getVersion(GlobalPos pos) {
      return this.values.getOrDefault(pos, VersionedValue.DEFAULT).version();
   }

   private boolean applyIfNewer(GlobalPos pos, boolean value, long version) {
      VersionedValue current = this.values.get(pos);
      if (current != null && current.version() >= version) return false;
      this.values.put(pos, new VersionedValue(value, version));
      setDirty();
      return true;
   }

   public void addEdge(GlobalPos parent, GlobalPos child) {
      
      this.knownRoots.remove(child);
      List<GlobalPos> children = this.downstream.computeIfAbsent(parent, k -> new ArrayList<>());
      if (!children.contains(child)) {
         children.add(child);
         List<GlobalPos> ups = this.upstreamsOf.computeIfAbsent(child, k -> new ArrayList<>());
         if (!ups.contains(parent)) ups.add(parent);
         setDirty();

         Set<GlobalPos> changed = new HashSet<>();
         if (recomputeTopology(child)) {
            changed.add(child);
            propagateInstant(child, new HashSet<>(), changed);
         }
      }
   }

   public void removeEdge(GlobalPos parent, GlobalPos child) {
      List<GlobalPos> children = this.downstream.get(parent);
      if (children != null && children.remove(child)) {
         if (children.isEmpty()) this.downstream.remove(parent);
         setDirty();
      }
      List<GlobalPos> ups = this.upstreamsOf.get(child);
      if (ups != null && ups.remove(parent)) {
         if (ups.isEmpty()) {
            this.upstreamsOf.remove(child);
            this.knownRoots.add(child);
            setDirty();
         } else {
            setDirty();
            Set<GlobalPos> changed = new HashSet<>();
            if (recomputeTopology(child)) {
               changed.add(child);
               propagateInstant(child, new HashSet<>(), changed);
            }
         }
      }
   }

   public void setCombineMode(GlobalPos node, MeshCombine combine) {
      MeshCombine previous = this.combineOf.put(node, combine);
      if (previous != combine) {
         setDirty();
         Set<GlobalPos> changed = new HashSet<>();
         if (recomputeTopology(node)) {
            changed.add(node);
            propagateInstant(node, new HashSet<>(), changed);
         }
      }
   }

   public void forgetRelay(GlobalPos pos) {
      
      
      for (GlobalPos child : List.copyOf(this.downstream.getOrDefault(pos, List.of()))) {
         removeEdge(pos, child);
      }
      for (GlobalPos parent : List.copyOf(this.upstreamsOf.getOrDefault(pos, List.of()))) {
         removeEdge(parent, pos);
      }

      boolean changed = false;
      if (this.values.remove(pos) != null) changed = true;
      if (this.downstream.remove(pos) != null) changed = true;
      if (this.upstreamsOf.remove(pos) != null) changed = true;
      if (this.combineOf.remove(pos) != null) changed = true;
      if (this.queued.remove(pos)) changed = true;
      if (this.gossipQueue.remove(pos)) changed = true;
      if (this.knownRoots.remove(pos)) changed = true;
      if (changed) setDirty();
   }

   public List<GlobalPos> getDownstream(GlobalPos pos) {
      return this.downstream.getOrDefault(pos, List.of());
   }

   private void enqueue(GlobalPos pos) {
      if (this.queued.add(pos)) {
         this.gossipQueue.addLast(pos);
      }
   }

   public static boolean sameRegion(GlobalPos a, GlobalPos b) {
      if (!a.dimension().equals(b.dimension())) return false;
      return regionCoord(a.pos().getX()) == regionCoord(b.pos().getX())
            && regionCoord(a.pos().getZ()) == regionCoord(b.pos().getZ());
   }

   private static int regionCoord(int blockCoord) {
      return Math.floorDiv(blockCoord, REGION_SIZE_BLOCKS);
   }

   public Set<GlobalPos> publishRoot(GlobalPos pos, boolean value) {
      if (!this.upstreamsOf.getOrDefault(pos, List.of()).isEmpty()) {
         this.knownRoots.remove(pos);
         return Set.of();
      }
      this.knownRoots.add(pos);
      VersionedValue current = this.values.get(pos);
      if (current != null && current.value() == value) {
         Set<GlobalPos> changed = new HashSet<>();
         propagateInstant(pos, new HashSet<>(), changed);
         return changed;
      }
      long version = nextVersion();
      Set<GlobalPos> changed = new HashSet<>();
      if (applyIfNewer(pos, value, version)) {
         changed.add(pos);
         propagateInstant(pos, new HashSet<>(), changed);
      }
      return changed;
   }

   private boolean recomputeTopology(GlobalPos node) {
      List<GlobalPos> ups = this.upstreamsOf.get(node);
      if (ups == null || ups.isEmpty()) {
         this.knownRoots.add(node);
         return false;
      }
      boolean value = combinedValue(node, ups);
      VersionedValue current = this.values.get(node);
      if (current != null && current.value() == value) return false;
      return applyIfNewer(node, value, nextVersion());
   }

   private boolean recompute(GlobalPos node) {
      List<GlobalPos> ups = this.upstreamsOf.get(node);
      if (ups == null || ups.isEmpty()) {
         this.knownRoots.add(node);
         return false;
      }
      boolean value = combinedValue(node, ups);
      long version = 0;
      for (GlobalPos up : ups) {
         version = Math.max(version, this.values.getOrDefault(up, VersionedValue.DEFAULT).version());
      }
      return applyIfNewer(node, value, version);
   }

   private boolean combinedValue(GlobalPos node, List<GlobalPos> ups) {
      if (ups.size() == 1) {
         return this.values.getOrDefault(ups.get(0), VersionedValue.DEFAULT).value();
      }
      MeshCombine combine = this.combineOf.getOrDefault(node, MeshCombine.OR);
      long bestVersion = -1;
      boolean anyOn = false;
      boolean allOn = true;
      GlobalPos latestSource = null;
      for (GlobalPos up : ups) {
         VersionedValue v = this.values.getOrDefault(up, VersionedValue.DEFAULT);
         anyOn |= v.value();
         allOn &= v.value();
         if (v.version() > bestVersion) {
            bestVersion = v.version();
            latestSource = up;
         }
      }
      return switch (combine) {
         case AND -> allOn;
         case LATEST -> latestSource != null && this.values.getOrDefault(latestSource, VersionedValue.DEFAULT).value();
         default -> anyOn;
      };
   }

   private void propagateInstant(GlobalPos pos, Set<GlobalPos> visited, Set<GlobalPos> changed) {
      if (!visited.add(pos)) return;
      for (GlobalPos child : getDownstream(pos)) {
         if (!recompute(child)) continue;
         changed.add(child);
         if (sameRegion(pos, child)) {
            propagateInstant(child, visited, changed);
         } else {
            enqueue(child);
         }
      }
   }

   public Set<GlobalPos> reassert(GlobalPos pos, boolean value) {
      return publishRoot(pos, value);
   }

   public Set<GlobalPos> drainGossip() {
      return drainGossip(GOSSIP_BUDGET_PER_TICK);
   }

   public Set<GlobalPos> drainGossip(int budget) {
      Set<GlobalPos> changed = new HashSet<>();
      int processed = 0;
      while (processed < budget && !this.gossipQueue.isEmpty()) {
         GlobalPos pos = this.gossipQueue.pollFirst();
         this.queued.remove(pos);
         processed++;
         if (!this.values.containsKey(pos)) continue;
         propagateInstant(pos, new HashSet<>(), changed);
      }
      return changed;
   }

   public Set<GlobalPos> beaconSweep() {
      Set<GlobalPos> changed = new HashSet<>();
      for (GlobalPos root : List.copyOf(this.knownRoots)) {
         if (!this.upstreamsOf.getOrDefault(root, List.of()).isEmpty()) {
            this.knownRoots.remove(root);
            continue;
         }
         VersionedValue current = this.values.get(root);
         if (current == null) continue;
         
         
         propagateInstant(root, new HashSet<>(), changed);
      }
      return changed;
   }
   public boolean hasPendingGossip() {
      return !this.gossipQueue.isEmpty();
   }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}

