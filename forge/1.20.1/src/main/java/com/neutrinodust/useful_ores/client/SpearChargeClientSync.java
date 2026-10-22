package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.network.SpearChargeFramePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

@Mod.EventBusSubscriber(modid="useful_ores", value=Dist.CLIENT)
public final class SpearChargeClientSync {
    private SpearChargeClientSync() {}
    @SubscribeEvent
    public static void tick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft=Minecraft.getInstance();
        LocalPlayer player=minecraft.player;
        if(player==null)return;
        ItemStack stack=player.getUseItem();
        if(!player.isUsingItem()||!stack.is(SpearTags.SPEARS))return;
        Vec3 velocity20=new Vec3((player.getX()-player.xo)*20.0D,(player.getY()-player.yo)*20.0D,(player.getZ()-player.zo)*20.0D);
        if(minecraft.getConnection()!=null){
            com.neutrinodust.useful_ores.network.ModNetworking.sendToServer(new SpearChargeFramePacket(velocity20.x,velocity20.y,velocity20.z,null));
        }
    }
}
