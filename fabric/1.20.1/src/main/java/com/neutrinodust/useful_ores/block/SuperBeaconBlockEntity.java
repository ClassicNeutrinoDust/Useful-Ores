package com.neutrinodust.useful_ores.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class SuperBeaconBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private static final int MAX_LEVELS = 4;
    public static final List<List<MobEffect>> BEACON_EFFECTS = List.of(
        List.of(MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED),
        List.of(MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP),
        List.of(MobEffects.DAMAGE_BOOST),
        List.of(MobEffects.REGENERATION)
    );
    private static final Set<MobEffect> VALID_EFFECTS = BEACON_EFFECTS.stream().flatMap(Collection::stream).collect(Collectors.toSet());

    private static final class AccessibleBeaconBeamSection extends BeaconBlockEntity.BeaconBeamSection {
        private AccessibleBeaconBeamSection(float[] color) {
            super(color);
        }

        private void incrementHeight() {
            super.increaseHeight();
        }
    }
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_SECONDARY = 2;
    public static final int NUM_DATA_VALUES = 3;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final Component DEFAULT_NAME = Component.translatable("container.beacon");
    private static final String TAG_PRIMARY = "primary_effect";
    private static final String TAG_SECONDARY = "secondary_effect";

    private static final int BASE_RANGE = 64;
    private static final int RANGE_PER_LEVEL = 112;

    private List<BeaconBlockEntity.BeaconBeamSection> beamSections = new ArrayList<>();
    private List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections = new ArrayList<>();
    private int levels;
    private int lastCheckY;
    private @Nullable MobEffect primaryPower;
    private @Nullable MobEffect secondaryPower;
    private @Nullable Component name;
    private LockCode lockKey = LockCode.NO_LOCK;

    private boolean chunkForced = false;
    private final ContainerData dataAccess = new ContainerData() {
        {
            Objects.requireNonNull(SuperBeaconBlockEntity.this);
        }

        @Override
        public int get(int dataId) {
            return switch (dataId) {
                case 0 -> SuperBeaconBlockEntity.this.levels;
                case 1 -> MobEffect.getIdFromNullable(SuperBeaconBlockEntity.this.primaryPower);
                case 2 -> MobEffect.getIdFromNullable(SuperBeaconBlockEntity.this.secondaryPower);
                default -> 0;
            };
        }

        @Override
        public void set(int dataId, int value) {
            switch (dataId) {
                case 0:
                    SuperBeaconBlockEntity.this.levels = value;
                    break;
                case 1:
                    if (!SuperBeaconBlockEntity.this.level.isClientSide() && !SuperBeaconBlockEntity.this.beamSections.isEmpty()) {
                        SuperBeaconBlockEntity.playSound(SuperBeaconBlockEntity.this.level, SuperBeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
                    }

                    SuperBeaconBlockEntity.this.primaryPower = SuperBeaconBlockEntity.filterEffect(MobEffect.byId(value));
                    break;
                case 2:
                    SuperBeaconBlockEntity.this.secondaryPower = SuperBeaconBlockEntity.filterEffect(MobEffect.byId(value));
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    private static @Nullable MobEffect filterEffect(@Nullable MobEffect effect) {
        return VALID_EFFECTS.contains(effect) ? effect : null;
    }

    public SuperBeaconBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.SUPER_BEACON, worldPosition, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState selfState, SuperBeaconBlockEntity entity) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockPos checkPos;
        if (entity.lastCheckY < y) {
            checkPos = pos;
            entity.checkingBeamSections = Lists.newArrayList();
            entity.lastCheckY = pos.getY() - 1;
        } else {
            checkPos = new BlockPos(x, entity.lastCheckY + 1, z);
        }

        BeaconBlockEntity.BeaconBeamSection lastBeamSection = entity.checkingBeamSections.isEmpty()
            ? null
            : entity.checkingBeamSections.get(entity.checkingBeamSections.size() - 1);
        
        int lastSetBlock = level.getMaxBuildHeight() - 1;

        for (int i = 0; i < 10 && checkPos.getY() <= lastSetBlock; i++) {
            BlockState state = level.getBlockState(checkPos);

            float[] color = state.getBlock() instanceof net.minecraft.world.level.block.BeaconBeamBlock beaconBeamBlock
                    ? beaconBeamBlock.getColor().getTextureDiffuseColors()
                    : null;
            if (color != null) {
                if (entity.checkingBeamSections.size() <= 1) {
                    lastBeamSection = new AccessibleBeaconBeamSection(color);
                    entity.checkingBeamSections.add(lastBeamSection);
                } else if (lastBeamSection != null) {
                    if (java.util.Arrays.equals(color, lastBeamSection.getColor())) {
                        ((AccessibleBeaconBeamSection) lastBeamSection).incrementHeight();
                    } else {
                        lastBeamSection = new AccessibleBeaconBeamSection(averageRgb(lastBeamSection.getColor(), color));
                        entity.checkingBeamSections.add(lastBeamSection);
                    }
                }
            } else {
                if (lastBeamSection == null || state.getLightBlock(level, checkPos) >= 15 && !state.is(Blocks.BEDROCK)) {
                    entity.checkingBeamSections.clear();
                    entity.lastCheckY = lastSetBlock;
                    break;
                }

                ((AccessibleBeaconBeamSection) lastBeamSection).incrementHeight();
            }

            checkPos = checkPos.above();
            entity.lastCheckY++;
        }

        int previousLevels = entity.levels;
        if (level.getGameTime() % 80L == 0L) {
            if (!entity.beamSections.isEmpty()) {
                entity.levels = updateBase(level, x, y, z);
            }

            if (entity.levels > 0 && !entity.beamSections.isEmpty()) {
                applyEffects(level, pos, entity.levels, entity.primaryPower, entity.secondaryPower);
                playSound(level, pos, SoundEvents.BEACON_AMBIENT);
            }
        }

        if (entity.lastCheckY >= lastSetBlock) {
            entity.lastCheckY = level.getMinBuildHeight() - 1;
            boolean wasActive = previousLevels > 0;
            entity.beamSections = entity.checkingBeamSections;
            if (!level.isClientSide()) {
                boolean isActive = entity.levels > 0;
                if (!wasActive && isActive) {
                    playSound(level, pos, SoundEvents.BEACON_ACTIVATE);
                    entity.setChunkForced(true);

                    for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(x, y, z, x, y - 4, z).inflate(10.0, 5.0, 10.0))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(player, entity.levels);
                    }
                } else if (wasActive && !isActive) {
                    playSound(level, pos, SoundEvents.BEACON_DEACTIVATE);
                    entity.setChunkForced(false);
                }
            }
        }
    }

    private static int updateBase(Level level, int x, int y, int z) {
        int levels = 0;

        for (int step = 1; step <= 4; levels = step++) {
            int ly = y - step;
            if (ly < level.getMinBuildHeight()) {
                break;
            }

            boolean isOk = true;

            for (int lx = x - step; lx <= x + step && isOk; lx++) {
                for (int lz = z - step; lz <= z + step; lz++) {
                    if (!level.getBlockState(new BlockPos(lx, ly, lz)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        isOk = false;
                        break;
                    }
                }
            }

            if (!isOk) {
                break;
            }
        }

        return levels;
    }

    @Override
    public void setRemoved() {
        playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
        this.setChunkForced(false);
        super.setRemoved();
    }

    private void setChunkForced(boolean forced) {
        if (this.chunkForced == forced) {
            return;
        }

        if (this.level instanceof ServerLevel serverLevel) {
            int chunkX = this.worldPosition.getX() >> 4;
            int chunkZ = this.worldPosition.getZ() >> 4;
            serverLevel.setChunkForced(chunkX, chunkZ, forced);
            this.chunkForced = forced;
        }
    }

    private static void applyEffects(
        Level level, BlockPos worldPosition, int levels, @Nullable MobEffect primaryPower, @Nullable MobEffect secondaryPower
    ) {
        if (!level.isClientSide() && primaryPower != null) {
            double range = BASE_RANGE + levels * RANGE_PER_LEVEL;
            int baseAmp = 0;
            if (levels >= 4 && Objects.equals(primaryPower, secondaryPower)) {
                baseAmp = 1;
            }

            int durationTicks = (9 + levels * 2) * 20;
            AABB bb = new AABB(worldPosition).inflate(range).expandTowards(0.0, level.getHeight(), 0.0);
            List<Player> players = level.getEntitiesOfClass(Player.class, bb);

            for (Player player : players) {
                player.addEffect(new MobEffectInstance(primaryPower, durationTicks, baseAmp, true, true));
            }

            if (levels >= 4 && !Objects.equals(primaryPower, secondaryPower) && secondaryPower != null) {
                for (Player player : players) {
                    player.addEffect(new MobEffectInstance(secondaryPower, durationTicks, 0, true, true));
                }
            }
        }
    }

    public static void playSound(Level level, BlockPos worldPosition, SoundEvent event) {
        level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public List<BeaconBlockEntity.BeaconBeamSection> getBeamSections() {
        return (List<BeaconBlockEntity.BeaconBeamSection>)(this.levels == 0 ? ImmutableList.of() : this.beamSections);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    private static float[] averageRgb(float[] a, float[] b) {
        return new float[] {(a[0] + b[0]) / 2.0F, (a[1] + b[1]) / 2.0F, (a[2] + b[2]) / 2.0F};
    }

    private static void storeEffect(CompoundTag output, String field, @Nullable MobEffect effect) {
        if (effect != null) {
            ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (key != null) output.putString(field, key.toString());
        }
    }

    private static @Nullable MobEffect loadEffect(CompoundTag input, String field) {
        if (!input.contains(field, net.minecraft.nbt.Tag.TAG_STRING)) return null;
        ResourceLocation key = ResourceLocation.tryParse(input.getString(field));
        if (key == null) return null;
        return BuiltInRegistries.MOB_EFFECT.getOptional(key).filter(VALID_EFFECTS::contains).orElse(null);
    }

    @Override
    public void load(CompoundTag input) {
        super.load(input);
        this.primaryPower = loadEffect(input, "primary_effect");
        this.secondaryPower = loadEffect(input, "secondary_effect");
        this.name = input.contains("CustomName", net.minecraft.nbt.Tag.TAG_STRING)
            ? Component.Serializer.fromJson(input.getString("CustomName")) : null;
        this.lockKey = LockCode.fromTag(input);
    }

    @Override
    protected void saveAdditional(CompoundTag output) {
        super.saveAdditional(output);
        storeEffect(output, "primary_effect", this.primaryPower);
        storeEffect(output, "secondary_effect", this.secondaryPower);
        output.putInt("Levels", this.levels);
        if (this.name != null) output.putString("CustomName", Component.Serializer.toJson(this.name));
        this.lockKey.addToTag(output);
    }

    public void setCustomName(@Nullable Component name) {
        this.name = name;
    }

    @Override
    public @Nullable Component getCustomName() {
        return this.name;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (BaseContainerBlockEntity.canUnlock(player, this.lockKey, this.getDisplayName())) {
            return new SuperBeaconMenu(containerId, inventory, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()));
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.lastCheckY = level.getMinBuildHeight() - 1;
    }
}

