package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class SolariteFurnacePartBlockEntity extends BlockEntity {
    public SolariteFurnacePartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLARITE_FURNACE_PART.get(), pos, state);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && level != null) {
            BlockPos controllerPos = SolariteFurnacePartBlock.controllerPos(getBlockState(), worldPosition);
            BlockEntity controller = level.getBlockEntity(controllerPos);
            if (controller instanceof SolariteFurnaceBlockEntity furnace) {
                return furnace.getCapability(capability, side);
            }
        }
        return super.getCapability(capability, side);
    }
}
