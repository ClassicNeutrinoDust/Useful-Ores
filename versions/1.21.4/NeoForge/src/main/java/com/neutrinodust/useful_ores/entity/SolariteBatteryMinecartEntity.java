package com.neutrinodust.useful_ores.entity;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.block.rail.SolariteRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailBlock;
import com.neutrinodust.useful_ores.block.rail.TitaniumRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.Vec3;

public class SolariteBatteryMinecartEntity extends Minecart {

    public static final double SOLARITE_MAX_SPEED = 1.6;
    private static final double GRID_POWER_SPEED_THRESHOLD = 0.8;

    private static final float[] SPEED_PRESETS = { 0.2f, 0.5f, 0.8f, 1.1f, 1.4f, 1.6f };
    private static final float DEFAULT_CRUISE_SPEED = SPEED_PRESETS[0];

    private static final float CONTROLLER_RATE = 0.12f;

    private static final double VANILLA_CURVE_DERAIL_SPEED = 1.4;

    private static final double MIN_MOVING_SPEED = 1.0e-7;

    private static final float BATTERY_CAPACITY_TICKS = 20f * 60f * 20f;

    private static final float RECHARGE_PER_TICK = 1f / (10f * 60f * 20f);

    private static final EntityDataAccessor<Float> DATA_CRUISE_SPEED =
            SynchedEntityData.defineId(SolariteBatteryMinecartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BATTERY =
            SynchedEntityData.defineId(SolariteBatteryMinecartEntity.class, EntityDataSerializers.FLOAT);

    private int requestedDirection;

    private boolean propulsionPrimed;

    private Vec3 lastPropulsionDirection = Vec3.ZERO;

    private boolean reversedForCurrentStop;

    public SolariteBatteryMinecartEntity(EntityType<? extends Minecart> type, Level level) {
        super(type, level);
    }

    @Override
    protected net.minecraft.world.item.Item getDropItem() {
        return com.neutrinodust.useful_ores.init.ModItems.SOLARITE_BATTERY_MINECART.get();
    }

    @Override
    public net.minecraft.world.item.ItemStack getPickResult() {
        return new net.minecraft.world.item.ItemStack(
                com.neutrinodust.useful_ores.init.ModItems.SOLARITE_BATTERY_MINECART.get());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CRUISE_SPEED, DEFAULT_CRUISE_SPEED);
        builder.define(DATA_BATTERY, 1f);
    }

