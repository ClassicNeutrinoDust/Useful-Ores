package com.neutrinodust.useful_ores.mixin;

import com.mojang.datafixers.util.Pair;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.OldMinecartBehavior;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OldMinecartBehavior.class)
public abstract class MixinOldMinecartBehaviorSolariteHighSpeed {
    private AbstractMinecart usefulOres$minecart() {
        return ((MixinMinecartBehaviorAccessor) (Object) this).usefulOres$getMinecart();
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    private void usefulOres$solariteRailMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
        AbstractMinecart minecart = usefulOres$minecart();
        if (!(minecart instanceof SolariteBatteryMinecartEntity) && isActiveSolariteRail(minecart, level)) {
            cir.setReturnValue(1.6D);
        }
    }

    @Inject(method = "moveAlongTrack", at = @At("HEAD"), cancellable = true)
    private void usefulOres$highSpeedTrackTraversal(ServerLevel level, CallbackInfo ci) {
        AbstractMinecart minecart = usefulOres$minecart();
        BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BaseRailBlock)) return;

        boolean solariteRail = isActiveSolariteRail(minecart, level);
        double speed = minecart.getDeltaMovement().horizontalDistance();

        if (!(minecart instanceof SolariteBatteryMinecartEntity) && solariteRail) {
            if (speed < 1.0e-7) {
                Vec3 start = stationaryStartDirection(minecart, state, pos);
                if (start.lengthSqr() > 1.0e-12) {
                    minecart.setDeltaMovement(start.scale(0.02));
                }
                return;
            }

            double accelerated = Math.min(1.6D, speed + 0.10D);
            if (accelerated > speed) {
                minecart.setDeltaMovement(minecart.getDeltaMovement().scale(accelerated / speed));
                speed = accelerated;
            }

            if (speed <= 0.8D) return;
            moveAcrossRailsForOrdinaryCart(level, minecart, speed);
            ci.cancel();
            return;
        }

        if (speed <= 0.8D) return;

        if (minecart instanceof SolariteBatteryMinecartEntity cart) {
            moveAcrossRails(level, cart, speed);
            ci.cancel();
        }
    }

    private static boolean isActiveSolariteRail(AbstractMinecart cart, ServerLevel level) {
        BlockState state = level.getBlockState(cart.getCurrentBlockPosOrRailBelow());
        return state.getBlock() instanceof com.neutrinodust.useful_ores.block.rail.SolariteRailBlock
                && state.hasProperty(com.neutrinodust.useful_ores.block.rail.SolariteRailBlock.LIT)
                && state.getValue(com.neutrinodust.useful_ores.block.rail.SolariteRailBlock.LIT);
    }

    private static Vec3 stationaryStartDirection(AbstractMinecart cart, BlockState state, BlockPos pos) {
        if (!(state.getBlock() instanceof BaseRailBlock railBlock)) return Vec3.ZERO;
        RailShape shape = state.getValue(railBlock.getShapeProperty());
        if (shape == RailShape.EAST_WEST) {
            if (cart.isRedstoneConductor(pos.west())) return new Vec3(1, 0, 0);
            if (cart.isRedstoneConductor(pos.east())) return new Vec3(-1, 0, 0);
        } else if (shape == RailShape.NORTH_SOUTH) {
            if (cart.isRedstoneConductor(pos.north())) return new Vec3(0, 0, 1);
            if (cart.isRedstoneConductor(pos.south())) return new Vec3(0, 0, -1);
        }
        return Vec3.ZERO;
    }

    private static void moveAcrossRails(ServerLevel level, SolariteBatteryMinecartEntity cart, double initialSpeed) {
        cart.resetFallDistance();
        double movementLeft = Math.max(initialSpeed, 0.0);
        boolean first = true;

        for (int iteration = 0; iteration < 8 && cart.isAlive() && movementLeft > 1.0e-5; iteration++) {
            BlockPos pos = cart.getCurrentBlockPosOrRailBelow();
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BaseRailBlock railBlock)) {
                cart.setOnRails(false);
                return;
            }
            cart.setOnRails(true);

            RailShape shape = state.getValue(railBlock.getShapeProperty());
            Vec3 movement = cart.getDeltaMovement().horizontal();
            double speed = movement.length();
            if (speed < 1.0e-5) return;

            double slideSpeed = Math.max(0.0078125, speed * 0.02);
            if (cart.isInWater()) slideSpeed *= 0.2;
            movement = switch (shape) {
                case ASCENDING_EAST -> movement.add(-slideSpeed, 0.0, 0.0);
                case ASCENDING_WEST -> movement.add(slideSpeed, 0.0, 0.0);
                case ASCENDING_NORTH -> movement.add(0.0, 0.0, slideSpeed);
                case ASCENDING_SOUTH -> movement.add(0.0, 0.0, -slideSpeed);
                default -> movement;
            };

            if (first) {
                movement = cart.solariteApplyNaturalSlowdown(movement);
                double max = cart.getSolariteMaxTrackSpeed(level);
                double horizontal = movement.horizontalDistance();
                if (horizontal > max && horizontal > 1.0e-8) {
                    movement = movement.scale(max / horizontal);
                }
                first = false;
            }

            cart.setDeltaMovement(movement);
            movementLeft = Math.min(movementLeft, movement.horizontalDistance());
            movementLeft = stepAlongTrack(cart, pos, shape, movementLeft);

            if (movementLeft <= 1.0e-5) break;
        }
    }

    private static void moveAcrossRailsForOrdinaryCart(ServerLevel level, AbstractMinecart cart, double initialSpeed) {
        cart.resetFallDistance();
        double movementLeft = initialSpeed;

        for (int iteration = 0; iteration < 8 && cart.isAlive() && movementLeft > 1.0e-5; iteration++) {
            BlockPos pos = cart.getCurrentBlockPosOrRailBelow();
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof com.neutrinodust.useful_ores.block.rail.SolariteRailBlock railBlock)
                    || !state.getValue(com.neutrinodust.useful_ores.block.rail.SolariteRailBlock.LIT)) {

                Vec3 current = cart.getDeltaMovement();
                if (current.horizontalDistance() > 1.0e-5) {
                    cart.setDeltaMovement(current.scale(-1.0));
                }
                cart.setOnRails(false);
                return;
            }

            RailShape shape = state.getValue(railBlock.getShapeProperty());
            Vec3 movement = cart.getDeltaMovement();
            double speed = movement.horizontalDistance();
            if (speed < 1.0e-5) return;

            double slideSpeed = cart.isInWater() ? 0.0015625 : 0.0078125;
            movement = switch (shape) {
                case ASCENDING_EAST -> movement.add(-slideSpeed, 0.0, 0.0);
                case ASCENDING_WEST -> movement.add(slideSpeed, 0.0, 0.0);
                case ASCENDING_NORTH -> movement.add(0.0, 0.0, slideSpeed);
                case ASCENDING_SOUTH -> movement.add(0.0, 0.0, -slideSpeed);
                default -> movement;
            };

            cart.setDeltaMovement(movement);
            movementLeft = Math.min(movementLeft, movement.horizontalDistance());
            movementLeft = stepAlongTrackOrdinaryCart(cart, pos, shape, movementLeft);
            if (movementLeft <= 1.0e-5) break;
        }
    }

    private static double stepAlongTrackOrdinaryCart(AbstractMinecart cart, BlockPos pos,
                                                     RailShape shape, double movementLeft) {
        Pair<Vec3i, Vec3i> exits = AbstractMinecart.exits(shape);
        Vec3i exit0 = exits.getFirst();
        Vec3i exit1 = exits.getSecond();
        Vec3 movement = cart.getDeltaMovement().horizontal();
        if (movement.length() < 1.0e-5) {
            cart.setDeltaMovement(Vec3.ZERO);
            return 0.0;
        }

        boolean inHill = exit0.getY() != exit1.getY();
        Vec3 inDirection = new Vec3(exit1).scale(0.5).horizontal();
        Vec3 outDirection = new Vec3(exit0).scale(0.5).horizontal();
        if (movement.dot(outDirection) < movement.dot(inDirection)) outDirection = inDirection;

        Vec3 outPosition = Vec3.atBottomCenterOf(pos).add(outDirection).add(0.0, 0.1, 0.0)
                .add(outDirection.normalize().scale(1.0e-5));
        if (inHill && !isDescendingOrdinary(movement, shape)) outPosition = outPosition.add(0.0, 1.0, 0.0);

        Vec3 towardsOut = outPosition.subtract(cart.position()).normalize();
        if (towardsOut.horizontalDistance() < 1.0e-8) return 0.0;
        movement = towardsOut.scale(movement.length() / towardsOut.horizontalDistance());

        Vec3 oldPosition = cart.position();
        double distanceToOut = oldPosition.distanceTo(outPosition);
        double step = Math.min(movementLeft, distanceToOut) * (inHill ? Math.sqrt(2.0) : 1.0);
        Vec3 newPosition = oldPosition.add(movement.normalize().scale(step));
        double used = oldPosition.distanceTo(newPosition);
        if (used >= distanceToOut - 1.0e-6) {
            newPosition = outPosition;
            movementLeft = Math.max(0.0, movementLeft - distanceToOut);
        } else {
            movementLeft = 0.0;
        }

        cart.move(MoverType.SELF, newPosition.subtract(oldPosition));
        if (inHill) {
            double horizontalDistance = outPosition.horizontal().distanceTo(cart.position().horizontal());
            double projectedY = outPosition.y + (isDescendingOrdinary(movement, shape) ? horizontalDistance : -horizontalDistance);
            if (cart.getY() < projectedY) cart.setPos(cart.getX(), projectedY, cart.getZ());
        }
        cart.setDeltaMovement(movement);
        return movementLeft;
    }

    private static boolean isDescendingOrdinary(Vec3 movement, RailShape shape) {
        return switch (shape) {
            case ASCENDING_EAST -> movement.x < 0.0;
            case ASCENDING_WEST -> movement.x > 0.0;
            case ASCENDING_NORTH -> movement.z > 0.0;
            case ASCENDING_SOUTH -> movement.z < 0.0;
            default -> false;
        };
    }

    private static double stepAlongTrack(SolariteBatteryMinecartEntity cart, BlockPos pos,
                                         RailShape shape, double movementLeft) {
        Pair<Vec3i, Vec3i> exits = AbstractMinecart.exits(shape);
        Vec3i exit0 = exits.getFirst();
        Vec3i exit1 = exits.getSecond();
        Vec3 movement = cart.getDeltaMovement().horizontal();
        if (movement.length() < 1.0e-5) {
            cart.setDeltaMovement(Vec3.ZERO);
            return 0.0;
        }

        boolean inHill = exit0.getY() != exit1.getY();
        Vec3 inDirection = new Vec3(exit1).scale(0.5).horizontal();
        Vec3 outDirection = new Vec3(exit0).scale(0.5).horizontal();
        if (movement.dot(outDirection) < movement.dot(inDirection)) {
            outDirection = inDirection;
        }

        Vec3 outPosition = Vec3.atBottomCenterOf(pos)
                .add(outDirection)
                .add(0.0, 0.1, 0.0)
                .add(outDirection.normalize().scale(1.0e-5));

        if (inHill && !isDescending(movement, shape)) {
            outPosition = outPosition.add(0.0, 1.0, 0.0);
        }

        Vec3 towardsOut = outPosition.subtract(cart.position()).normalize();
        if (towardsOut.horizontalDistance() < 1.0e-8) return 0.0;
        movement = towardsOut.scale(movement.length() / towardsOut.horizontalDistance());

        Vec3 oldPosition = cart.position();
        Vec3 newPosition = oldPosition.add(movement.normalize().scale(
                Math.min(movementLeft, oldPosition.distanceTo(outPosition))
                        * (inHill ? Math.sqrt(2.0) : 1.0)));

        double distanceToOut = oldPosition.distanceTo(outPosition);
        double used = oldPosition.distanceTo(newPosition);
        if (used >= distanceToOut - 1.0e-6) {
            newPosition = outPosition;
            movementLeft = Math.max(0.0, movementLeft - distanceToOut);
        } else {
            movementLeft = 0.0;
        }

        cart.move(MoverType.SELF, newPosition.subtract(oldPosition));

        if (inHill) {
            double horizontalDistance = outPosition.horizontal().distanceTo(cart.position().horizontal());
            double projectedY = outPosition.y + (isDescending(movement, shape) ? horizontalDistance : -horizontalDistance);
            if (cart.getY() < projectedY) {
                cart.setPos(cart.getX(), projectedY, cart.getZ());
            }
        }

        if (cart.position().distanceTo(oldPosition) < 1.0e-5 && used > 1.0e-5) {
            cart.setDeltaMovement(Vec3.ZERO);
            return 0.0;
        }

        cart.setDeltaMovement(movement);
        return movementLeft;
    }

    private static boolean isDescending(Vec3 movement, RailShape shape) {
        return switch (shape) {
            case ASCENDING_EAST -> movement.x < 0.0;
            case ASCENDING_WEST -> movement.x > 0.0;
            case ASCENDING_NORTH -> movement.z > 0.0;
            case ASCENDING_SOUTH -> movement.z < 0.0;
            default -> false;
        };
    }
}

