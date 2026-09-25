package com.neutrinodust.useful_ores.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.item.context.UseOnContext;
import com.neutrinodust.useful_ores.init.ModItems;
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
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // Preserve vanilla water-potion-to-mud behavior, but return a
        // Sperrylite Catalytic Vial instead of Minecraft's glass bottle.
        if (player == null
                || context.getClickedFace() == net.minecraft.core.Direction.DOWN
                || !level.getBlockState(context.getClickedPos()).is(BlockTags.CONVERTABLE_TO_MUD)
                || !stack.has(DataComponents.POTION_CONTENTS)
                || !stack.get(DataComponents.POTION_CONTENTS).is(Potions.WATER)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.MUD.defaultBlockState());
            level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.FLUID_PLACE, pos);
            replaceOneWithEmptyVial(player, context.getHand(), stack);
        }

        return InteractionResult.SUCCESS;
    }

    private void replaceOneWithEmptyVial(Player player, InteractionHand hand, ItemStack stack) {
        ItemStack emptyVial = new ItemStack(ModItems.SPERRYLITE_CATALYTIC_VIAL.get());

        if (stack.getCount() == 1) {
            player.setItemInHand(hand, emptyVial);
            return;
        }

        stack.shrink(1);
        if (!player.getInventory().add(emptyVial)) {
            player.drop(emptyVial, false);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.get(DataComponents.POTION_CONTENTS) == null) {
            return tryFillFromWater(level, player, hand, stack);
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    private InteractionResult tryFillFromWater(Level level, Player player, InteractionHand hand, ItemStack stack) {
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
            // A stack of empty vials represents separate vessels. Fill exactly
            // one vessel, rather than applying the water component to all 16.
            ItemStack filledVial = stack.split(1);
            filledVial.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));

            if (!player.getInventory().add(filledVial)) {
                player.drop(filledVial, false);
            }
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

        // Consuming one filled vial always produces one EMPTY CUSTOM VIAL,
        // never a vanilla glass bottle. When the filled vial is stacked,
        // remove exactly one from that stack and return the empty vessel to
        // the inventory so the remaining filled vials stay untouched.
        ItemStack emptyVial = stack.copyWithCount(1);
        emptyVial.remove(DataComponents.POTION_CONTENTS);

        if (stack.getCount() > 1) {
            stack.shrink(1);
            if (!level.isClientSide() && !player.getInventory().add(emptyVial)) {
                player.drop(emptyVial, false);
            }
            return stack;
        }

        return emptyVial;
    }
}
