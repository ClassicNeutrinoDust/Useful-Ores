package com.neutrinodust.useful_ores.xpjar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class ArcaniteXpJarEvents {

    @SubscribeEvent
    public void onXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(ModItems.ARCANITE_XP_JAR.get())) return;

        int current = ArcaniteXpJarItem.getStoredXp(mainHand);
        if (current >= ArcaniteXpJarBlockEntity.MAX_XP) return;

        ExperienceOrb orb = event.getOrb();
        int orbValue = orb.getValue();
        int room = ArcaniteXpJarBlockEntity.MAX_XP - current;
        int absorbed = Math.min(orbValue, room);

        ArcaniteXpJarItem.setStoredXp(mainHand, current + absorbed);

        if (absorbed < orbValue) {

            event.setCanceled(true);
            orb.discard();
            player.giveExperiencePoints(orbValue - absorbed);
        } else {

            event.setCanceled(true);
            orb.discard();
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.4f, 1.3f);
    }
}

