package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.init.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PhosgenePowderEvents {
    private static final int CHECK_INTERVAL = 10;
    private static int tickCounter = 0;

    private PhosgenePowderEvents() {}

    public static void register() {
        UseBlockCallback.EVENT.register(PhosgenePowderEvents::onUseBlock);
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level instanceof ServerLevel serverLevel) {
                PhosgenePowderData.get(serverLevel).remove(pos);
                PhosgenePowderSync.sendRemovalToAll(serverLevel, pos);
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++tickCounter % CHECK_INTERVAL != 0) return;
            for (ServerLevel level : server.getAllLevels()) tickLevel(level);
        });
    }

    private static InteractionResult onUseBlock(net.minecraft.world.entity.player.Player player,
                                                  Level level, InteractionHand hand,
                                                  net.minecraft.world.phys.BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.PHOSGENE_POTION_POWDER.get())) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        BlockPos pos = hit.getBlockPos();
        if (level.getBlockState(pos).isAir()) return InteractionResult.PASS;

        PotionContents contents = PhosgenePotionPowderItem.getPotionContents(stack);
        if (!contents.hasEffects()) return InteractionResult.FAIL;

        Identifier potionId = contents.potion().flatMap(h -> h.unwrapKey().map(k -> k.identifier())).orElse(null);
        if (potionId == null) return InteractionResult.FAIL;

        int color = contents.getColor();
        PhosgenePowderData data = PhosgenePowderData.get(serverLevel);
        data.apply(pos, potionId.toString(), serverLevel.getGameTime(), color);
        PhosgenePowderSync.sendAppliedToAll(serverLevel, pos, color);

        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.65F, 1.25F);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    private static void tickLevel(ServerLevel level) {
        PhosgenePowderData data = PhosgenePowderData.get(level);
        long now = level.getGameTime();
        List<Long> expired = new ArrayList<>();

        for (Map.Entry<Long, PhosgenePowderData.Applied> entry : data.snapshot().entrySet()) {
            long packed = entry.getKey();
            BlockPos pos = BlockPos.of(packed);
            PhosgenePowderData.Applied applied = entry.getValue();

            // Cap fields created by older versions to the new one-day maximum.
            if (applied.expiresAt() > now + PhosgenePowderData.LIFETIME_TICKS) {
                applied = new PhosgenePowderData.Applied(
                        applied.potionId(), now + PhosgenePowderData.LIFETIME_TICKS, applied.color());
                data.update(pos, applied);
            }

            if (applied.expiresAt() <= now || level.getBlockState(pos).isAir()) {
                expired.add(packed);
                continue;
            }

            HolderPotion holder = findPotion(level, applied.potionId());
            if (holder == null) continue;

            AABB area = new AABB(pos.getX(), pos.getY() + 1.0, pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 2.05, pos.getZ() + 1.0).inflate(0.05, 0.05, 0.05);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
            for (LivingEntity entity : entities) {
                for (var effect : holder.contents().getAllEffects()) {
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

    private record HolderPotion(PotionContents contents) {}

    private static HolderPotion findPotion(ServerLevel level, String id) {
        Identifier identifier = Identifier.parse(id);
        var holder = BuiltInRegistries.POTION.get(identifier);
        return holder.map(p -> new HolderPotion(new PotionContents(p))).orElse(null);
    }
}

