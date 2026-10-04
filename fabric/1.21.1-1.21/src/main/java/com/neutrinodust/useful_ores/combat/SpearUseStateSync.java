package com.neutrinodust.useful_ores.combat;

import com.neutrinodust.useful_ores.init.SpearTags;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

























public final class SpearUseStateSync {
    private SpearUseStateSync() {}

    public static void register() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide() && player.isUsingItem()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.is(SpearTags.SPEARS) || player.getUseItem().is(SpearTags.SPEARS)) {
                    player.stopUsingItem();
                    SpearChargeCombat.endUse(player, stack);
                }
            }
            return InteractionResult.PASS;
        });
    }

    


    public static void cancelOnBlockBreak(Player player) {
        if (player.level().isClientSide() || !player.isUsingItem()) {
            return;
        }
        ItemStack used = player.getUseItem();
        if (used.is(SpearTags.SPEARS)) {
            player.stopUsingItem();
            SpearChargeCombat.endUse(player, used);
        }
    }
}
