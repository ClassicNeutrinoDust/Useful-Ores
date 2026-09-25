package com.neutrinodust.useful_ores.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SperryliteCatalyticVialItem extends PotionItem {

    public SperryliteCatalyticVialItem(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.network.chat.Component getName(ItemStack stack) {

        return net.minecraft.network.chat.Component.translatable(this.getDescriptionId());
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public net.minecraft.world.item.ItemUseAnimation getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.get(DataComponents.POTION_CONTENTS) == null) {
            return tryFillFromWater(level, player, stack);
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    private InteractionResult tryFillFromWater(Level level, Player player, ItemStack stack) {
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hit.getBlockPos();
        if (!level.getFluidState(pos).is(FluidTags.WATER)) {
            return InteractionResult.PASS;
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);

        if (!level.isClientSide()) {
            // Fill exactly one vial from the stack instead of stamping the potion
            // contents onto the whole stack (which would fill every vial in it at
            // once). The rest of the empty vials stay behind as empty vials.
            ItemStack filledVial = new ItemStack(this);
            filledVial.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
            filledVial.set(DataComponents.MAX_STACK_SIZE, 1);

            if (stack.getCount() > 1) {
                stack.shrink(1);
                if (!player.getInventory().add(filledVial)) {
                    player.drop(filledVial, false, Prediction.SERVER_ONLY);
                }
            } else {
                stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
                stack.set(DataComponents.MAX_STACK_SIZE, 1);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        // Vanilla's dirt -> mud conversion is hardcoded to hand back a plain glass
        // bottle regardless of the potion item subclass, which would silently turn
        // this vial into a glass bottle. Handle the conversion ourselves so the
        // player gets an empty vial back instead.
        boolean isDirtLike = state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT);
        if (isDirtLike && contents.is(Potions.WATER)) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(context.getPlayer(), GameEvent.FLUID_PLACE, pos);

                Player player = context.getPlayer();
                if (player != null && !player.getAbilities().instabuild) {
                    stack.remove(DataComponents.POTION_CONTENTS);
                    stack.remove(DataComponents.MAX_STACK_SIZE);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        Player player = livingEntity instanceof Player p ? p : null;

        if (!level.isClientSide()) {
            contents.forEachEffect(livingEntity::addEffect, 1.0f);
            if (player != null) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }

        if (player == null || player.getAbilities().instabuild) {
            return stack;
        }

        // Consume exactly one filled vial.  Return that one vial as an empty
        // vial so it can stack with other empty vials in the inventory.
        if (stack.getCount() > 1) {
            stack.shrink(1);
            ItemStack emptyVial = new ItemStack(this);
            if (!level.isClientSide() && !player.getInventory().add(emptyVial)) {
                player.drop(emptyVial, false, Prediction.SERVER_ONLY);
            }
            return stack;
        }

        stack.remove(DataComponents.POTION_CONTENTS);
        stack.remove(DataComponents.MAX_STACK_SIZE);
        return stack;
    }
}
