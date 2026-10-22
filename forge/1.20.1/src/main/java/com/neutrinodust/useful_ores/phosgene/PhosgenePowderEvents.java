package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import com.neutrinodust.useful_ores.util.PotionStackData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PhosgenePowderEvents {
    private static int tickCounter;

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            PhosgenePowderSync.sendFullSync(player);
        }
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            PhosgenePowderSync.sendFullSync(player);
        }
    }

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.PHOSGENE_POTION_POWDER.get())) return;

        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        if (level.getBlockState(pos).isAir()) return;

        Potion potion = PhosgenePotionPowderItem.getPotionContents(stack);

        if (!PotionStackData.hasContents(stack) || potion.getEffects().isEmpty()) return;
        ResourceLocation decodedPotionId = BuiltInRegistries.POTION.getKey(potion);
        if (decodedPotionId == null || !"minecraft".equals(decodedPotionId.getNamespace())) return;

        ResourceLocation potionId = BuiltInRegistries.POTION.getKey(potion);
        if (potionId == null) return;

        PhosgenePowderData data = PhosgenePowderData.get(serverLevel);
        data.apply(pos, potionId.toString(), serverLevel.getGameTime(), PotionUtils.getColor(stack));
        PhosgenePowderSync.sendAppliedToAll(serverLevel, pos, PotionUtils.getColor(stack));

        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.65F, 1.25F);
        if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        BlockPos pos = event.getPos();
        if (PhosgenePowderData.get(serverLevel).get(pos) == null) return;
        PhosgenePowderData.get(serverLevel).remove(pos);
        PhosgenePowderSync.sendRemovalToAll(serverLevel, pos);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        if (++tickCounter % 10 != 0) return;
        for (ServerLevel level : event.getServer().getAllLevels()) tickLevel(level);
    }

    private static void tickLevel(ServerLevel level) {
        PhosgenePowderData data = PhosgenePowderData.get(level);
        long now = level.getGameTime();
        List<Long> expired = new ArrayList<>();

        for (Map.Entry<Long, PhosgenePowderData.Applied> entry : data.snapshot().entrySet()) {
            BlockPos pos = BlockPos.of(entry.getKey());
            PhosgenePowderData.Applied applied = entry.getValue();

            if (applied.expiresAt() > now + PhosgenePowderData.LIFETIME_TICKS) {
                applied = new PhosgenePowderData.Applied(
                        applied.potionId(), now + PhosgenePowderData.LIFETIME_TICKS, applied.color());
                data.update(pos, applied);
            }

            if (applied.expiresAt() <= now || level.getBlockState(pos).isAir()) {
                expired.add(entry.getKey());
                continue;
            }

            ResourceLocation storedPotionId = ResourceLocation.tryParse(applied.potionId());
            if (storedPotionId == null) continue;
            Potion potion = BuiltInRegistries.POTION.get(storedPotionId);
            if (potion == null || !storedPotionId.equals(BuiltInRegistries.POTION.getKey(potion))) continue;
            AABB area = new AABB(pos.getX(), pos.getY() + 1.0, pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 2.05, pos.getZ() + 1.0)
                    .inflate(0.05, 0.05, 0.05);

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                for (var effect : potion.getEffects()) {
                    entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            effect.getEffect(), PhosgenePowderData.EFFECT_TICKS, effect.getAmplifier(),
                            false, true, true));
                }
            }
        }

        for (long packed : expired) {
            data.remove(packed);
            PhosgenePowderSync.sendRemovalToAll(level, BlockPos.of(packed));
        }
    }
}

