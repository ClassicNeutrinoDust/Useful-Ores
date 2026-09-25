package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.init.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Vanilla's dirt-to-mud conversion (DirtBlock#useItemOn) only looks at an
 * item's water PotionContents, not the concrete item class, so right
 * clicking dirt with a filled Sperrylite Catalytic Vial used to trigger it
 * and hand back a plain vanilla glass bottle instead of an empty vial.
 * This intercepts that interaction first so the vial correctly turns back
 * into an empty catalytic vial.
 */
public final class SperryliteVialDirtEvents {

    private static final PotionContents WATER_CONTENTS = new PotionContents(Potions.WATER);

    private SperryliteVialDirtEvents() {}

    public static void register() {
        UseBlockCallback.EVENT.register(SperryliteVialDirtEvents::onUseBlock);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof SperryliteCatalyticVialItem)) return InteractionResult.PASS;

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.equals(WATER_CONTENTS)) return InteractionResult.PASS;

        BlockPos pos = hit.getBlockPos();
        if (!level.getBlockState(pos).is(Blocks.DIRT)) return InteractionResult.PASS;

        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
            level.gameEvent(player, GameEvent.FLUID_PLACE, pos);

            if (!player.getAbilities().instabuild) {
                ItemStack emptyVial = new ItemStack(ModItems.SPERRYLITE_CATALYTIC_VIAL.get());
                if (stack.getCount() > 1) {
                    stack.shrink(1);
                    if (!player.getInventory().add(emptyVial)) {
                        player.drop(emptyVial, false);
                    }
                } else {
                    stack.remove(DataComponents.POTION_CONTENTS);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
