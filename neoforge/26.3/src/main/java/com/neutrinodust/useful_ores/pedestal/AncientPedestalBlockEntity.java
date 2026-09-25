package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AncientPedestalBlockEntity extends BlockEntity implements com.geckolib.animatable.GeoAnimatable {

    public AncientPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_PEDESTAL.get(), pos, state);
    }

    private final com.geckolib.animatable.instance.AnimatableInstanceCache geoCache =
            com.geckolib.util.GeckoLibUtil.createInstanceCache(this);

    @Override
    public com.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar controllers) {

    }
}

