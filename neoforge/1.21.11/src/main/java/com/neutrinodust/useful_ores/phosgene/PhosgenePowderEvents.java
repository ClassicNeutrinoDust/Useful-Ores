package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.PHOSGENE_POTION_POWDER.get())) return;

        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        if (level.getBlockState(pos).isAir()) return;

        PotionContents contents = PhosgenePotionPowderItem.getPotionContents(stack);
        if (!contents.hasEffects()) return;

        Identifier potionId = contents.potion().flatMap(h -> h.unwrapKey().map(k -> k.identifier())).orElse(null);
        if (potionId == null) return;

        PhosgenePowderData data = PhosgenePowderData.get(serverLevel);
        data.apply(pos, potionId.toString(), serverLevel.getGameTime(), contents.getColor());
        PhosgenePowderSync.sendAppliedToAll(serverLevel, pos, contents.getColor());

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
    public void onServerTick(ServerTickEvent.Post event) {
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

            var holder = BuiltInRegistries.POTION.get(Identifier.parse(applied.potionId()));
            if (holder.isEmpty()) continue;

            PotionContents contents = new PotionContents(holder.get());
            AABB area = new AABB(pos.getX(), pos.getY() + 1.0, pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 2.05, pos.getZ() + 1.0)
                    .inflate(0.05, 0.05, 0.05);

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                for (var effect : contents.getAllEffects()) {
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

