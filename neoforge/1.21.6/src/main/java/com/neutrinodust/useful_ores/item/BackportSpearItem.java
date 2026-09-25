package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.combat.SpearChargeCombat;
import com.neutrinodust.useful_ores.init.ModSpearSounds;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;






public final class BackportSpearItem extends Item {
    public static final int CONTACT_COOLDOWN_TICKS = 10;
    public static final float FORWARD_MOVEMENT = 0.38F;

    private final ToolMaterial material;
    private final float swingAnimationSeconds;
    private final float chargeDamageMultiplier;
    private final float chargeDelaySeconds;
    private final float maxDurationForDismountSeconds;
    private final float minSpeedForDismount;
    private final float maxDurationForChargeKnockbackSeconds;
    private final float minSpeedForChargeKnockback;
    private final float maxDurationForChargeDamageSeconds;
    private final float minRelativeSpeedForChargeDamage;

    public BackportSpearItem(
            ToolMaterial material,
            float swingAnimationSeconds,
            float chargeDamageMultiplier,
            float chargeDelaySeconds,
            float maxDurationForDismountSeconds,
            float minSpeedForDismount,
            float maxDurationForChargeKnockbackSeconds,
            float minSpeedForChargeKnockback,
            float maxDurationForChargeDamageSeconds,
            float minRelativeSpeedForChargeDamage,
            Properties properties) {
        super(properties.sword(material, 1.0F, (1.0F / swingAnimationSeconds) - 4.0F));
        this.material = material;
        this.swingAnimationSeconds = swingAnimationSeconds;
        this.chargeDamageMultiplier = chargeDamageMultiplier;
        this.chargeDelaySeconds = chargeDelaySeconds;
        this.maxDurationForDismountSeconds = maxDurationForDismountSeconds;
        this.minSpeedForDismount = minSpeedForDismount;
        this.maxDurationForChargeKnockbackSeconds = maxDurationForChargeKnockbackSeconds;
        this.minSpeedForChargeKnockback = minSpeedForChargeKnockback;
        this.maxDurationForChargeDamageSeconds = maxDurationForChargeDamageSeconds;
        this.minRelativeSpeedForChargeDamage = minRelativeSpeedForChargeDamage;
    }

    public float swingAnimationSeconds() { return swingAnimationSeconds; }
    public float chargeDamageMultiplier() { return chargeDamageMultiplier; }
    public float chargeDelaySeconds() { return chargeDelaySeconds; }
    public float maxDurationForDismountSeconds() { return maxDurationForDismountSeconds; }
    public float minSpeedForDismount() { return minSpeedForDismount; }
    public float maxDurationForChargeKnockbackSeconds() { return maxDurationForChargeKnockbackSeconds; }
    public float minSpeedForChargeKnockback() { return minSpeedForChargeKnockback; }
    public float maxDurationForChargeDamageSeconds() { return maxDurationForChargeDamageSeconds; }
    public float minRelativeSpeedForChargeDamage() { return minRelativeSpeedForChargeDamage; }

    public static BackportSpearItem from(ItemStack stack) {
        return stack.getItem() instanceof BackportSpearItem spear ? spear : null;
    }

    public KineticParams kineticParams() {
        return new KineticParams(
                CONTACT_COOLDOWN_TICKS,
                secondsToTicks(chargeDelaySeconds),
                new Condition(secondsToTicks(maxDurationForDismountSeconds), minSpeedForDismount, 0.0F),
                new Condition(secondsToTicks(maxDurationForChargeKnockbackSeconds), minSpeedForChargeKnockback, 0.0F),
                new Condition(secondsToTicks(maxDurationForChargeDamageSeconds), 0.0F, minRelativeSpeedForChargeDamage),
                FORWARD_MOVEMENT,
                chargeDamageMultiplier);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        if (!level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSpearSounds.SPEAR_USE.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        SpearChargeCombat.onUseTick(level, user, stack, remainingUseTicks);
        super.onUseTick(level, user, stack, remainingUseTicks);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        SpearChargeCombat.endUse(user, stack);
        return super.releaseUsing(stack, level, user, remainingUseTicks);
    }

    public boolean canAttackBlock(net.minecraft.world.level.block.state.BlockState state, Level level,
                                  net.minecraft.core.BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        
    }

    private static int secondsToTicks(float seconds) { return (int) (seconds * 20.0F); }

    public record KineticParams(int contactCooldownTicks, int delayTicks, Condition dismount,
                                Condition knockback, Condition damage, float forwardMovement,
                                float damageMultiplier) {}

    public record Condition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
        public boolean test(int ticksUsed, double attackerSpeed, double relativeSpeed, float entityFactor) {
            return ticksUsed <= maxDurationTicks
                    && attackerSpeed >= minSpeed * entityFactor
                    && relativeSpeed >= minRelativeSpeed * entityFactor;
        }
    }
}
