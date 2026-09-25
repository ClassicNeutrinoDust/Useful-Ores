package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.client.spear.SpearSuperLog;
import com.neutrinodust.useful_ores.combat.SpearChargeCombat;
import com.neutrinodust.useful_ores.init.ModSpearSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class SpearItem extends Item {
    public SpearItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        SpearSuperLog.useStart(level.isClientSide() ? "CLIENT" : "SERVER", level.getGameTime(), player.getName().getString());
        if (!level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSpearSounds.SPEAR_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }

    




    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
        SpearChargeCombat.onUseTick(level, user, stack, remainingUseDuration);
        super.onUseTick(level, user, stack, remainingUseDuration);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseDuration) {
        SpearChargeCombat.endUse(user, stack);
        SpearSuperLog.useEnd(level.isClientSide() ? "CLIENT" : "SERVER", level.getGameTime(), user.getName().getString());
        return super.releaseUsing(stack, level, user, remainingUseDuration);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.hurtEnemy(stack, target, attacker);
    }
}
