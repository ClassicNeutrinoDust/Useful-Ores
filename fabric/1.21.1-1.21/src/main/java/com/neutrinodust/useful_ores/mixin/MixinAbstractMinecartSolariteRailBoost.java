package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.rail.SolariteRailBlock;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
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

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    private void usefulOres$solariteMaxSpeed(CallbackInfoReturnable<Double> cir) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (usefulOres$isActiveSolariteRail(cart)) {
            cir.setReturnValue(SOLARITE_MAX_SPEED);
        }
    }

    @Inject(method = "moveAlongTrack", at = @At("TAIL"))
    private void usefulOres$solariteDayBoost(BlockPos pos, BlockState state, CallbackInfo ci) {
        AbstractMinecart cart = (AbstractMinecart) (Object) this;
        if (cart instanceof SolariteBatteryMinecartEntity) return;
        if (!(cart.level() instanceof net.minecraft.server.level.ServerLevel)) return;
        if (!(state.getBlock() instanceof SolariteRailBlock)) return;

        if (!SolariteRailBlock.shouldBeActive(cart.level(), pos)) return;

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
