package com.neutrinodust.useful_ores.minecart;

import com.mojang.datafixers.util.Pair;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;















public final class SolariteMinecartBehavior {
    
    public static final int POS_ROT_LERP_TICKS = 3;
    public static final double ON_RAIL_Y_OFFSET = 0.1D;
    public static final double OPPOSING_SLOPES_REST_AT_SPEED_THRESHOLD = 0.005D;
    private static final double EPS = 1.0E-5F;
    private static final int MAX_QUEUED_STEPS = 64;

    private final SolariteBatteryMinecartEntity cart;
    @Nullable
    private StepPartialTicks cacheIndexAlpha;
    private int cachedLerpDelay;
    private float cachedPartialTick;
    private int lerpDelay = 0;
    public final List<MinecartStep> lerpSteps = new LinkedList<>();
    public final List<MinecartStep> currentLerpSteps = new LinkedList<>();
    public double currentLerpStepsTotalWeight = 0.0D;
    public MinecartStep oldLerp = MinecartStep.ZERO;

    public SolariteMinecartBehavior(SolariteBatteryMinecartEntity cart) {
        this.cart = cart;
    }

    

    public record MinecartStep(Vec3 position, Vec3 movement, float yRot, float xRot, float weight) {
        public static final MinecartStep ZERO = new MinecartStep(Vec3.ZERO, Vec3.ZERO, 0.0F, 0.0F, 0.0F);

        public void write(FriendlyByteBuf buf) {
            buf.writeDouble(position.x); buf.writeDouble(position.y); buf.writeDouble(position.z);
            buf.writeDouble(movement.x); buf.writeDouble(movement.y); buf.writeDouble(movement.z);
            buf.writeFloat(yRot); buf.writeFloat(xRot); buf.writeFloat(weight);
        }

