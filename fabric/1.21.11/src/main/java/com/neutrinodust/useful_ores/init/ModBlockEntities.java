package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlockEntity;
import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import com.neutrinodust.useful_ores.block.SuperBeaconBlockEntity;
import com.neutrinodust.useful_ores.block.RedstoneClockBlockEntity;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlockEntity;
import com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailBlockEntity;
import com.neutrinodust.useful_ores.block.rail.EnderiumRailBlockEntity;
import com.neutrinodust.useful_ores.filter.ArgentiteFilterBlockEntity;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlockEntity;
import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import com.neutrinodust.useful_ores.solar.SolarBatteryBlockEntity;
import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarBlockEntity;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
   private static <T extends BlockEntity> BlockEntityType<T> register(
      String name, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks
   ) {
      BlockEntityType<T> type = FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build();
      return Registry.register(
         BuiltInRegistries.BLOCK_ENTITY_TYPE,
         Identifier.fromNamespaceAndPath(ModRegisters.MODID, name),
         type
      );
   }

   public static final BlockEntityType<SuperBeaconBlockEntity> SUPER_BEACON = register(
      "super_beacon", SuperBeaconBlockEntity::new, ModItems.SUPER_BEACON.get()
   );

   public static final BlockEntityType<WirelessRedstoneRelayBlockEntity> WIRELESS_REDSTONE_RELAY = register(
      "wireless_redstone_relay", WirelessRedstoneRelayBlockEntity::new, ModItems.WIRELESS_REDSTONE_RELAY.get()
   );

   public static final BlockEntityType<RedstoneClockBlockEntity> REDSTONE_CLOCK = register(
      "redstone_clock", RedstoneClockBlockEntity::new, ModItems.REDSTONE_CLOCK.get()
   );

   public static final BlockEntityType<SolariteFurnaceBlockEntity> SOLARITE_FURNACE = register(
      "solarite_furnace", SolariteFurnaceBlockEntity::new, ModItems.SOLARITE_FURNACE_BLOCK.get()
   );

   public static final BlockEntityType<com.neutrinodust.useful_ores.solar.SolariteFurnacePartBlockEntity> SOLARITE_FURNACE_PART = register(
      "solarite_furnace_part", com.neutrinodust.useful_ores.solar.SolariteFurnacePartBlockEntity::new, ModItems.SOLARITE_FURNACE_PART.get()
   );

   public static final BlockEntityType<NyxiumDarkBarrierBlockEntity> NYXIUM_DARK_BARRIER = register(
      "nyxium_dark_barrier", NyxiumDarkBarrierBlockEntity::new, ModItems.NYXIUM_DARK_BARRIER.get()
   );

   public static final BlockEntityType<ArgentiteFilterBlockEntity> ARGENTITE_FILTER = register(
      "argentite_filter", ArgentiteFilterBlockEntity::new, ModItems.ARGENTITE_FILTER.get()
   );

   public static final BlockEntityType<ArcaniteXpJarBlockEntity> ARCANITE_XP_JAR = register(
      "arcanite_xp_jar", ArcaniteXpJarBlockEntity::new, ModItems.ARCANITE_XP_JAR_BLOCK.get()
   );

   public static final BlockEntityType<AncientPedestalBlockEntity> ANCIENT_PEDESTAL = register(
      "ancient_pedestal", AncientPedestalBlockEntity::new, ModItems.ANCIENT_PEDESTAL.get()
   );

   public static final BlockEntityType<DarkWormholePortalBlockEntity> DARK_WORMHOLE_PORTAL = register(
      "dark_wormhole_portal", DarkWormholePortalBlockEntity::new, ModItems.DARK_WORMHOLE_PORTAL.get()
   );

   public static final BlockEntityType<FulguriteElectricTrapBlockEntity> FULGURITE_ELECTRIC_TRAP = register(
      "fulgurite_electric_trap", FulguriteElectricTrapBlockEntity::new, ModItems.FULGURITE_ELECTRIC_TRAP.get()
   );

   public static final BlockEntityType<SolarBatteryBlockEntity> SOLAR_BATTERY = register(
      "solar_battery", SolarBatteryBlockEntity::new, ModItems.SOLAR_BATTERY_BLOCK.get()
   );

   public static final BlockEntityType<TitaniumControllerRailBlockEntity> TITANIUM_CONTROLLER_RAIL = register(
      "titanium_controller_rail", TitaniumControllerRailBlockEntity::new, ModTitaniumRailBlocks.TITANIUM_CONTROLLER_RAIL.get()
   );

   public static final BlockEntityType<EnderiumRailBlockEntity> ENDERIUM_RAIL = register(
      "enderium_rail", EnderiumRailBlockEntity::new, ModEnderiumRailBlocks.ENDERIUM_RAIL.get()
   );

   public static void init() {
   }
}

