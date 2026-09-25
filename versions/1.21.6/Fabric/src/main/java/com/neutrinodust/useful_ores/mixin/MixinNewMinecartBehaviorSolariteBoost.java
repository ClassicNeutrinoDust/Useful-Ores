package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.rail.SolariteRailBlock;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class MixinNewMinecartBehaviorSolariteBoost {

    private AbstractMinecart usefulOres$minecart() {
        return ((MixinMinecartBehaviorAccessor) (Object) this).usefulOres$getMinecart();
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    private void usefulOres$solariteMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
        AbstractMinecart cart = usefulOres$minecart();
        if (!(cart instanceof SolariteBatteryMinecartEntity) && isActiveSolariteRail(cart, level)) {
            cir.setReturnValue(1.6D);
            return;
        }

        // 1.21.6 exposes the experimental minecart speed gamerule only when
        // the Minecart Improvements feature is enabled.  This mixin can still
        // execute when that feature is disabled, so querying the rule directly
        // would throw IllegalArgumentException and crash the integrated server
        // (especially when a player mounts the minecart and the vehicle ticks).
        int blocksPerSecond = 8;
        try {
            blocksPerSecond = level.getGameRules().getInt(net.minecraft.world.level.GameRules.RULE_MINECART_MAX_SPEED);
        } catch (IllegalArgumentException ignored) {
            // Vanilla 1.21.6 fallback cap when the experimental rule is absent.
        }
        double waterFactor = cart.isInWater() ? 0.5 : 1.0;
        cir.setReturnValue(blocksPerSecond * waterFactor / 20.0);
    }

    @Inject(method = "calculateBoostTrackSpeed", at = @At("HEAD"), cancellable = true)
    private void usefulOres$solariteBoost(Vec3 movement, BlockPos pos, BlockState state,
                                           CallbackInfoReturnable<Vec3> cir) {
        AbstractMinecart cart = usefulOres$minecart();
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (!(state.getBlock() instanceof SolariteRailBlock) || !state.getValue(SolariteRailBlock.LIT)) return;

        double speed = movement.horizontalDistance();
        double max = 1.6D;

        if (speed < 0.01D) {

            Vec3 start = usefulOres$stationaryStartDirection(cart, state, pos);
            if (start.lengthSqr() > 1.0e-12) {
                cir.setReturnValue(start.scale(0.02D));
            }
            return;
        }

        double boosted = Math.min(max, speed + 0.10D);
        if (boosted > speed) {
            cir.setReturnValue(movement.scale(boosted / speed));
        }
    }

    private static boolean isActiveSolariteRail(AbstractMinecart cart, ServerLevel level) {
        BlockState state = level.getBlockState(cart.getCurrentBlockPosOrRailBelow());
        return state.getBlock() instanceof SolariteRailBlock
                && state.hasProperty(SolariteRailBlock.LIT)
                && state.getValue(SolariteRailBlock.LIT);
    }

    private static Vec3 usefulOres$stationaryStartDirection(AbstractMinecart cart, BlockState state, BlockPos pos) {
        if (!state.hasProperty(SolariteRailBlock.SHAPE)) return Vec3.ZERO;
        RailShape shape = state.getValue(SolariteRailBlock.SHAPE);
        if (shape == RailShape.EAST_WEST) {
            if (cart.isRedstoneConductor(pos.west())) return new Vec3(1, 0, 0);
            if (cart.isRedstoneConductor(pos.east())) return new Vec3(-1, 0, 0);
        } else if (shape == RailShape.NORTH_SOUTH) {
            if (cart.isRedstoneConductor(pos.north())) return new Vec3(0, 0, 1);
            if (cart.isRedstoneConductor(pos.south())) return new Vec3(0, 0, -1);
        }
        return Vec3.ZERO;
    }
}

