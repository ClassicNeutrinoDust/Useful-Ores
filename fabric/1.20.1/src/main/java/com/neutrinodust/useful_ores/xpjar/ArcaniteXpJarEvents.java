package com.neutrinodust.useful_ores.xpjar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArcaniteXpJarEvents {

    public static boolean onXpPickup(ExperienceOrb orb, Player player) {
        if (player.level().isClientSide()) return false;

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(ModItems.ARCANITE_XP_JAR.get())) return false;

        int current = ArcaniteXpJarItem.getStoredXp(mainHand);
        if (current >= ArcaniteXpJarBlockEntity.MAX_XP) return false;

        int orbValue = orb.getValue();
        int room = ArcaniteXpJarBlockEntity.MAX_XP - current;
        int absorbed = Math.min(orbValue, room);

        ArcaniteXpJarItem.setStoredXp(mainHand, current + absorbed);

        orb.discard();
        if (absorbed < orbValue) {
            player.giveExperiencePoints(orbValue - absorbed);
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.4f, 1.3f);

        return true;
    }
}

