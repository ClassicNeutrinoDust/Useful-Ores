package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.blastproof.BlastproofBlockData;
import com.neutrinodust.useful_ores.blastproof.ExplosionContext;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class SolariteFurnaceMultiblock {

    private SolariteFurnaceMultiblock() {}

    private static final int[][] OFFSETS = {
            {0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1},
            {1, 1, 0}, {1, 0, 1}, {0, 1, 1}, {1, 1, 1}
    };

    private static boolean working = false;

    public static boolean isWorking() {
        return working;
    }

    public static BlockPos[] structurePositions(BlockPos origin) {
        BlockPos[] result = new BlockPos[OFFSETS.length];
        for (int i = 0; i < OFFSETS.length; i++) {
            result[i] = origin.offset(OFFSETS[i][0], OFFSETS[i][1], OFFSETS[i][2]);
        }
        return result;
    }

    public static void tryAssemble(Level level, BlockPos placedPos) {
        if (working || level.isClientSide()) return;

        for (int[] originOffset : OFFSETS) {
            BlockPos origin = placedPos.offset(-originOffset[0], -originOffset[1], -originOffset[2]);
            if (isFullComponentCube(level, origin)) {
                assemble(level, origin);
                return;
            }
        }
    }

    private static boolean isFullComponentCube(Level level, BlockPos origin) {
        for (int[] off : OFFSETS) {
            BlockState state = level.getBlockState(origin.offset(off[0], off[1], off[2]));
            if (!state.is(ModItems.SOLARITE_FURNACE_COMPONENT.get())) return false;
        }
        return true;
    }

    private static void assemble(Level level, BlockPos origin) {
        working = true;
        try {

            BlockState originState = level.getBlockState(origin);
            Direction facing = originState.hasProperty(SolariteFurnaceComponentBlock.FACING)
                    ? originState.getValue(SolariteFurnaceComponentBlock.FACING)
                    : Direction.NORTH;
            level.setBlockAndUpdate(origin, ModItems.SOLARITE_FURNACE_BLOCK.get().defaultBlockState()
                    .setValue(SolariteFurnaceBlock.FACING, facing));
            for (int[] off : OFFSETS) {
                if (off[0] == 0 && off[1] == 0 && off[2] == 0) continue;
                BlockPos partPos = origin.offset(off[0], off[1], off[2]);
                BlockState partState = ModItems.SOLARITE_FURNACE_PART.get().defaultBlockState()
                        .setValue(SolariteFurnacePartBlock.HIGH_X, off[0] == 1)
                        .setValue(SolariteFurnacePartBlock.HIGH_Y, off[1] == 1)
                        .setValue(SolariteFurnacePartBlock.HIGH_Z, off[2] == 1);
                level.setBlockAndUpdate(partPos, partState);
            }
        } finally {
            working = false;
        }
    }

    public static void disassemble(Level level, BlockPos origin, BlockPos alreadyGoneBy) {
        if (working) return;
        working = true;
        try {
            BlastproofBlockData blastData = (level instanceof ServerLevel serverLevel)
                    ? BlastproofBlockData.get(serverLevel) : null;
            boolean protectCoated = blastData != null && ExplosionContext.isActive();
            for (int[] off : OFFSETS) {
                BlockPos p = origin.offset(off[0], off[1], off[2]);
                if (protectCoated && blastData.isBlastproof(p)) continue;
                if (!p.equals(alreadyGoneBy) && !level.getBlockState(p).isAir()) {
                    level.removeBlock(p, false);
                }
                Block.popResource(level, p, new ItemStack(ModItems.SOLARITE_FURNACE_COMPONENT.asItem()));
                if (blastData != null) blastData.unmark(p);
            }
        } finally {
            working = false;
        }
    }
}

