package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AncientPedestalBlockEntity extends BlockEntity implements software.bernie.geckolib.animatable.GeoAnimatable {

    public AncientPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_PEDESTAL.get(), pos, state);
    }

    private final software.bernie.geckolib.animatable.instance.AnimatableInstanceCache geoCache =
            software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache(this);

    @Override
    public software.bernie.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public double getTick(Object object) {
        return this.level != null ? this.level.getGameTime() : 0.0D;
    }
}

