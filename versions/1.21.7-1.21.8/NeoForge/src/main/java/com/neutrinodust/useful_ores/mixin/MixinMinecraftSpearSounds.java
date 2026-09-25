package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.client.spear.SpearSuperLog;
import com.neutrinodust.useful_ores.init.ModSpearSounds;
import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.network.SpearCancelChargePacket;
import com.neutrinodust.useful_ores.network.SpearJabPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** V99: dedicated spear jab dispatcher. Generic Player#attack is never used for spear jabs. */
@Mixin(Minecraft.class)
public abstract class MixinMinecraftSpearSounds {
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true, require = 0)
    private void usefulOres$performSpearJab(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = (Minecraft)(Object)this;
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(SpearTags.SPEARS)) return;

        HitResult hit = minecraft.hitResult;
        String hitType = hit == null ? "NULL" : hit.getType().name();
        long tick = player.level().getGameTime();
        boolean wasUsing = player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND;

        // Block clicks remain 100% vanilla mining. This is also the user's known-good visual anchor.
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            SpearSuperLog.startAttack(tick, hitType, wasUsing, false, "BAIL_BLOCK");
            return;
        }

        if (player.getCooldowns().isOnCooldown(stack)) {
            SpearSuperLog.startAttack(tick, hitType, wasUsing, true, "BLOCKED_BY_COOLDOWN");
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        if (wasUsing) {
            player.stopUsingItem();
            Minecraft.getInstance().getConnection().send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(new SpearCancelChargePacket()));
        }

        player.swing(InteractionHand.MAIN_HAND);
        player.playSound(ModSpearSounds.SPEAR_ATTACK.value(), 1.0F, 1.0F);
        Minecraft.getInstance().getConnection().send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(new SpearJabPacket()));

        double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        int cooldownTicks = attackSpeed > 0.0D
                ? Math.max(1, Math.round(20.0F / (float)attackSpeed))
                : 10;
        player.getCooldowns().addCooldown(stack, cooldownTicks);
        SpearSuperLog.startAttack(tick, hitType, wasUsing, false,
                "JAB_FIRED(newCooldown=" + cooldownTicks + "t)");

        // Consume the click. The legacy generic attack is deliberately not reached.
        cir.setReturnValue(true);
        cir.cancel();
    }
}
