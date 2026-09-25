package com.neutrinodust.useful_ores.combat;

import com.neutrinodust.useful_ores.item.BackportSpearItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Keeps the server's spear-use state synchronized when a new left-click action begins. */
@EventBusSubscriber(modid = "useful_ores")
public final class SpearUseStateSync {
    private SpearUseStateSync() {}

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getSide().isServer()) {
            cancelIfSpearUse(event.getEntity(), event.getEntity().getUseItem());
        }
    }

    private static void cancelIfSpearUse(Player player, ItemStack used) {
        if (!player.level().isClientSide() && player.isUsingItem()
                && used.getItem() instanceof BackportSpearItem) {
            player.stopUsingItem();
            SpearChargeCombat.endUse(player, used);
        }
    }
}
