package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class SolariteBatteryMinecartItem extends Item {
    public SolariteBatteryMinecartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.RAILS)) return InteractionResult.FAIL;
        ItemStack stack = context.getItemInHand();
        if (level instanceof ServerLevel server) {
            RailShape shape = state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.RAIL_SHAPE)
                    ? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.RAIL_SHAPE)
                    : RailShape.NORTH_SOUTH;
            double yOffset = shape.isAscending() ? 0.5625D : 0.0625D;
            SolariteBatteryMinecartEntity cart = new SolariteBatteryMinecartEntity(ModEntities.SOLARITE_BATTERY_MINECART, server);
            cart.setPos(pos.getX() + 0.5D, pos.getY() + yOffset, pos.getZ() + 0.5D);
            if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)) cart.setCustomName(stack.getHoverName());
            cart.setOldPosAndRot();
            cart.getBehavior().adjustToRails(pos, state, true);
            cart.getBehavior().lerpSteps.clear();
            cart.setOnRails(true);
            server.addFreshEntity(cart);
            if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
