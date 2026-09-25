package com.neutrinodust.useful_ores.combat;

import com.neutrinodust.useful_ores.init.SpearTags;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * MixinMinecraftSpearSounds cancels the "using item" (charging) state on the
 * CLIENT the instant the player left-clicks to jab. That mixin only patches
 * Minecraft#startAttack, which is a client-only method: it never told the
 * SERVER's copy of the player that the charge was cancelled.
 *
 * That mismatch is the real cause of the "jab animation looks right when I
 * hit a block, but drops to the charging pose when I jab air or a mob"
 * symptom. Hitting a block does not send any extra entity-data sync to the
 * client for the local player in between clicks, so the client's local
 * cancellation "sticks". Attacking an entity (or even swinging near one)
 * frequently triggers extra entity tracking/sync packets involving the
 * player, and whenever one of those carries the (still-true) server-side
 * "using item" flag, it overwrites the client's local override and the
 * renderer falls back to the charging pose for a frame or more - exactly
 * the "goes down on screen" symptom, and it also meant a left-click jab
 * thrown mid-charge never actually told the server the charge had ended,
 * so the server-side SpearChargeCombat tick kept running (and kept its
 * 10-tick contact cooldown active) after the player thought they'd let go.
 *
 * Fix: cancel the charge on the SERVER the moment a spear-holding player's
 * attack is dispatched there too, the same way vanilla itself always
 * cancels an in-progress use the moment you start a new action.
 */
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

    /** Also cancel on the server side whenever a spear holder starts destroying a block,
     *  mirroring vanilla's "any new action cancels the current item use" rule. Called from
     *  the block-break event hook already registered in UsefulOresMod. */
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
