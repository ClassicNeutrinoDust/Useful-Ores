package com.neutrinodust.useful_ores.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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
        level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.FLUID_PICKUP, pos);

        if (!level.isClientSide()) {
            stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResult.SUCCESS;
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
                player.drop(emptyVial, false);
            }
            return stack;
        }

        stack.remove(DataComponents.POTION_CONTENTS);
        return stack;
    }
}

