package com.neutrinodust.useful_ores.minecart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.shapes.EntityCollisionContext;








public final class SolariteMinecartCollisionContext extends EntityCollisionContext {
    private final BlockPos ignoreBelow;
    private final BlockPos slopeIgnore;
    private final Level level;

    public SolariteMinecartCollisionContext(AbstractMinecart minecart, boolean collidesWithFluid) {
        super(minecart);
        this.level = minecart.level();

        BlockPos railPos = minecart instanceof com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity solarite
                ? solarite.getCurrentBlockPosOrRailBelow()
                : null;

        BlockPos below = null;
        BlockPos slope = null;
        if (railPos != null) {
            BlockState state = minecart.level().getBlockState(railPos);
            if (BaseRailBlock.isRail(state)) {
                below = railPos.below();
                RailShape shape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
                if (shape.isAscending()) {
                    slope = switch (shape) {
                        case ASCENDING_EAST -> railPos.east();
                        case ASCENDING_WEST -> railPos.west();
                        case ASCENDING_NORTH -> railPos.north();
                        case ASCENDING_SOUTH -> railPos.south();
                        default -> null;
                    };
                }

                
                
                
                
                
                
                
            }
        }

        this.ignoreBelow = below;
        this.slopeIgnore = slope;
    }

    public boolean shouldIgnore(BlockPos pos) {
        
        
        
        return pos.equals(ignoreBelow) || pos.equals(slopeIgnore);
    }
}
