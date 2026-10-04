package com.neutrinodust.useful_ores.entity;

import com.neutrinodust.useful_ores.block.rail.SolariteRailBlock;
import com.neutrinodust.useful_ores.minecart.SolariteMinecartBehavior;
import com.neutrinodust.useful_ores.mixin.AbstractMinecartAccessor;
import com.neutrinodust.useful_ores.network.SolariteCartStepsPacket;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import com.neutrinodust.useful_ores.util.NbtCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;







public class SolariteBatteryMinecartEntity extends Minecart {
    public static final double SOLARITE_MAX_SPEED = 1.6D;
    public static final double GRID_POWER_SPEED_THRESHOLD = 0.8D;

    private static final double BATTERY_CAPACITY_TICKS = 20D * 60D * 20D;
    private static final float RECHARGE_PER_TICK = 1F / (10F * 60F * 20F);
    private static final double MIN_MOVING_SPEED = 1.0E-7D;
    private static final float DEFAULT_CRUISE_SPEED = 0.2F;
    
    private static final float CONTROLLER_RATE = 0.12F;
    
    private static final float[] SPEED_PRESETS = {0.2F, 0.5F, 0.8F, 1.1F, 1.4F, 1.6F};
    private static final EntityDataAccessor<Float> DATA_CRUISE_SPEED =
            SynchedEntityData.defineId(SolariteBatteryMinecartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BATTERY =
            SynchedEntityData.defineId(SolariteBatteryMinecartEntity.class, EntityDataSerializers.FLOAT);

    
    private int requestedDirection;
    
    private int launchRequestDirection;
    
    private Vec3 lastPropulsionDirection = Vec3.ZERO;
    private double lastRailSpeed = 0.0D;
    
    private boolean propulsionPrimed;
    
    private boolean reversedForCurrentStop;
    
    private boolean entityCollisionStop;

    private static final double STOPPED_SPEED = 0.01D;
    private static final double TELEPORT_RESET_DISTANCE_SQR = 100.0D;

    private final SolariteMinecartBehavior behavior = new SolariteMinecartBehavior(this);
    private boolean onRails;
    
    private Vec3 lastNetPos;
    private Vec3 lastNetMovement = Vec3.ZERO;


    public SolariteBatteryMinecartEntity(EntityType<? extends Minecart> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getDropItem() {
        return com.neutrinodust.useful_ores.init.ModItems.SOLARITE_BATTERY_MINECART.get();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(com.neutrinodust.useful_ores.init.ModItems.SOLARITE_BATTERY_MINECART.get());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CRUISE_SPEED, DEFAULT_CRUISE_SPEED);
        builder.define(DATA_BATTERY, 1F);
    }

    public float getCruiseSpeed() { return entityData.get(DATA_CRUISE_SPEED); }
    public float getBatteryLevel() { return entityData.get(DATA_BATTERY); }

    public void setSpeedInput(int direction) {
        int normalized = Integer.signum(direction);
        requestedDirection = normalized;
        if (normalized != 0) {
            launchRequestDirection = normalized;
        }
    }

    public void clearSpeedInput() {
        requestedDirection = 0;
    }

    public boolean hasRequestedDirection() { return requestedDirection != 0 || launchRequestDirection != 0; }
    public int getRequestedDirection() { return requestedDirection; }

    
    public int consumeLaunchRequestDirection() {
        int direction = launchRequestDirection != 0 ? launchRequestDirection : requestedDirection;
        launchRequestDirection = 0;
        return direction;
    }

    private BlockPos railPos() { return getCurrentBlockPosOrRailBelow(); }
    private BlockState railState() { return level().getBlockState(railPos()); }

    private boolean isDay() {
        if (level().dimension() != Level.OVERWORLD) return false;
        long time = Math.floorMod(level().getDayTime(), 24000L);
        return time < 12000L;
    }

    private boolean hasSunlight() {
        return isDay() && level().canSeeSky(railPos().above());
    }

    private boolean isOnSolariteRail() {
        return railState().getBlock() instanceof SolariteRailBlock;
    }

    private boolean isOnSolariteRailActive() {
        
        
        
        return isOnSolariteRail() && SolariteRailBlock.shouldBeActive(level(), railPos());
    }

    private boolean isOnPoweredRail() {
        BlockState state = railState();
        return state.getBlock() instanceof PoweredRailBlock
                && state.hasProperty(BlockStateProperties.POWERED)
                && state.getValue(BlockStateProperties.POWERED);
    }

    private boolean hasPropulsionPower(double speed) {
        
        if (isOnSolariteRail()) return isOnSolariteRailActive() || getBatteryLevel() > 0F;

        
        
        if (isOnPoweredRail()) {
            double configuredSpeed = targetSpeed();
            if (configuredSpeed <= GRID_POWER_SPEED_THRESHOLD + 1.0E-6D) return true;
            return isDay() || getBatteryLevel() > 0F;
        }

        
        return isDay() || getBatteryLevel() > 0F;
    }

    public boolean hasPropulsionPowerForRail() {
        return hasPropulsionPower(getDeltaMovement().horizontalDistance());
    }

    public void rememberPropulsionDirection(Vec3 direction) {
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSqr() > 1.0E-12D) {
            lastPropulsionDirection = horizontal.normalize();
        }
    }

