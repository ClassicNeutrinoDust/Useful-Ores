package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.network.SpearChargeFramePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;


@EventBusSubscriber(modid="useful_ores", value=Dist.CLIENT)
public final class SpearChargeClientSync {
    private SpearChargeClientSync() {}
    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        Minecraft minecraft=Minecraft.getInstance();
        LocalPlayer player=minecraft.player;
        if(player==null)return;
        ItemStack stack=player.getUseItem();
        if(!player.isUsingItem()||!stack.is(SpearTags.SPEARS))return;
        Vec3 velocity20=new Vec3((player.getX()-player.xo)*20.0D,(player.getY()-player.yo)*20.0D,(player.getZ()-player.zo)*20.0D);
        if(minecraft.getConnection()!=null){
            minecraft.getConnection().send(new ServerboundCustomPayloadPacket(new SpearChargeFramePacket(velocity20.x,velocity20.y,velocity20.z,null)));
        }
    }
}
