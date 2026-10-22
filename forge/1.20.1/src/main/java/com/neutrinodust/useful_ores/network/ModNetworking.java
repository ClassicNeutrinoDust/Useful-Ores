package com.neutrinodust.useful_ores.network;

import com.neutrinodust.useful_ores.block.WirelessRedstoneLinking;
import com.neutrinodust.useful_ores.combat.SpearChargeCombat;
import com.neutrinodust.useful_ores.item.BackportSpearItem;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.client.ClientPacketHandlers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.eventbus.api.IEventBus;
import java.util.Optional;
import java.util.function.Supplier;

public final class ModNetworking {
 private static final String PROTOCOL = "1";
 public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath("useful_ores", "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
 private static int nextId;
 private ModNetworking() {}
 public static void init(IEventBus modBus) {
  register(CancelRelaySelectionPacket.class, CancelRelaySelectionPacket::encode, CancelRelaySelectionPacket::decode, (p,c)-> { ServerPlayer player=c.get().getSender(); if(player!=null) WirelessRedstoneLinking.cancelPending(player); }, NetworkDirection.PLAY_TO_SERVER);
  register(SolariteCartSpeedInputPacket.class, SolariteCartSpeedInputPacket::encode, SolariteCartSpeedInputPacket::decode, (p,c)-> { ServerPlayer player=c.get().getSender(); if(player!=null && player.getVehicle() instanceof SolariteBatteryMinecartEntity cart) cart.setSpeedInput(Math.max(-1,Math.min(1,p.direction()))); }, NetworkDirection.PLAY_TO_SERVER);
  register(SpearCancelChargePacket.class, SpearCancelChargePacket::encode, SpearCancelChargePacket::decode, (p,c)-> { ServerPlayer player=c.get().getSender(); if(player!=null){ if(player.isUsingItem() && player.getUseItem().getItem() instanceof BackportSpearItem) player.stopUsingItem(); SpearChargeCombat.endUse(player,player.getUseItem()); } }, NetworkDirection.PLAY_TO_SERVER);
  register(SpearJabPacket.class, SpearJabPacket::encode, SpearJabPacket::decode, (p,c)-> { ServerPlayer player=c.get().getSender(); if(player!=null) SpearChargeCombat.performJab(player); }, NetworkDirection.PLAY_TO_SERVER);
  register(SpearChargeFramePacket.class, SpearChargeFramePacket::encode, SpearChargeFramePacket::decode, (p,c)-> { ServerPlayer player=c.get().getSender(); if(player!=null) SpearChargeCombat.onClientChargeFrame(player,p); }, NetworkDirection.PLAY_TO_SERVER);
  register(SolariteCartStepsPacket.class, SolariteCartStepsPacket::encode, SolariteCartStepsPacket::decode, (p,c)-> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ClientPacketHandlers.handleSolariteCartSteps(p)), NetworkDirection.PLAY_TO_CLIENT);
  register(SyncGlowingBlocksPacket.class, SyncGlowingBlocksPacket::encode, SyncGlowingBlocksPacket::decode, (p,c)-> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ClientPacketHandlers.handleGlowingBlocks(p)), NetworkDirection.PLAY_TO_CLIENT);
  register(SyncLockedChestsPacket.class, SyncLockedChestsPacket::encode, SyncLockedChestsPacket::decode, (p,c)-> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ClientPacketHandlers.handleLockedChests(p)), NetworkDirection.PLAY_TO_CLIENT);
  register(SyncPhosgenePowderBlocksPacket.class, SyncPhosgenePowderBlocksPacket::encode, SyncPhosgenePowderBlocksPacket::decode, (p,c)-> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ClientPacketHandlers.handlePhosgeneBlocks(p)), NetworkDirection.PLAY_TO_CLIENT);
 }
 private static <T> void register(Class<T> type, java.util.function.BiConsumer<T,net.minecraft.network.FriendlyByteBuf> encoder, java.util.function.Function<net.minecraft.network.FriendlyByteBuf,T> decoder, java.util.function.BiConsumer<T,Supplier<NetworkEvent.Context>> handler, NetworkDirection direction) {
  CHANNEL.registerMessage(nextId++,type,encoder,decoder,(packet,ctx)->{ctx.get().enqueueWork(()->handler.accept(packet,ctx));ctx.get().setPacketHandled(true);},Optional.of(direction));
 }
 public static void sendToServer(Object packet){CHANNEL.sendToServer(packet);}
 public static void sendToPlayer(ServerPlayer player,Object packet){CHANNEL.send(PacketDistributor.PLAYER.with(()->player),packet);}
 public static void sendToDimension(ServerLevel level,Object packet){CHANNEL.send(PacketDistributor.DIMENSION.with(level::dimension),packet);}
 public static void sendToTracking(Entity entity,Object packet){CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(()->entity),packet);}
}
