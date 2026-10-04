package com.neutrinodust.useful_ores.mixin;
import net.minecraft.world.level.BlockCollisions;

import com.neutrinodust.useful_ores.minecart.SolariteMinecartCollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;











@Mixin(BlockCollisions.class)
public abstract class MixinBlockCollisionsSolariteMinecart {
    @Redirect(
            method = "computeNext",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape usefulOres$solariteMinecartShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof SolariteMinecartCollisionContext solariteContext
                && solariteContext.shouldIgnore(pos)) {
            return Shapes.empty();
        }
        return state.getCollisionShape(level, pos, context);
    }
}
