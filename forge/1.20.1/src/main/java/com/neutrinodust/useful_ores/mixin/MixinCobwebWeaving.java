package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.attribution.ModLegacyEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WebBlock.class)
public abstract class MixinCobwebWeaving {
    @Redirect(
            method = "entityInside",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V"),
            require = 1)
    private void usefulOres$weavingSlowdown(Entity entity, BlockState state, Vec3 vanillaMultiplier) {
        if (entity instanceof LivingEntity living && living.hasEffect(ModLegacyEffects.WEAVING.get())) {

            vanillaMultiplier = vanillaMultiplier.scale(2.0D);
        }
        entity.makeStuckInBlock(state, vanillaMultiplier);
    }
}