    public float getCruiseSpeed() {
        return entityData.get(DATA_CRUISE_SPEED);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
                if (player instanceof ServerPlayer serverPlayer) {
                    cycleCruiseSpeed(serverPlayer);
                }
            }
            return level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return super.interact(player, hand);
    }

    private void cycleCruiseSpeed(ServerPlayer player) {
        float current = getCruiseSpeed();
        int next = 0;

        for (int i = 0; i < SPEED_PRESETS.length; i++) {
            if (current < SPEED_PRESETS[i] - 1.0e-4f) {
                next = i;
                break;
            }
            next = (i + 1) % SPEED_PRESETS.length;
        }

        float selected = SPEED_PRESETS[next];
        entityData.set(DATA_CRUISE_SPEED, selected);
        player.displayClientMessage(
                Component.literal(String.format(java.util.Locale.ROOT,
                        "Solarite minecart speed: %.0f b/s", selected * 20.0f)),
                true);
    }

    public float getBatteryLevel() {
        return entityData.get(DATA_BATTERY);
    }

    private BlockPos railPos() {
        return getCurrentBlockPosOrRailBelow();
    }

    private BlockState railState() {
        return level().getBlockState(railPos());
    }

    private boolean isDay() {
        if (level().dimension() != Level.OVERWORLD) return false;
        long time = level().getDayTime() % 24000L;
        return time < 12000L;
    }

    private boolean hasSunlight() {
        return isDay() && level().canSeeSky(railPos().above());
    }

    private boolean isOnSolariteRail() {
        return railState().getBlock() instanceof SolariteRailBlock;
    }

    private boolean isOnPoweredRail() {
        BlockState state = railState();
        return state.getBlock() instanceof PoweredRailBlock
                && state.getValue(BlockStateProperties.POWERED);
    }

    private boolean hasPropulsionPower(double currentSpeed) {
        if (isOnSolariteRail()) {
            return hasSunlight() || getBatteryLevel() > 0f;
        }

        if (isOnPoweredRail()) {
            if (currentSpeed <= GRID_POWER_SPEED_THRESHOLD + 1.0e-6) return true;
            return hasSunlight() || getBatteryLevel() > 0f;
        }

        return hasSunlight() || getBatteryLevel() > 0f;
    }

    private double targetSpeed() {
        if (isOnSolariteRail() && hasSunlight()) return SOLARITE_MAX_SPEED;
        return Math.min(SOLARITE_MAX_SPEED, Math.max(0.0, getCruiseSpeed()));
    }

    public double getEffectiveDisplaySpeed() {
        return targetSpeed();
    }

    public Vec3 solariteApplyNaturalSlowdown(Vec3 movement) {
        return applyNaturalSlowdown(movement);
    }

    public double getSolariteMaxTrackSpeed(ServerLevel level) {
        return getMaxSpeed(level);
    }

    @Override
    public double getMaxSpeed(ServerLevel level) {
        return targetSpeed();
    }

    public boolean isPropulsionPrimed() {
        return propulsionPrimed;
    }

    public void primePropulsion() {
        propulsionPrimed = true;
    }

    protected Vec3 applyNaturalSlowdown(Vec3 movement) {
        Vec3 vanilla = super.applyNaturalSlowdown(movement);
        double speed = vanilla.horizontalDistance();

        if (speed > MIN_MOVING_SPEED) {
            lastPropulsionDirection = new Vec3(vanilla.x / speed, 0.0, vanilla.z / speed);
        }

        boolean poweredRail = isOnPoweredRail();
        boolean solariteRail = isOnSolariteRail();
        boolean hasPower = hasPropulsionPower(Math.max(speed, 0.0));

        if (speed < MIN_MOVING_SPEED) {

            if (poweredRail) return vanilla;

            if (solariteRail && isSolariteRailActive()) {
                Vec3 startDirection = stationaryRailStartDirection(railState());
                if (startDirection.lengthSqr() > 1.0e-12 && hasPower) {
                    return new Vec3(startDirection.x * 0.02, vanilla.y, startDirection.z * 0.02);
                }
            }

            boolean shouldStart = propulsionPrimed && hasPower && (
                    requestedDirection != 0
                    || lastPropulsionDirection.lengthSqr() > 0.0
            );

            if (!shouldStart) return vanilla;

            Vec3 direction = lastPropulsionDirection;
            if (direction.lengthSqr() < 1.0e-12) {
                direction = railDirection(railState());
            }
            if (direction.lengthSqr() < 1.0e-12) return vanilla;

            return new Vec3(direction.x * 0.08, vanilla.y, direction.z * 0.08);
        }

        boolean grid = poweredRail && speed <= GRID_POWER_SPEED_THRESHOLD + 1.0e-6;
        boolean propulsion = grid || hasPower;
        if (!propulsion) return vanilla;

        double target = targetSpeed();
        if (speed >= target - 1.0e-9) return vanilla;

        double acceleration = solariteRail && hasSunlight() ? 0.10 : 0.08;
        double newSpeed = Math.min(target, speed + acceleration);
        double scale = newSpeed / speed;
        return new Vec3(vanilla.x * scale, vanilla.y, vanilla.z * scale);
    }

    private boolean isSolariteRailActive() {
        BlockState state = railState();
        return state.getBlock() instanceof SolariteRailBlock
                && state.hasProperty(SolariteRailBlock.LIT)
                && state.getValue(SolariteRailBlock.LIT);
    }

    private Vec3 stationaryRailStartDirection(BlockState state) {
        if (!(state.getBlock() instanceof BaseRailBlock railBlock)) return Vec3.ZERO;
        RailShape shape = state.getValue(railBlock.getShapeProperty());
        BlockPos pos = railPos();

        if (shape == RailShape.EAST_WEST) {
            if (usefulOres$isRedstoneConductor(pos.west())) return new Vec3(1.0, 0.0, 0.0);
            if (usefulOres$isRedstoneConductor(pos.east())) return new Vec3(-1.0, 0.0, 0.0);
        } else if (shape == RailShape.NORTH_SOUTH) {
            if (usefulOres$isRedstoneConductor(pos.north())) return new Vec3(0.0, 0.0, 1.0);
            if (usefulOres$isRedstoneConductor(pos.south())) return new Vec3(0.0, 0.0, -1.0);
        }
        return Vec3.ZERO;
    }

    private Vec3 railDirection(BlockState state) {
        if (!(state.getBlock() instanceof BaseRailBlock railBlock)) return Vec3.ZERO;
        RailShape shape = state.getValue(railBlock.getShapeProperty());
        return switch (shape) {
            case EAST_WEST, ASCENDING_EAST -> new Vec3(1.0, 0.0, 0.0);
            case ASCENDING_WEST -> new Vec3(-1.0, 0.0, 0.0);
            case NORTH_SOUTH, ASCENDING_NORTH -> new Vec3(0.0, 0.0, 1.0);
            case ASCENDING_SOUTH -> new Vec3(0.0, 0.0, -1.0);
            case SOUTH_EAST -> new Vec3(0.70710678, 0.0, -0.70710678);
            case SOUTH_WEST -> new Vec3(-0.70710678, 0.0, -0.70710678);
            case NORTH_WEST -> new Vec3(-0.70710678, 0.0, 0.70710678);
            case NORTH_EAST -> new Vec3(0.70710678, 0.0, 0.70710678);
        };
    }

    private boolean usefulOres$isRedstoneConductor(BlockPos pos) {
        return level().getBlockState(pos).isRedstoneConductor(level(), pos);
    }

    /**
     * Vanilla minecarts let a non-player mob become a passenger when the
     * (moving) cart collides with it. The custom movement/behavior handling
     * on this entity ends up bypassing that vanilla hook, so mobs never sit
     * down in this cart. This restores the same feel: an empty cart that
     * touches an unmounted, non-player living entity picks it up as a rider.
     */
    private void trySeatNearbyMob() {
        if (!getPassengers().isEmpty()) return;

        AABB pickupBox = getBoundingBox().inflate(0.2, 0.0, 0.2);
        java.util.List<LivingEntity> nearby = level().getEntitiesOfClass(
                LivingEntity.class,
                pickupBox,
                candidate -> candidate.isAlive()
                        && !(candidate instanceof Player)
                        && candidate.getVehicle() == null
                        && !candidate.isPassenger());

        if (!nearby.isEmpty()) {
            nearby.get(0).startRiding(this);
        }
    }

    public void setSpeedInput(int direction) {
        requestedDirection = Integer.signum(direction);
    }

    private void updateSpeedController() {

        if (isOnSolariteRail() && hasSunlight()) return;

        if (requestedDirection == 0) return;

        float current = getCruiseSpeed();
        float next = current + requestedDirection * CONTROLLER_RATE;
        next = Math.max(0.0f, Math.min((float) SOLARITE_MAX_SPEED, next));

        if (Math.abs(next - current) > 1.0e-6f) {
            entityData.set(DATA_CRUISE_SPEED, next);
        }
    }

    private boolean isVanillaRailCurve() {
        BlockState state = railState();
        if (!(state.getBlock() instanceof RailBlock)) return false;
        if (state.getBlock() instanceof TitaniumRailBlock) return false;
        if (state.getBlock() instanceof PoweredRailBlock) return false;
        if (!(state.getBlock() instanceof BaseRailBlock railBlock)) return false;
        RailShape shape = state.getValue(railBlock.getShapeProperty());
        return switch (shape) {
            case SOUTH_EAST, SOUTH_WEST, NORTH_WEST, NORTH_EAST -> true;
            default -> false;
        };
    }

    private void applyVanillaCurveDerailRule() {
        if (level().isClientSide()) return;
        if (!isVanillaRailCurve()) return;

        double speed = getDeltaMovement().horizontalDistance();
        if (speed <= VANILLA_CURVE_DERAIL_SPEED) return;

        setOnRails(false);
    }

    @Override
    public void tick() {

        super.tick();

        if (level().isClientSide()) return;

        trySeatNearbyMob();

        if (!propulsionPrimed && getDeltaMovement().horizontalDistance() > 0.0025) {
            propulsionPrimed = true;
        }

        double stopSpeed = getDeltaMovement().horizontalDistance();
        if (stopSpeed > 0.02) {
            reversedForCurrentStop = false;
        } else if (propulsionPrimed && horizontalCollision && !reversedForCurrentStop
                && lastPropulsionDirection.lengthSqr() > 1.0e-12) {
            lastPropulsionDirection = lastPropulsionDirection.scale(-1.0);
            reversedForCurrentStop = true;
        }

        updateSpeedController();
        applyVanillaCurveDerailRule();

        double speed = getDeltaMovement().horizontalDistance();
        boolean moving = speed > 0.05;
        boolean solar = hasSunlight();
        boolean solariteRail = isOnSolariteRail();
        boolean poweredRail = isOnPoweredRail();
        float battery = getBatteryLevel();

        if (solar) {
            entityData.set(DATA_BATTERY, Math.min(1f, battery + RECHARGE_PER_TICK));
        }

        if (!moving) return;

        boolean grid = poweredRail && speed <= GRID_POWER_SPEED_THRESHOLD + 1.0e-6;
        if (grid || solar) return;

        if (battery > 0f) {

            float drain = 1f / BATTERY_CAPACITY_TICKS;
            entityData.set(DATA_BATTERY, Math.max(0f, battery - drain));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag output) {
        super.addAdditionalSaveData(output);
        output.putFloat("SolariteBattery", getBatteryLevel());
        output.putFloat("SolariteCruiseSpeed", getCruiseSpeed());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag input) {
        super.readAdditionalSaveData(input);
        entityData.set(DATA_BATTERY, Math.max(0f, Math.min(1f, NbtCompat.getFloatOr(input, "SolariteBattery", 1f))));
        entityData.set(DATA_CRUISE_SPEED, Math.max(0.1f, Math.min((float) SOLARITE_MAX_SPEED,
                NbtCompat.getFloatOr(input, "SolariteCruiseSpeed", DEFAULT_CRUISE_SPEED))));
    }

    public boolean isRunningOnFreeSolarPower() {
        double speed = getDeltaMovement().horizontalDistance();

        if (isOnSolariteRail()) {
            return hasSunlight();
        }

        if (isOnPoweredRail()) {
            return speed > GRID_POWER_SPEED_THRESHOLD + 1.0e-6 && hasSunlight();
        }

        return hasSunlight();
    }

    public boolean isRunningOnGridPower() {
        double speed = getDeltaMovement().horizontalDistance();
        return isOnPoweredRail() && speed <= GRID_POWER_SPEED_THRESHOLD + 1.0e-6;
    }

    public boolean isRunningOnBatteryPower() {
        return !isRunningOnFreeSolarPower() && !isRunningOnGridPower();
    }
}

