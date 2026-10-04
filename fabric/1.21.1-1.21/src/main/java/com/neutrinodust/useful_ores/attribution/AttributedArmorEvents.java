package com.neutrinodust.useful_ores.attribution;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AttributedArmorEvents {

    public static final AttributedArmorEvents INSTANCE = new AttributedArmorEvents();

    private static final int EFFECT_REFRESH_DURATION = 60;
    private static final int PARTICLE_INTERVAL_TICKS = 6;

    
    private static final int NIGHT_VISION_REFRESH_DURATION = 240;
    private static final int NIGHT_VISION_REFRESH_THRESHOLD_TICKS = 220;

    public void onPlayerTick(Player player) {
        if (player.tickCount % 2 != 0) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        String id = matchingAttribute(helmet, chest, legs, boots);
        if (id == null) return;

        AttributedEffects effect = AttributedEffects.byId(id);
        if (effect == null) return;

        for (MobEffectInstance instance : effect.freshInstances(EFFECT_REFRESH_DURATION)) {
            if (instance.getEffect().equals(net.minecraft.world.effect.MobEffects.NIGHT_VISION)) {
                MobEffectInstance current = player.getEffect(instance.getEffect());
                if (current != null && current.getDuration() > NIGHT_VISION_REFRESH_THRESHOLD_TICKS) continue;
                instance = new MobEffectInstance(instance.getEffect(), NIGHT_VISION_REFRESH_DURATION,
                        instance.getAmplifier(), true, false, true);
            }
            player.addEffect(instance);
        }

        if (player.level().getGameTime() % PARTICLE_INTERVAL_TICKS == 0
                && player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.6,
                    player.getY() + player.getRandom().nextDouble() * 1.8,
                    player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.6,
                    1, 0.0, 0.02, 0.0, 0.01);
        }
    }

    private String matchingAttribute(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
        if (!isAttributed(helmet, ModAttributedItems.ATTRIBUTED_SUBSPACE_HELMET.get())) return null;
        if (!isAttributed(chest, ModAttributedItems.ATTRIBUTED_SUBSPACE_CHESTPLATE.get())) return null;
        if (!isAttributed(legs, ModAttributedItems.ATTRIBUTED_SUBSPACE_LEGGINGS.get())) return null;
        if (!isAttributed(boots, ModAttributedItems.ATTRIBUTED_SUBSPACE_BOOTS.get())) return null;

        String id = helmet.get(ModDataComponents.ATTRIBUTED_EFFECT);
        if (id == null) return null;
        if (!id.equals(chest.get(ModDataComponents.ATTRIBUTED_EFFECT))) return null;
        if (!id.equals(legs.get(ModDataComponents.ATTRIBUTED_EFFECT))) return null;
        if (!id.equals(boots.get(ModDataComponents.ATTRIBUTED_EFFECT))) return null;
        return id;
    }

    private boolean isAttributed(ItemStack stack, net.minecraft.world.item.Item expected) {
        return stack.is(expected) && stack.get(ModDataComponents.ATTRIBUTED_EFFECT) != null;
    }
}

