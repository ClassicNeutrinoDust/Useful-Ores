package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;





@Mixin(ServerEntity.class)
public abstract class MixinServerEntitySolariteCart {
    @Shadow @Final private Entity entity;
    @Shadow @Final private Consumer<Packet<?>> broadcast;
    @Shadow private int tickCount;
    @Shadow private int lastSentYRot;
    @Shadow private int lastSentXRot;
    @Shadow private Vec3 lastSentMovement;

    @Shadow
    protected abstract void sendDirtyEntityData();

    @Inject(method = "sendChanges", at = @At("HEAD"))
    private void useful_ores$sendSolariteSteps(CallbackInfo ci) {
        if (entity instanceof SolariteBatteryMinecartEntity cart) {
            cart.syncToTrackers(broadcast, tickCount);
        }
    }

    @Inject(method = "sendChanges",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z", ordinal = 0),
            cancellable = true)
    private void useful_ores$skipVanillaMovementSync(CallbackInfo ci) {
        if (!(entity instanceof SolariteBatteryMinecartEntity)) return;
        sendDirtyEntityData();
        
        lastSentYRot = (int) Math.floor(entity.getYRot() * 256.0F / 360.0F);
        lastSentXRot = (int) Math.floor(entity.getXRot() * 256.0F / 360.0F);
        lastSentMovement = entity.getDeltaMovement();
        entity.hasImpulse = false;
        tickCount++;
        if (entity.hurtMarked) {
            entity.hurtMarked = false;
            broadcast.accept(new ClientboundSetEntityMotionPacket(entity));
        }
        ci.cancel();
    }
}
