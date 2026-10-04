package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.armor.OsmiumArmorEvents;
import com.neutrinodust.useful_ores.attribution.AttributedArmorEvents;
import com.neutrinodust.useful_ores.blastproof.BlastproofHudEvents;
import com.neutrinodust.useful_ores.block.rail.EnderiumRailHudEvents;
import com.neutrinodust.useful_ores.lighting.VoidshardDarknessHandler;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayHudEvents;
import com.neutrinodust.useful_ores.entity.SolariteMinecartHudEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayerTick {

    @Inject(method = "tick", at = @At("HEAD"))
    private void usefulOres$osmiumTickPre(CallbackInfo ci) {
        OsmiumArmorEvents.INSTANCE.onPlayerTickPre((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void usefulOres$osmiumTickPost(CallbackInfo ci) {
        OsmiumArmorEvents.INSTANCE.onPlayerTickPost((Player) (Object) this);
        BlastproofHudEvents.INSTANCE.onPlayerTickPost((Player) (Object) this);
        EnderiumRailHudEvents.INSTANCE.onPlayerTickPost((Player) (Object) this);
        AttributedArmorEvents.INSTANCE.onPlayerTick((Player) (Object) this);
        VoidshardDarknessHandler.INSTANCE.onPlayerTick((Player) (Object) this);
        SolariteMinecartHudEvents.INSTANCE.onPlayerTick((Player) (Object) this);
        WirelessRedstoneRelayHudEvents.INSTANCE.onPlayerTickPost((Player) (Object) this);
    }
}