    public void markEntityCollisionStop() {
        entityCollisionStop = true;
    }

    public Vec3 getInputRailDirectionForLaunch() {
        return inputRailDirection(consumeLaunchRequestDirection());
    }

    public double targetSpeed() {
        
        
        if (isOnSolariteRailActive()) return SOLARITE_MAX_SPEED;
        return Math.min(SOLARITE_MAX_SPEED, Math.max(0.0D, getCruiseSpeed()));
    }

    
    public double getActualHorizontalSpeed() {
        return Math.min(SOLARITE_MAX_SPEED, getDeltaMovement().horizontalDistance());
    }

    
    
    public double getEffectiveDisplaySpeed() { return targetSpeed(); }

    private Vec3 inputRailDirection(int input) {
        BlockState state = railState();
        if (!(state.getBlock() instanceof BaseRailBlock rail)) return Vec3.ZERO;
        RailShape shape = state.getValue(rail.getShapeProperty());

        Vec3 a;
        Vec3 b;
        switch (shape) {
            case NORTH_SOUTH -> { a = new Vec3(0, 0, -1); b = new Vec3(0, 0, 1); }
            case EAST_WEST -> { a = new Vec3(-1, 0, 0); b = new Vec3(1, 0, 0); }
            case ASCENDING_EAST -> { a = new Vec3(-1, 0, 0); b = new Vec3(1, 1, 0); }
            case ASCENDING_WEST -> { a = new Vec3(1, 0, 0); b = new Vec3(-1, 1, 0); }
            case ASCENDING_NORTH -> { a = new Vec3(0, 0, 1); b = new Vec3(0, 1, -1); }
            case ASCENDING_SOUTH -> { a = new Vec3(0, 0, -1); b = new Vec3(0, 1, 1); }
            case SOUTH_EAST -> { a = new Vec3(0, 0, 1); b = new Vec3(1, 0, 0); }
            case SOUTH_WEST -> { a = new Vec3(0, 0, 1); b = new Vec3(-1, 0, 0); }
            case NORTH_WEST -> { a = new Vec3(0, 0, -1); b = new Vec3(-1, 0, 0); }
            case NORTH_EAST -> { a = new Vec3(0, 0, -1); b = new Vec3(1, 0, 0); }
            default -> { return Vec3.ZERO; }
        }

        Vec3 wanted = Vec3.ZERO;
        if (getFirstPassenger() instanceof Player player) {
            Vec3 look = player.getLookAngle();
            wanted = new Vec3(look.x, 0.0D, look.z);
            if (wanted.lengthSqr() > 1.0E-12D) wanted = wanted.normalize();
        }
        Vec3 da = new Vec3(a.x, 0.0D, a.z).normalize();
        Vec3 db = new Vec3(b.x, 0.0D, b.z).normalize();
        Vec3 chosen = wanted.lengthSqr() > 1.0E-12D
                ? (wanted.dot(da) >= wanted.dot(db) ? da : db)
                : (lastPropulsionDirection.lengthSqr() > 1.0E-12D ? lastPropulsionDirection : da);

        if (input < 0) chosen = chosen.scale(-1.0D);
        return chosen;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!level().isClientSide() && hand == InteractionHand.MAIN_HAND && player instanceof ServerPlayer serverPlayer) {
                cycleCruiseSpeed(serverPlayer);
            }
            return level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return super.interact(player, hand);
    }

    private void cycleCruiseSpeed(ServerPlayer player) {
        float current = getCruiseSpeed();
        int selectedIndex = 0;
        for (int i = 0; i < SPEED_PRESETS.length; i++) {
            if (current < SPEED_PRESETS[i] - 1.0E-4F) {
                selectedIndex = i;
                break;
            }
            selectedIndex = (i + 1) % SPEED_PRESETS.length;
        }
        float selected = SPEED_PRESETS[selectedIndex];
        entityData.set(DATA_CRUISE_SPEED, selected);
        player.displayClientMessage(
                Component.literal(String.format(Locale.ROOT,
                        "Solarite minecart speed: %.0f b/s", selected * 20F)),
                true);
    }

    public boolean isDaytimeSolarPower() { return isDay(); }

    private boolean isGridPoweredLocally(double speed) {
        BlockState state = railState();
        if (!(state.getBlock() instanceof PoweredRailBlock)) return false;
        return state.hasProperty(BlockStateProperties.POWERED)
                && state.getValue(BlockStateProperties.POWERED)
                && speed <= GRID_POWER_SPEED_THRESHOLD + 1.0E-6D;
    }

    public void tickBatterySystem(double speed) {
        if (level().isClientSide()) return;
        speed = Math.max(0.0D, Math.min(SOLARITE_MAX_SPEED, speed));

        boolean solar = isRunningOnFreeSolarPower();
        boolean grid = isRunningOnGridPower();
        float battery = getBatteryLevel();

        if (solar) {
            entityData.set(DATA_BATTERY, Math.min(1F, battery + RECHARGE_PER_TICK));
            return;
        }

        
        if (grid || speed <= 0.05D) return;

        
        
        if (battery > 0F) {
            entityData.set(DATA_BATTERY, Math.max(0F,
                    battery - (float)(1D / BATTERY_CAPACITY_TICKS)));
        }
    }

    public boolean isRunningOnFreeSolarPower() {
        double configuredSpeed = targetSpeed();

        
        if (isOnPoweredRail() && configuredSpeed <= GRID_POWER_SPEED_THRESHOLD + 1.0E-6D) return false;

        if (isOnSolariteRail()) return isOnSolariteRailActive();

        
        return isDay();
    }

    public boolean isRunningOnGridPower() {
        double configuredSpeed = targetSpeed();
        return isOnPoweredRail() && configuredSpeed <= GRID_POWER_SPEED_THRESHOLD + 1.0E-6D;
    }

    public boolean isRunningOnBatteryPower() {
        return !isRunningOnFreeSolarPower() && !isRunningOnGridPower();
    }

    

    public SolariteMinecartBehavior getBehavior() { return behavior; }

    public boolean isFirstTick() { return firstTick; }

    @Override
    public boolean isOnRails() { return onRails; }

    public void setOnRails(boolean onRails) { this.onRails = onRails; }

    public boolean isFlipped() { return ((AbstractMinecartAccessor) (Object) this).useful_ores$isFlipped(); }

    public void setFlipped(boolean flipped) { ((AbstractMinecartAccessor) (Object) this).useful_ores$setFlipped(flipped); }

    
    @Override
    public void applyGravity() { super.applyGravity(); }

    @Override
    public void checkInsideBlocks() { super.checkInsideBlocks(); }

    @Override
    public void comeOffTrack() { super.comeOffTrack(); }

    
    @Override
    public double getMaxSpeed() {
        return targetSpeed();
    }

    
    public BlockPos getCurrentBlockPosOrRailBelow() {
        int x = Mth.floor(getX());
        int y = Mth.floor(getY());
        int z = Mth.floor(getZ());
        double below = getY() - 0.1D - 1.0E-5F;
        if (level().getBlockState(BlockPos.containing(x, below, z)).is(BlockTags.RAILS)) {
            y = Mth.floor(below);
        } else if (level().getBlockState(new BlockPos(x, y - 1, z)).is(BlockTags.RAILS)) {
            y--;
        }
        return new BlockPos(x, y, z);
    }

    @Override
    public void move(MoverType type, Vec3 delta) {
        Vec3 target = position().add(delta);
        super.move(type, delta);
        boolean pushed = behavior.pushAndPickupEntities();
        if (pushed) {
            super.move(type, target.subtract(position()));
        }
        if (type == MoverType.PISTON) {
            onRails = false;
        }
    }

    
    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        setPos(x, y, z);
        setYRot(yRot % 360.0F);
        setXRot(xRot % 360.0F);
    }

    @Override
    public void tick() {
        
        if (getHurtTime() > 0) setHurtTime(getHurtTime() - 1);
        if (getDamage() > 0.0F) setDamage(getDamage() - 1.0F);
        checkBelowWorld();
        handlePortal();
        if (!level().isClientSide()) {
            updateSpeedController();
        }
        behavior.tick();
        updateInWaterStateAndDoFluidPushing();
        if (isInLava()) {
            lavaHurt();
            fallDistance *= 0.5F;
        }
        firstTick = false;
        if (!level().isClientSide()) {
            double speed = Math.min(SOLARITE_MAX_SPEED, getDeltaMovement().horizontalDistance());

            if (speed > 0.0025D) {
                propulsionPrimed = true;
                reversedForCurrentStop = false;
                lastRailSpeed = speed;
                rememberPropulsionDirection(getDeltaMovement());
            } else if (horizontalCollision && !entityCollisionStop
                    && lastPropulsionDirection.lengthSqr() > 1.0E-12D) {
                BlockPos railPos = getCurrentBlockPosOrRailBelow();
                BlockState railState = level().getBlockState(railPos);
                RailShape railShape = railState.getBlock() instanceof BaseRailBlock rail
                        ? railState.getValue(rail.getShapeProperty()) : RailShape.NORTH_SOUTH;
                boolean continuation = hasRailContinuationFrom(railPos, railShape, lastPropulsionDirection);
                if (continuation) {
                    
                    
                    horizontalCollision = false;
                    verticalCollision = false;
                    double restore = Math.min(SOLARITE_MAX_SPEED, Math.max(lastRailSpeed, STOPPED_SPEED));
                    setDeltaMovement(lastPropulsionDirection.x * restore, 0.0D, lastPropulsionDirection.z * restore);
                    propulsionPrimed = true;
                    reversedForCurrentStop = false;
                } else if (propulsionPrimed && !reversedForCurrentStop) {
                    
                    Vec3 reverse = lastPropulsionDirection.scale(-1.0D);
                    double launch = Math.min(SOLARITE_MAX_SPEED, Math.max(STOPPED_SPEED, targetSpeed()));
                    setDeltaMovement(reverse.x * launch, 0.0D, reverse.z * launch);
                    reversedForCurrentStop = true;
                }
            }

            tickBatterySystem(speed);
            entityCollisionStop = false;
        }
    }

    private BlockState getCurrentBlockPosOrRailBelowState() {
        return level().getBlockState(getCurrentBlockPosOrRailBelow());
    }

    private record RailExit(int x, int y, int z) {}

    private static RailExit[] entityRailExits(RailShape shape) {
        return switch (shape) {
            case NORTH_SOUTH -> new RailExit[] { new RailExit(0,0,-1), new RailExit(0,0,1) };
            case EAST_WEST -> new RailExit[] { new RailExit(-1,0,0), new RailExit(1,0,0) };
            case ASCENDING_EAST -> new RailExit[] { new RailExit(-1,-1,0), new RailExit(1,0,0) };
            case ASCENDING_WEST -> new RailExit[] { new RailExit(-1,0,0), new RailExit(1,-1,0) };
            case ASCENDING_NORTH -> new RailExit[] { new RailExit(0,0,-1), new RailExit(0,-1,1) };
            case ASCENDING_SOUTH -> new RailExit[] { new RailExit(0,-1,-1), new RailExit(0,0,1) };
            case SOUTH_EAST -> new RailExit[] { new RailExit(0,0,1), new RailExit(1,0,0) };
            case SOUTH_WEST -> new RailExit[] { new RailExit(0,0,1), new RailExit(-1,0,0) };
            case NORTH_WEST -> new RailExit[] { new RailExit(0,0,-1), new RailExit(-1,0,0) };
            case NORTH_EAST -> new RailExit[] { new RailExit(0,0,-1), new RailExit(1,0,0) };
        };
    }

    private boolean hasRailContinuationFrom(BlockPos pos, RailShape shape, Vec3 direction) {
        Vec3 travel = new Vec3(direction.x, 0.0D, direction.z);
        if (travel.lengthSqr() < 1.0E-12D) return false;
        travel = travel.normalize();

        BlockPos actual = BlockPos.containing(getX(), getY(), getZ());
        BlockPos[] candidates = { pos, pos.below(), pos.above(), actual, actual.below(), actual.above() };
        double bestScore = 0.15D;
        for (BlockPos candidate : candidates) {
            BlockState state = level().getBlockState(candidate);
            if (!BaseRailBlock.isRail(state)) continue;

            RailShape candidateShape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
            for (RailExit exit : entityRailExits(candidateShape)) {
                Vec3 exitDirection = new Vec3(exit.x(), 0.0D, exit.z());
                if (exitDirection.lengthSqr() < 1.0E-12D) continue;
                double alignment = travel.dot(exitDirection.normalize());
                if (alignment <= bestScore) continue;

                BlockPos next = candidate.offset(exit.x(), exit.y(), exit.z());
                BlockState nextState = level().getBlockState(next);
                if (!BaseRailBlock.isRail(nextState)) continue;

                RailShape nextShape = nextState.getValue(
                        ((BaseRailBlock) nextState.getBlock()).getShapeProperty());
                int backX = -exit.x();
                int backY = -exit.y();
                int backZ = -exit.z();
                for (RailExit nextExit : entityRailExits(nextShape)) {
                    if (nextExit.x() == backX && nextExit.y() == backY && nextExit.z() == backZ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    

    



    private void updateSpeedController() {
        if (level().isClientSide() || requestedDirection == 0) return;
        if (isOnSolariteRailActive()) return;

        float current = getCruiseSpeed();
        float next = current + requestedDirection * CONTROLLER_RATE;
        next = Math.max(0.0F, Math.min((float) SOLARITE_MAX_SPEED, next));
        if (Math.abs(next - current) > 1.0E-6F) {
            entityData.set(DATA_CRUISE_SPEED, next);
        }
    }

    




    public Vec3 applyPropulsion(Vec3 movement, BlockPos pos, BlockState state, RailShape shape) {
        double speed = movement.horizontalDistance();
        double target = Math.min(targetSpeed(), getMaxSpeed());

        if (speed > STOPPED_SPEED) {
            rememberPropulsionDirection(new Vec3(movement.x / speed, 0.0D, movement.z / speed));
            
            
            launchRequestDirection = 0;
        }

        if (!hasPropulsionPower(speed)) return movement;

        if (speed <= STOPPED_SPEED) {
            
            if (!hasRequestedDirection() || target <= 0.0D) return movement;
            Vec3 launchDir = getInputRailDirectionForLaunch();
            if (launchDir.lengthSqr() < 1.0E-12D) return movement;

            
            
            return new Vec3(launchDir.x * target, movement.y, launchDir.z * target);
        }

        
        
        
        if (speed > target + 1.0E-9D) {
            double braking = 0.12D;
            double newSpeed = Math.max(target, speed - braking);
            double scale = newSpeed / speed;
            return new Vec3(movement.x * scale, movement.y, movement.z * scale);
        }
        if (speed >= target - 1.0E-9D) return movement;
        double acceleration = isOnSolariteRailActive() ? 0.10D : 0.08D;
        double newSpeed = Math.min(target, speed + acceleration);
        double scale = newSpeed / speed;
        return new Vec3(movement.x * scale, movement.y, movement.z * scale);
    }

    

    



    public void syncToTrackers(Consumer<Packet<?>> broadcast, int serverEntityTick) {
        Vec3 pos = position();
        Vec3 movement = getDeltaMovement();
        boolean reset = false;
        List<SolariteMinecartBehavior.MinecartStep> out;
        List<SolariteMinecartBehavior.MinecartStep> queue = behavior.lerpSteps;
        if (lastNetPos != null && lastNetPos.distanceToSqr(pos) > TELEPORT_RESET_DISTANCE_SQR) {
            reset = true;
            queue.clear();
            out = List.of(new SolariteMinecartBehavior.MinecartStep(pos, movement, getYRot(), getXRot(), 1.0F));
        } else if (!queue.isEmpty()) {
            out = List.copyOf(queue);
            queue.clear();
        } else {
            boolean changed = lastNetPos == null
                    || lastNetPos.distanceToSqr(pos) > 5.8E-11D
                    || lastNetMovement.distanceToSqr(movement) > 1.0E-7D;
            if (!changed && serverEntityTick % 60 != 0) return;
            out = List.of(new SolariteMinecartBehavior.MinecartStep(pos, movement, getYRot(), getXRot(), 1.0F));
        }
        lastNetPos = pos;
        lastNetMovement = movement;
        broadcast.accept(new ClientboundCustomPayloadPacket(new SolariteCartStepsPacket(getId(), out, reset)));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag output) {
        super.addAdditionalSaveData(output);
        output.putFloat("SolariteBattery", getBatteryLevel());
        output.putFloat("SolariteCruiseSpeed", getCruiseSpeed());
        output.putBoolean("FlippedRotation", isFlipped());
        output.putBoolean("HasTicked", firstTick);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag input) {
        super.readAdditionalSaveData(input);
        setFlipped(input.getBoolean("FlippedRotation"));
        firstTick = input.getBoolean("HasTicked");
        entityData.set(DATA_BATTERY,
                Math.max(0F, Math.min(1F, NbtCompat.getFloatOr(input, "SolariteBattery", 1F))));
        entityData.set(DATA_CRUISE_SPEED,
                Math.max(0F, Math.min((float)SOLARITE_MAX_SPEED,
                        NbtCompat.getFloatOr(input, "SolariteCruiseSpeed", DEFAULT_CRUISE_SPEED))));
    }

    public boolean hasBatteryPower() { return getBatteryLevel() > 0F; }
}
