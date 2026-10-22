package com.neutrinodust.useful_ores.attribution;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class AttributedArmorEvents {

    private static final int EFFECT_REFRESH_DURATION = 120;

    private static final int REFRESH_THRESHOLD_TICKS = 20;

    private static final int NIGHT_VISION_REFRESH_DURATION = 240;
    private static final int NIGHT_VISION_REFRESH_THRESHOLD_TICKS = 220;
    private static final int PARTICLE_INTERVAL_TICKS = 6;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
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
            boolean nightVision = instance.getEffect().equals(net.minecraft.world.effect.MobEffects.NIGHT_VISION);
            MobEffectInstance current = player.getEffect(instance.getEffect());

            int threshold = nightVision ? NIGHT_VISION_REFRESH_THRESHOLD_TICKS : REFRESH_THRESHOLD_TICKS;
            if (current != null && current.getDuration() > threshold) continue;
            if (nightVision) {
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

        String id = ModDataComponents.getAttributedEffect(helmet);
        if (id == null) return null;
        if (id == null || !id.equals(ModDataComponents.getAttributedEffect(chest))) return null;
        if (!id.equals(ModDataComponents.getAttributedEffect(legs))) return null;
        if (!id.equals(ModDataComponents.getAttributedEffect(boots))) return null;
        return id;
    }

    private boolean isAttributed(ItemStack stack, net.minecraft.world.item.Item expected) {
        return stack.is(expected) && ModDataComponents.getAttributedEffect(stack) != null;
    }
}

