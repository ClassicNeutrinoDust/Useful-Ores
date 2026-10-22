package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.rail.SolariteRailBlock;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class MixinAbstractMinecartSolariteRailBoost {
    private static final double SOLARITE_MAX_SPEED = 1.6D; 
    private static final double SOLARITE_ACCELERATION = 0.10D; 
    private static final double EPS = 1.0E-7D;

    private static final double SLOPE_SAFE_SPEED = 0.6D;
    private static final int SLOPE_LOOKAHEAD = 4;

    @Inject(method = "moveAlongTrack", at = @At("HEAD"))
    private void usefulOres$limitSpeedBeforeClimb(BlockPos pos, BlockState state, CallbackInfo ci) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (!(cart.level() instanceof net.minecraft.server.level.ServerLevel)) return;

        usefulOres$rampLimited = false;
        Vec3 velocity = cart.getDeltaMovement();
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (speed <= SLOPE_SAFE_SPEED) {
            usefulOres$rampLimited = usefulOres$rampAhead(cart.level(), pos, state, velocity);
            return;
        }
        if (!usefulOres$rampAhead(cart.level(), pos, state, velocity)) return;

        double scale = SLOPE_SAFE_SPEED / speed;
        cart.setDeltaMovement(velocity.x * scale, velocity.y, velocity.z * scale);
        usefulOres$rampLimited = true;
    }

    private boolean usefulOres$rampLimited;

    private static boolean usefulOres$rampAhead(Level level, BlockPos start, BlockState startState, Vec3 velocity) {
        Direction dir = Math.abs(velocity.x) >= Math.abs(velocity.z)
                ? (velocity.x > 0.0D ? Direction.EAST : Direction.WEST)
                : (velocity.z > 0.0D ? Direction.SOUTH : Direction.NORTH);
        BlockPos cur = start;
        BlockState curState = startState;
        for (int i = 0; i <= SLOPE_LOOKAHEAD; i++) {
            if (BaseRailBlock.isRail(curState)) {
                RailShape shape = curState.getValue(((BaseRailBlock) curState.getBlock()).getShapeProperty());
                if (usefulOres$ascendsToward(shape, dir)) return true;
            }
            BlockPos next = cur.relative(dir);
            BlockPos found = null;
            for (BlockPos candidate : new BlockPos[] {next, next.above(), next.below()}) {
                if (BaseRailBlock.isRail(level.getBlockState(candidate))) {
                    found = candidate;
                    break;
                }
            }
            if (found == null) return false;
            cur = found;
            curState = level.getBlockState(found);
        }
        return false;
    }

    private static boolean usefulOres$ascendsToward(RailShape shape, Direction dir) {
        return switch (shape) {
            case ASCENDING_EAST -> dir == Direction.EAST;
            case ASCENDING_WEST -> dir == Direction.WEST;
            case ASCENDING_NORTH -> dir == Direction.NORTH;
            case ASCENDING_SOUTH -> dir == Direction.SOUTH;
            default -> false;
        };
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    private void usefulOres$solariteMaxSpeed(CallbackInfoReturnable<Double> cir) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (usefulOres$rampLimited) return;
        if (usefulOres$isActiveSolariteRail(cart)) {
            cir.setReturnValue(SOLARITE_MAX_SPEED);
        }
    }

    @Inject(method = "getCurrentCartSpeedCapOnRail", at = @At("HEAD"), cancellable = true)
    private void usefulOres$solariteRailSpeedCap(CallbackInfoReturnable<Float> cir) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (usefulOres$rampLimited) return;
        if (usefulOres$isActiveSolariteRail(cart)) {
            cir.setReturnValue((float) SOLARITE_MAX_SPEED);
        }
    }

    @Inject(method = "moveAlongTrack", at = @At("TAIL"))
    private void usefulOres$solariteDayBoost(BlockPos pos, BlockState state, CallbackInfo ci) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (!(cart.level() instanceof net.minecraft.server.level.ServerLevel)) return;
        if (!(state.getBlock() instanceof SolariteRailBlock)) return;

        if (!SolariteRailBlock.shouldBeActive(cart.level(), pos)) return;
        if (usefulOres$rampLimited) {
            Vec3 v = cart.getDeltaMovement();
            double sp = Math.sqrt(v.x * v.x + v.z * v.z);
            if (sp > EPS && sp < SLOPE_SAFE_SPEED) {
                double k = SLOPE_SAFE_SPEED / sp;
                cart.setDeltaMovement(v.x * k, v.y, v.z * k);
            }
            return;
        }

        Vec3 velocity = cart.getDeltaMovement();
        Vec3 horizontal = new Vec3(velocity.x, 0.0D, velocity.z);
        double speed = horizontal.length();

        if (speed <= EPS) return;

        double boostedSpeed = Math.min(SOLARITE_MAX_SPEED, speed + SOLARITE_ACCELERATION);
        if (boostedSpeed <= speed + EPS) return;

        double scale = boostedSpeed / speed;
        cart.setDeltaMovement(velocity.x * scale, velocity.y, velocity.z * scale);
    }

    private static boolean usefulOres$isActiveSolariteRail(AbstractMinecart cart) {
        Level level = cart.level();
        BlockPos pos = usefulOres$railPos(cart);
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof SolariteRailBlock
                && SolariteRailBlock.shouldBeActive(level, pos);
    }

    private static BlockPos usefulOres$railPos(AbstractMinecart cart) {
        int x = net.minecraft.util.Mth.floor(cart.getX());
        int z = net.minecraft.util.Mth.floor(cart.getZ());
        double below = cart.getY() - 0.1D - 1.0E-5D;
        BlockPos candidate = BlockPos.containing(x, below, z);
        if (net.minecraft.world.level.block.BaseRailBlock.isRail(cart.level().getBlockState(candidate))) {
            return candidate;
        }
        return BlockPos.containing(cart.getX(), cart.getY(), cart.getZ());
    }

}
