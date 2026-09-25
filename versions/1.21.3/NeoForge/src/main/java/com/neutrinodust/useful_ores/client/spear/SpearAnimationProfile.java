package com.neutrinodust.useful_ores.client.spear;

import com.neutrinodust.useful_ores.init.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * Backported representation of the vanilla KineticWeapon parameters used by the spear animation logic.
 * The 1.21.4 game does not expose Item.Properties.spear/KineticWeapon, so the
 * exact numeric contract is kept here and keyed by the Useful Ores spear item.
 */
public record SpearAnimationProfile(
        float swingAnimationSeconds,
        float chargeDamageMultiplier,
        float chargeDelaySeconds,
        float maxDurationForDismountSeconds,
        float minSpeedForDismount,
        float maxDurationForChargeKnockbackSeconds,
        float minSpeedForChargeKnockback,
        float maxDurationForChargeDamageSeconds,
        float minRelativeSpeedForChargeDamage,
        float forwardMovement) {

    public static SpearAnimationProfile forStack(ItemStack stack) {
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (path.endsWith("_spear")) {
            path = path.substring(0, path.length() - "_spear".length());
        }
        ModConfig.MaterialEntries entry = ModConfig.ENTRIES.get(path);
        float speed = entry == null ? 2.0F : entry.miningSpeed.get().floatValue();

        // Useful Ores spear tuning table retained from the existing spear port.
        // Vanilla spear third-person attack movement is 0.38.
        if (speed <= 2.5F) {
            return new SpearAnimationProfile(0.65F, 0.70F, 0.75F, 5.0F, 14.0F, 10.0F, 5.1F, 15.0F, 4.6F, 0.38F);
        }
        if (speed <= 4.5F) {
            return new SpearAnimationProfile(0.75F, 0.82F, 0.70F, 4.5F, 13.0F, 9.0F, 5.1F, 13.75F, 4.6F, 0.38F);
        }
        if (speed <= 6.5F) {
            return new SpearAnimationProfile(0.95F, 0.95F, 0.60F, 2.5F, 11.0F, 6.75F, 5.1F, 11.25F, 4.6F, 0.38F);
        }
        if (speed <= 9.0F) {
            return new SpearAnimationProfile(1.05F, 1.075F, 0.50F, 3.0F, 10.0F, 6.5F, 5.1F, 10.0F, 4.6F, 0.38F);
        }
        return new SpearAnimationProfile(1.15F, 1.20F, 0.40F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F, 0.38F);
    }

    public float forwardMovement() { return forwardMovement; }
}