        public static MinecartStep read(FriendlyByteBuf buf) {
            Vec3 p = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            Vec3 m = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            return new MinecartStep(p, m, buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    }

    private record StepPartialTicks(float partialTicksInStep, MinecartStep currentStep, MinecartStep previousStep) {}

    private static final class TrackIteration {
        double movementLeft = 0.0D;
        boolean firstIteration = true;
        boolean hasGainedSlopeSpeed = false;
        boolean hasHalted = false;

        boolean shouldIterate() {
            return firstIteration || movementLeft > EPS;
        }
    }

    

    private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = buildExits();

    private static Map<RailShape, Pair<Vec3i, Vec3i>> buildExits() {
        Vec3i west = new Vec3i(-1, 0, 0), east = new Vec3i(1, 0, 0);
        Vec3i north = new Vec3i(0, 0, -1), south = new Vec3i(0, 0, 1);
        Vec3i westDown = new Vec3i(-1, -1, 0), eastDown = new Vec3i(1, -1, 0);
        Vec3i northDown = new Vec3i(0, -1, -1), southDown = new Vec3i(0, -1, 1);
        Map<RailShape, Pair<Vec3i, Vec3i>> m = new EnumMap<>(RailShape.class);
        m.put(RailShape.NORTH_SOUTH, Pair.of(north, south));
        m.put(RailShape.EAST_WEST, Pair.of(west, east));
        m.put(RailShape.ASCENDING_EAST, Pair.of(westDown, east));
        m.put(RailShape.ASCENDING_WEST, Pair.of(west, eastDown));
        m.put(RailShape.ASCENDING_NORTH, Pair.of(north, southDown));
        m.put(RailShape.ASCENDING_SOUTH, Pair.of(northDown, south));
        m.put(RailShape.SOUTH_EAST, Pair.of(south, east));
        m.put(RailShape.SOUTH_WEST, Pair.of(south, west));
        m.put(RailShape.NORTH_WEST, Pair.of(north, west));
        m.put(RailShape.NORTH_EAST, Pair.of(north, east));
        return m;
    }

    private static Pair<Vec3i, Vec3i> exits(RailShape shape) {
        return EXITS.get(shape);
    }

    private static Vec3 lerpVec(float t, Vec3 a, Vec3 b) {
        return new Vec3(Mth.lerp((double) t, a.x, b.x), Mth.lerp((double) t, a.y, b.y), Mth.lerp((double) t, a.z, b.z));
    }

    private static Vec3 horizontal(Vec3 v) {
        return new Vec3(v.x, 0.0D, v.z);
    }

    private static Vec3 bottomCenter(BlockPos p) {
        return new Vec3(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D);
    }

    private static boolean isRail(BlockState state) {
        return BaseRailBlock.isRail(state);
    }

    

    public void tick() {
        if (cart.level() instanceof ServerLevel level) {
            BlockPos pos = cart.getCurrentBlockPosOrRailBelow();
            BlockState state = level.getBlockState(pos);
            if (cart.isFirstTick()) {
                cart.setOnRails(isRail(state));
                adjustToRails(pos, state, true);
            }
            cart.applyGravity();
            moveAlongTrack(level);
        } else {
            lerpClientPositionAndRotation();
            cart.setOnRails(isRail(cart.level().getBlockState(cart.blockPosition())));
        }
    }

    

    
    public void receiveSteps(List<MinecartStep> steps, boolean reset) {
        if (steps.isEmpty()) return;
        if (reset) {
            lerpSteps.clear();
            currentLerpSteps.clear();
            currentLerpStepsTotalWeight = 0.0D;
            lerpDelay = 0;
            cacheIndexAlpha = null;
            MinecartStep s = steps.get(steps.size() - 1);
            cart.setPos(s.position.x, s.position.y, s.position.z);
            cart.setDeltaMovement(s.movement);
            cart.setYRot(s.yRot);
            cart.setXRot(s.xRot);
            cart.setOldPosAndRot();
            oldLerp = s;
            return;
        }
        lerpSteps.addAll(steps);
        while (lerpSteps.size() > MAX_QUEUED_STEPS) lerpSteps.remove(0);
    }

    private void lerpClientPositionAndRotation() {
        
        
        
        
        if (--lerpDelay <= 0) {
            setOldLerpValues();
            currentLerpSteps.clear();
            if (!lerpSteps.isEmpty()) {
                currentLerpSteps.addAll(lerpSteps);
                lerpSteps.clear();
                currentLerpStepsTotalWeight = 0.0D;
                for (MinecartStep s : currentLerpSteps) currentLerpStepsTotalWeight += s.weight;
                lerpDelay = currentLerpStepsTotalWeight == 0.0D ? 0 : POS_ROT_LERP_TICKS;
                cacheIndexAlpha = null;
            } else {
                cacheIndexAlpha = null;
            }
        }

        if (!currentLerpSteps.isEmpty()) {
            
            
            
            Vec3 position = getCartLerpPosition(1.0F);
            Vec3 movement = getCartLerpMovements(1.0F);
            float xRot = getCartLerpXRot(1.0F);
            float yRot = getCartLerpYRot(1.0F);
            cart.setPos(position.x, position.y, position.z);
            cart.setDeltaMovement(movement);
            cart.setXRot(xRot);
            cart.setYRot(yRot);
        }
    }

    public void setOldLerpValues() {
        oldLerp = new MinecartStep(cart.position(), cart.getDeltaMovement(), cart.getYRot(), cart.getXRot(), 0.0F);
    }

    public boolean cartHasPosRotLerp() {
        return !currentLerpSteps.isEmpty();
    }

    public float getCartLerpXRot(float partialTick) {
        StepPartialTicks s = getCurrentLerpStep(partialTick);
        return Mth.rotLerp(s.partialTicksInStep, s.previousStep.xRot, s.currentStep.xRot);
    }

    public float getCartLerpYRot(float partialTick) {
        StepPartialTicks s = getCurrentLerpStep(partialTick);
        return Mth.rotLerp(s.partialTicksInStep, s.previousStep.yRot, s.currentStep.yRot);
    }

    public Vec3 getCartLerpPosition(float partialTick) {
        StepPartialTicks s = getCurrentLerpStep(partialTick);
        return lerpVec(s.partialTicksInStep, s.previousStep.position, s.currentStep.position);
    }

    public Vec3 getCartLerpMovements(float partialTick) {
        StepPartialTicks s = getCurrentLerpStep(partialTick);
        return lerpVec(s.partialTicksInStep, s.previousStep.movement, s.currentStep.movement);
    }

    private StepPartialTicks getCurrentLerpStep(float partialTick) {
        if (partialTick == cachedPartialTick && lerpDelay == cachedLerpDelay && cacheIndexAlpha != null) {
            return cacheIndexAlpha;
        }
        float lerpProgress = (POS_ROT_LERP_TICKS - lerpDelay + partialTick) / (float) POS_ROT_LERP_TICKS;
        float accumulated = 0.0F;
        float partialInStep = 1.0F;
        boolean found = false;
        int index;
        for (index = 0; index < currentLerpSteps.size(); index++) {
            float w = currentLerpSteps.get(index).weight;
            if (w > 0.0F) {
                accumulated += w;
                if (accumulated >= currentLerpStepsTotalWeight * lerpProgress) {
                    float before = accumulated - w;
                    partialInStep = (float) ((lerpProgress * currentLerpStepsTotalWeight - before) / w);
                    found = true;
                    break;
                }
            }
        }
        if (!found) index = currentLerpSteps.size() - 1;
        MinecartStep current = currentLerpSteps.get(index);
        MinecartStep previous = index > 0 ? currentLerpSteps.get(index - 1) : oldLerp;
        cacheIndexAlpha = new StepPartialTicks(partialInStep, current, previous);
        cachedLerpDelay = lerpDelay;
        cachedPartialTick = partialTick;
        return cacheIndexAlpha;
    }

    

    
    public void adjustToRails(BlockPos pos, BlockState state, boolean disableSmoothing) {
        if (!isRail(state)) return;
        RailShape shape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
        Pair<Vec3i, Vec3i> ex = exits(shape);
        Vec3 a = new Vec3(ex.getFirst().getX(), ex.getFirst().getY(), ex.getFirst().getZ()).scale(0.5D);
        Vec3 b = new Vec3(ex.getSecond().getX(), ex.getSecond().getY(), ex.getSecond().getZ()).scale(0.5D);
        Vec3 aH = horizontal(a);
        Vec3 bH = horizontal(b);
        Vec3 movementForOrientation = cart.getDeltaMovement();
        
        
        if ((movementForOrientation.length() > EPS
                && movementForOrientation.dot(aH) < movementForOrientation.dot(bH))
                || isDescendingTowards(bH, shape)) {
            Vec3 t = aH; aH = bH; bH = t;
        }
        float yaw = 180.0F - (float) (Math.atan2(aH.z, aH.x) * 180.0D / Math.PI);
        yaw += cart.isFlipped() ? 180.0F : 0.0F;
        Vec3 cur = cart.position();
        boolean diagonal = a.x != b.x && a.z != b.z;
        Vec3 target;
        if (diagonal) {
            Vec3 ab = b.subtract(a);
            Vec3 toCart = cur.subtract(bottomCenter(pos)).subtract(a);
            Vec3 proj = ab.scale(ab.dot(toCart) / ab.dot(ab));
            target = bottomCenter(pos).add(a).add(proj);
            yaw = 180.0F - (float) (Math.atan2(proj.z, proj.x) * 180.0D / Math.PI);
            yaw += cart.isFlipped() ? 180.0F : 0.0F;
        } else {
            boolean xDiffers = a.subtract(b).x != 0.0D;
            boolean zDiffers = a.subtract(b).z != 0.0D;
            target = new Vec3(zDiffers ? pos.getX() + 0.5D : cur.x, pos.getY(), xDiffers ? pos.getZ() + 0.5D : cur.z);
        }
        Vec3 offset = target.subtract(cur);
        Vec3 np = cur.add(offset);
        cart.setPos(np.x, np.y, np.z);
        float pitch = 0.0F;
        boolean slope = a.y != b.y;
        if (slope) {
            Vec3 end = bottomCenter(pos).add(bH);
            double dist = end.distanceTo(cart.position());
            cart.setPos(cart.getX(), cart.getY() + dist + ON_RAIL_Y_OFFSET, cart.getZ());
            pitch = cart.isFlipped() ? 45.0F : -45.0F;
        } else {
            cart.setPos(cart.getX(), cart.getY() + ON_RAIL_Y_OFFSET, cart.getZ());
        }
        setRotation(yaw, pitch);
        double moved = cur.distanceTo(cart.position());
        if (moved > 0.0D) {
            lerpSteps.add(new MinecartStep(cart.position(), cart.getDeltaMovement(), cart.getYRot(), cart.getXRot(),
                    disableSmoothing ? 0.0F : (float) moved));
        }
    }

    private void setRotation(float yRot, float xRot) {
        double diff = Math.abs(yRot - cart.getYRot());
        if (diff >= 175.0D && diff <= 185.0D) {
            cart.setFlipped(!cart.isFlipped());
            yRot -= 180.0F;
            xRot *= -1.0F;
        }
        xRot = Math.max(-45.0F, Math.min(45.0F, xRot));
        cart.setXRot(xRot % 360.0F);
        cart.setYRot(yRot % 360.0F);
    }

    public void moveAlongTrack(ServerLevel level) {
        for (TrackIteration it = new TrackIteration(); it.shouldIterate() && cart.isAlive(); it.firstIteration = false) {
            Vec3 startMovement = cart.getDeltaMovement();
            BlockPos pos = cart.getCurrentBlockPosOrRailBelow();
            BlockState state = level.getBlockState(pos);
            boolean onRail = isRail(state);
            if (cart.isOnRails() != onRail) {
                cart.setOnRails(onRail);
                adjustToRails(pos, state, false);
            }
            if (onRail) {
                cart.resetFallDistance();
                cart.setOldPosAndRot();
                if (state.is(Blocks.ACTIVATOR_RAIL)) {
                    cart.activateMinecart(pos.getX(), pos.getY(), pos.getZ(), state.getValue(PoweredRailBlock.POWERED));
                }
                RailShape shape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
                Vec3 newSpeed = calculateTrackSpeed(level, horizontal(startMovement), it, pos, state, shape);
                if (it.firstIteration) {
                    it.movementLeft = newSpeed.horizontalDistance();
                } else {
                    it.movementLeft += newSpeed.horizontalDistance() - startMovement.horizontalDistance();
                }
                cart.setDeltaMovement(newSpeed);
                it.movementLeft = makeStepAlongTrack(level, pos, shape, it.movementLeft);
            } else {
                cart.comeOffTrack();
                it.movementLeft = 0.0D;
            }

            Vec3 newPos = cart.position();
            Vec3 delta = newPos.subtract(new Vec3(cart.xo, cart.yo, cart.zo));
            double dist = delta.length();
            if (dist > EPS) {
                if (!(delta.horizontalDistanceSqr() > EPS)) {
                    if (!cart.isOnRails()) {
                        cart.setXRot(cart.onGround() ? 0.0F : Mth.rotLerp(0.2F, cart.getXRot(), 0.0F));
                    }
                } else {
                    float yaw = 180.0F - (float) (Math.atan2(delta.z, delta.x) * 180.0D / Math.PI);
                    float pitch = cart.onGround() && !cart.isOnRails()
                            ? 0.0F
                            : 90.0F - (float) (Math.atan2(delta.horizontalDistance(), delta.y) * 180.0D / Math.PI);
                    yaw += cart.isFlipped() ? 180.0F : 0.0F;
                    pitch *= cart.isFlipped() ? -1.0F : 1.0F;
                    setRotation(yaw, pitch);
                }
                lerpSteps.add(new MinecartStep(newPos, cart.getDeltaMovement(), cart.getYRot(), cart.getXRot(),
                        (float) Math.min(dist, cart.getMaxSpeed())));
            } else if (startMovement.horizontalDistanceSqr() > 0.0D) {
                lerpSteps.add(new MinecartStep(newPos, cart.getDeltaMovement(), cart.getYRot(), cart.getXRot(), 1.0F));
            }
            if (dist > EPS || it.firstIteration) {
                cart.checkInsideBlocks();
                cart.checkInsideBlocks();
            }
        }
        while (lerpSteps.size() > MAX_QUEUED_STEPS) lerpSteps.remove(0);
    }

    private Vec3 calculateTrackSpeed(ServerLevel level, Vec3 movement, TrackIteration it, BlockPos pos, BlockState state,
                                     RailShape shape) {
        Vec3 speed = movement;
        if (!it.hasGainedSlopeSpeed) {
            Vec3 slope = calculateSlopeSpeed(speed, shape);
            if (slope.horizontalDistanceSqr() != speed.horizontalDistanceSqr()) {
                it.hasGainedSlopeSpeed = true;
                speed = slope;
            }
        }
        if (it.firstIteration) {
            Vec3 input = calculatePlayerInputSpeed(speed);
            if (input.horizontalDistanceSqr() != speed.horizontalDistanceSqr()) {
                it.hasHalted = true;
                speed = input;
            }
        }
        if (!it.hasHalted) {
            Vec3 halted = calculateHaltTrackSpeed(speed, state);
            if (halted.horizontalDistanceSqr() != speed.horizontalDistanceSqr()) {
                it.hasHalted = true;
                speed = halted;
            }
        }
        if (it.firstIteration) {
            speed = applyNaturalSlowdown(speed);
            if (speed.lengthSqr() > 0.0D) {
                double max = Math.min(speed.length(), cart.getMaxSpeed());
                speed = speed.normalize().scale(max);
            }
            
            if (!it.hasHalted) {
                speed = cart.applyPropulsion(speed, pos, state, shape);

                
                
                
                
                
                
                if (shape.isAscending() && cart.hasPropulsionPowerForRail()) {
                    double target = Math.min(cart.targetSpeed(), cart.getMaxSpeed());
                    double horizontal = speed.horizontalDistance();
                    if (target > horizontal + 1.0E-9D && horizontal > 1.0E-9D) {
                        double scale = target / horizontal;
                        speed = new Vec3(speed.x * scale, speed.y, speed.z * scale);
                    }
                }
            }
        }
        return speed;
    }

    private Vec3 applyNaturalSlowdown(Vec3 movement) {
        double f = cart.isVehicle() ? 0.997D : 0.975D;
        Vec3 v = movement.multiply(f, 0.0D, f);
        if (cart.isInWater()) v = v.scale(0.95F);
        return v;
    }

    private Vec3 calculateSlopeSpeed(Vec3 movement, RailShape shape) {
        double slope = Math.max(0.0078125D, movement.horizontalDistance() * 0.02D);
        if (cart.isInWater()) slope *= 0.2D;
        return switch (shape) {
            case ASCENDING_EAST -> movement.add(-slope, 0.0D, 0.0D);
            case ASCENDING_WEST -> movement.add(slope, 0.0D, 0.0D);
            case ASCENDING_NORTH -> movement.add(0.0D, 0.0D, slope);
            case ASCENDING_SOUTH -> movement.add(0.0D, 0.0D, -slope);
            default -> movement;
        };
    }

    
    private Vec3 calculatePlayerInputSpeed(Vec3 speed) {
        if (cart.getFirstPassenger() instanceof ServerPlayer player) {
            float xx = player.xxa == 0.0F ? 0.0F : Math.signum(player.xxa);
            float zz = player.zza == 0.0F ? 0.0F : Math.signum(player.zza);
            if ((xx != 0.0F || zz != 0.0F) && speed.horizontalDistanceSqr() < 0.01D) {
                float rad = player.getYRot() * ((float) Math.PI / 180F);
                float sin = Mth.sin(rad);
                float cos = Mth.cos(rad);
                Vec3 intent = new Vec3(xx * cos - zz * sin, 0.0D, zz * cos + xx * sin);
                if (intent.lengthSqr() > 0.0D) {
                    return speed.add(intent.normalize().scale(0.001D));
                }
            }
        }
        return speed;
    }

    private Vec3 calculateHaltTrackSpeed(Vec3 speed, BlockState state) {
        if (state.is(Blocks.POWERED_RAIL) && !state.getValue(PoweredRailBlock.POWERED)) {
            return speed.length() < 0.03D ? Vec3.ZERO : speed.scale(0.5D);
        }
        return speed;
    }

    private double makeStepAlongTrack(ServerLevel level, BlockPos pos, RailShape shape, double movementLeft) {
        
        
        if (movementLeft < EPS) return 0.0D;

        Vec3 startPos = cart.position();
        Pair<Vec3i, Vec3i> exitPair = exits(shape);
        Vec3i firstExit = exitPair.getFirst();
        Vec3i secondExit = exitPair.getSecond();

        Vec3 movement = horizontal(cart.getDeltaMovement());
        if (movement.horizontalDistance() < EPS) {
            cart.setDeltaMovement(Vec3.ZERO);
            return 0.0D;
        }

        boolean slope = firstExit.getY() != secondExit.getY();
        Vec3 second = horizontal(new Vec3(secondExit.getX(), secondExit.getY(), secondExit.getZ()).scale(0.5D));
        Vec3 first = horizontal(new Vec3(firstExit.getX(), firstExit.getY(), firstExit.getZ()).scale(0.5D));

        if (movement.dot(first) < movement.dot(second)) {
            first = second;
        }

        Vec3 railTarget = bottomCenter(pos)
                .add(first)
                .add(0.0D, ON_RAIL_Y_OFFSET, 0.0D)
                .add(first.normalize().scale(1.0E-5D));

        if (slope && !isDescendingTowards(movement, shape)) {
            railTarget = railTarget.add(0.0D, 1.0D, 0.0D);
        }

        Vec3 railDelta = railTarget.subtract(cart.position());
        Vec3 railDirection = railDelta.normalize();
        double railHorizontal = railDirection.horizontalDistance();
        if (railHorizontal < EPS) {
            boolean endpointConnected = hasConnectedRailAtSelectedExit(level, pos, shape, movement)
                    || hasForwardRailContinuation(level, startPos, first);
            if (!endpointConnected) {
                cart.setDeltaMovement(Vec3.ZERO);
                return 0.0D;
            }

            Vec3 tangent = first.normalize();
            double tangentHorizontal = tangent.horizontalDistance();
            if (tangentHorizontal < EPS) {
                cart.setDeltaMovement(Vec3.ZERO);
                return 0.0D;
            }

            double handoff = Math.min(movementLeft, 1.0E-4D);
            cart.setPos(startPos.x + tangent.x * handoff,
                    startPos.y + tangent.y * handoff,
                    startPos.z + tangent.z * handoff);
            cart.setOnRails(true);
            cart.horizontalCollision = false;
            cart.verticalCollision = false;
            cart.setDeltaMovement(movement);
            pushAndPickupEntitiesOnRailKernel();
            return Math.max(0.0D, movementLeft - handoff);
        }

        
        movement = railDirection.scale(movement.horizontalDistance() / railHorizontal);
        
        
        
        
        cart.rememberPropulsionDirection(movement);

        Vec3 newPosition = startPos.add(
                movement.normalize().scale(movementLeft * (slope ? Mth.SQRT_OF_TWO : 1.0D))
        );

        if (startPos.distanceToSqr(railTarget) <= startPos.distanceToSqr(newPosition)) {
            
            
            
            movementLeft = railTarget.subtract(newPosition).horizontalDistance();
            newPosition = railTarget;
        } else {
            movementLeft = 0.0D;
        }

        Vec3 attemptedDelta = newPosition.subtract(startPos);
        boolean connectedExit = hasConnectedRailAtSelectedExit(level, pos, shape, movement);
        
        
        
        
        
        boolean destinationContinuation = hasForwardRailContinuation(level, newPosition, attemptedDelta);
        boolean railContinuation = connectedExit || destinationContinuation;
        double attemptedDistance = attemptedDelta.length();
        
        
        
        
        
        
        
        boolean useRailKernelPosition = railContinuation && attemptedDistance > EPS;
        if (useRailKernelPosition) {
            if (stopForRailKernelEntityCollision(level, startPos, newPosition)) {
                return 0.0D;
            }
            cart.setPos(newPosition.x, newPosition.y, newPosition.z);
            cart.setOnRails(true);
            cart.horizontalCollision = false;
            cart.verticalCollision = false;
            cart.setDeltaMovement(movement);
            
            
            
            
            pushAndPickupEntitiesOnRailKernel();
        } else {
            cart.move(MoverType.SELF, attemptedDelta);
        }

        
        
        
        
        
        if (railContinuation && cart.isOnRails()) {
            cart.horizontalCollision = false;
            cart.verticalCollision = false;
        }

        
        
        
        
        
        
        
        double actualDistance = cart.position().distanceTo(startPos);
        if (!useRailKernelPosition && attemptedDistance > EPS && actualDistance + EPS < attemptedDistance) {
            
            
            
            
            
            
            if (railContinuation) {
                if (stopForRailKernelEntityCollision(level, startPos, newPosition)) {
                    return 0.0D;
                }
                cart.setPos(newPosition.x, newPosition.y, newPosition.z);
                cart.setOnRails(true);
                cart.setDeltaMovement(movement);
                pushAndPickupEntitiesOnRailKernel();
                return movementLeft;
            }
            cart.setDeltaMovement(Vec3.ZERO);
            return 0.0D;
        }

        
        
        

        
        
        
        
        
        BlockState nextState = level.getBlockState(
                BlockPos.containing(newPosition.x, newPosition.y, newPosition.z));

        if (slope) {
            if (isRail(nextState)) {
                RailShape nextShape =
                        nextState.getValue(((BaseRailBlock) nextState.getBlock()).getShapeProperty());
                if (restAtVShape(shape, nextShape)) {
                    return 0.0D;
                }
            }

            
            
            
            
            double horizontalDistance = railTarget.subtract(cart.position()).horizontalDistance();
            double expectedY = railTarget.y
                    + (isDescendingTowards(movement, shape) ? horizontalDistance : -horizontalDistance);
            if (cart.getY() < expectedY) {
                cart.setPos(cart.getX(), expectedY, cart.getZ());
            }
        }

        if (!useRailKernelPosition && cart.position().distanceTo(startPos) < EPS && newPosition.distanceTo(startPos) > EPS) {
            
            
            
            
            
            
            if (railContinuation) {
                if (stopForRailKernelEntityCollision(level, startPos, newPosition)) {
                    return 0.0D;
                }
                cart.setPos(newPosition.x, newPosition.y, newPosition.z);
                cart.setOnRails(true);
                cart.setDeltaMovement(movement);
                pushAndPickupEntitiesOnRailKernel();
                return movementLeft;
            }
            cart.setDeltaMovement(Vec3.ZERO);
            return 0.0D;
        }

        
        cart.setDeltaMovement(movement);
        return movementLeft;
    }


    




    






    private boolean hasForwardRailContinuation(ServerLevel level, Vec3 destination, Vec3 attemptedDelta) {
        Vec3 horizontalDelta = horizontal(attemptedDelta);
        if (horizontalDelta.lengthSqr() < EPS * EPS) return false;

        BlockPos base = BlockPos.containing(destination.x, destination.y, destination.z);
        BlockPos[] candidates = {
                base,
                base.below(),
                base.above()
        };
        for (BlockPos railPos : candidates) {
            BlockState state = level.getBlockState(railPos);
            if (!isRail(state)) continue;
            RailShape nextShape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
            Pair<Vec3i, Vec3i> nextExits = exits(nextShape);
            Vec3i selected = selectExitForDirection(nextExits, horizontalDelta);
            BlockPos forward = railPos.offset(selected.getX(), selected.getY(), selected.getZ());
            if (isRail(level.getBlockState(forward))) {
                return true;
            }
        }
        return false;
    }

    private static Vec3i selectExitForDirection(Pair<Vec3i, Vec3i> pair, Vec3 direction) {
        Vec3 a = horizontal(new Vec3(pair.getFirst().getX(), pair.getFirst().getY(), pair.getFirst().getZ()));
        Vec3 b = horizontal(new Vec3(pair.getSecond().getX(), pair.getSecond().getY(), pair.getSecond().getZ()));
        return direction.dot(a) >= direction.dot(b) ? pair.getFirst() : pair.getSecond();
    }

    private boolean hasConnectedRailAtSelectedExit(ServerLevel level, BlockPos pos, RailShape shape, Vec3 movement) {
        Pair<Vec3i, Vec3i> pair = exits(shape);
        Vec3i a = pair.getFirst();
        Vec3i b = pair.getSecond();
        Vec3 ah = horizontal(new Vec3(a.getX(), a.getY(), a.getZ()));
        Vec3 bh = horizontal(new Vec3(b.getX(), b.getY(), b.getZ()));
        Vec3i selected = movement.dot(ah) >= movement.dot(bh) ? a : b;

        BlockPos nextPos = pos.offset(selected.getX(), selected.getY(), selected.getZ());
        BlockState nextState = level.getBlockState(nextPos);
        if (!isRail(nextState)) return false;

        RailShape nextShape = nextState.getValue(((BaseRailBlock) nextState.getBlock()).getShapeProperty());
        Pair<Vec3i, Vec3i> nextExits = exits(nextShape);
        Vec3i back = new Vec3i(-selected.getX(), -selected.getY(), -selected.getZ());
        return nextExits.getFirst().equals(back) || nextExits.getSecond().equals(back);
    }

    private boolean restAtVShape(RailShape current, RailShape next) {
        if (cart.getDeltaMovement().lengthSqr() < OPPOSING_SLOPES_REST_AT_SPEED_THRESHOLD
                && next.isAscending()
                && isDescendingTowards(cart.getDeltaMovement(), current)
                && !isDescendingTowards(cart.getDeltaMovement(), next)) {
            cart.setDeltaMovement(Vec3.ZERO);
            return true;
        }
        return false;
    }

    private static boolean isDescendingTowards(Vec3 movement, RailShape shape) {
        return switch (shape) {
            case ASCENDING_EAST -> movement.x < 0.0D;
            case ASCENDING_WEST -> movement.x > 0.0D;
            case ASCENDING_NORTH -> movement.z > 0.0D;
            case ASCENDING_SOUTH -> movement.z < 0.0D;
            default -> false;
        };
    }

    

    public boolean pushAndPickupEntities() {
        boolean pickedUp = pickupEntities(cart.getBoundingBox().inflate(0.2D, 0.0D, 0.2D));
        boolean pushed = pushEntities(cart.getBoundingBox().inflate(1.0E-7D));
        return pickedUp && !pushed;
    }

    




    private void pushAndPickupEntitiesOnRailKernel() {
        pickupEntities(cart.getBoundingBox().inflate(0.2D, 0.0D, 0.2D));
        pushEntities(cart.getBoundingBox().inflate(1.0E-7D));
    }

    
    private boolean stopForRailKernelEntityCollision(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        if (delta.lengthSqr() <= EPS * EPS) return false;

        AABB sweep = cart.getBoundingBox().move(start.subtract(cart.position()))
                .expandTowards(delta).inflate(0.05D);
        double halfWidth = cart.getBbWidth() * 0.5D;
        double halfHeight = cart.getBbHeight() * 0.5D;
        Entity hit = null;
        double hitT = Double.POSITIVE_INFINITY;
        for (Entity entity : level.getEntities(cart, sweep, EntitySelector.pushableBy(cart))) {
            if (!entity.isPushable() || cart.hasPassenger(entity)) continue;
            AABB expanded = entity.getBoundingBox().inflate(halfWidth, halfHeight, halfWidth);
            double t = segmentEntryT(expanded, start, end);
            if (t < hitT) {
                hitT = t;
                hit = entity;
            }
        }
        if (hit == null) return false;

        double safeT = Math.max(0.0D, hitT - 1.0E-4D);
        Vec3 stop = start.add(delta.scale(safeT));
        cart.setPos(stop.x, stop.y, stop.z);
        cart.setDeltaMovement(Vec3.ZERO);
        cart.horizontalCollision = true;
        cart.verticalCollision = false;
        cart.markEntityCollisionStop();
        pushEntities(cart.getBoundingBox().inflate(1.0E-4D));
        return true;
    }

    private static double segmentEntryT(AABB box, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double tMin = 0.0D;
        double tMax = 1.0D;
        double[] s = {start.x, start.y, start.z};
        double[] d = {delta.x, delta.y, delta.z};
        double[] min = {box.minX, box.minY, box.minZ};
        double[] max = {box.maxX, box.maxY, box.maxZ};
        for (int axis = 0; axis < 3; axis++) {
            if (Math.abs(d[axis]) < 1.0E-12D) {
                if (s[axis] < min[axis] || s[axis] > max[axis]) return Double.POSITIVE_INFINITY;
                continue;
            }
            double a = (min[axis] - s[axis]) / d[axis];
            double b = (max[axis] - s[axis]) / d[axis];
            if (a > b) {
                double swap = a;
                a = b;
                b = swap;
            }
            tMin = Math.max(tMin, a);
            tMax = Math.min(tMax, b);
            if (tMin > tMax) return Double.POSITIVE_INFINITY;
        }
        return tMin >= 0.0D && tMin <= 1.0D ? tMin : Double.POSITIVE_INFINITY;
    }

    private boolean pickupEntities(AABB box) {
        if (cart.getMinecartType() == AbstractMinecart.Type.RIDEABLE && !cart.isVehicle()) {
            List<Entity> list = cart.level().getEntities(cart, box, EntitySelector.pushableBy(cart));
            for (Entity e : list) {
                if (!(e instanceof Player) && !(e instanceof IronGolem) && !(e instanceof AbstractMinecart)
                        && !cart.isVehicle() && !e.isPassenger()) {
                    if (e.startRiding(cart)) return true;
                }
            }
        }
        return false;
    }

    private boolean pushEntities(AABB box) {
        boolean pushed = false;
        if (cart.getMinecartType() == AbstractMinecart.Type.RIDEABLE) {
            List<Entity> list = cart.level().getEntities(cart, box, EntitySelector.pushableBy(cart));
            for (Entity e : list) {
                if (e instanceof Player || e instanceof IronGolem || e instanceof AbstractMinecart
                        || cart.isVehicle() || e.isPassenger()) {
                    e.push(cart);
                    pushed = true;
                }
            }
        } else {
            for (Entity e : cart.level().getEntities(cart, box)) {
                if (!cart.hasPassenger(e) && e.isPushable() && e instanceof AbstractMinecart) {
                    e.push(cart);
                    pushed = true;
                }
            }
        }
        return pushed;
    }
}
