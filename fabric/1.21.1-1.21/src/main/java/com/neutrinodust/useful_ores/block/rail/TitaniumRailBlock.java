package com.neutrinodust.useful_ores.block.rail;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class TitaniumRailBlock extends RailBlock {

    public TitaniumRailBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateState(BlockState state, Level level, BlockPos pos, boolean movedByPiston) {
        RailShape commanded = TitaniumControllerRailBlock.getControllerCommandedShape(level, pos);
        if (commanded != null && this.getShapeProperty().getPossibleValues().contains(commanded)) {
            return state.setValue(this.getShapeProperty(), commanded);
        }
        return super.updateState(state, level, pos, movedByPiston);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            TitaniumControllerRailBlock.enforceControllerPriority(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                    BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide()) {
            TitaniumControllerRailBlock.enforceControllerPriority(level, pos);
        }
    }
}

