package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import com.neutrinodust.useful_ores.block.SuperBeaconBlockEntity;
import com.neutrinodust.useful_ores.block.RedstoneClockBlockEntity;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlockEntity;
import com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailBlockEntity;
import com.neutrinodust.useful_ores.block.rail.EnderiumRailBlockEntity;
import com.neutrinodust.useful_ores.solar.SolarBatteryBlockEntity;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarBlockEntity;
import com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlockEntity;
import com.neutrinodust.useful_ores.filter.ArgentiteFilterBlockEntity;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlockEntity;
import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
      DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "useful_ores");

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperBeaconBlockEntity>> SUPER_BEACON =
      BLOCK_ENTITY_TYPES.register(
         "super_beacon",
         () -> BlockEntityType.Builder.of(SuperBeaconBlockEntity::new, ModItems.SUPER_BEACON.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WirelessRedstoneRelayBlockEntity>> WIRELESS_REDSTONE_RELAY =
      BLOCK_ENTITY_TYPES.register(
         "wireless_redstone_relay",
         () -> BlockEntityType.Builder.of(WirelessRedstoneRelayBlockEntity::new, ModItems.WIRELESS_REDSTONE_RELAY.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneClockBlockEntity>> REDSTONE_CLOCK =
      BLOCK_ENTITY_TYPES.register(
         "redstone_clock",
         () -> BlockEntityType.Builder.of(RedstoneClockBlockEntity::new, ModItems.REDSTONE_CLOCK.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolariteFurnaceBlockEntity>> SOLARITE_FURNACE =
      BLOCK_ENTITY_TYPES.register(
         "solarite_furnace",
         () -> BlockEntityType.Builder.of(SolariteFurnaceBlockEntity::new, ModItems.SOLARITE_FURNACE_BLOCK.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FulguriteElectricTrapBlockEntity>> FULGURITE_ELECTRIC_TRAP =
      BLOCK_ENTITY_TYPES.register(
         "fulgurite_electric_trap",
         () -> BlockEntityType.Builder.of(FulguriteElectricTrapBlockEntity::new, ModItems.FULGURITE_ELECTRIC_TRAP.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarBatteryBlockEntity>> SOLAR_BATTERY =
      BLOCK_ENTITY_TYPES.register(
         "solar_battery",
         () -> BlockEntityType.Builder.of(SolarBatteryBlockEntity::new, ModItems.SOLAR_BATTERY_BLOCK.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TitaniumControllerRailBlockEntity>> TITANIUM_CONTROLLER_RAIL =
      BLOCK_ENTITY_TYPES.register(
         "titanium_controller_rail",
         () -> BlockEntityType.Builder.of(TitaniumControllerRailBlockEntity::new, ModTitaniumRailBlocks.TITANIUM_CONTROLLER_RAIL.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderiumRailBlockEntity>> ENDERIUM_RAIL =
      BLOCK_ENTITY_TYPES.register(
         "enderium_rail",
         () -> BlockEntityType.Builder.of(EnderiumRailBlockEntity::new, ModEnderiumRailBlocks.ENDERIUM_RAIL.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaniteXpJarBlockEntity>> ARCANITE_XP_JAR =
      BLOCK_ENTITY_TYPES.register(
         "arcanite_xp_jar",
         () -> BlockEntityType.Builder.of(ArcaniteXpJarBlockEntity::new, ModItems.ARCANITE_XP_JAR_BLOCK.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NyxiumDarkBarrierBlockEntity>> NYXIUM_DARK_BARRIER =
      BLOCK_ENTITY_TYPES.register(
         "nyxium_dark_barrier",
         () -> BlockEntityType.Builder.of(NyxiumDarkBarrierBlockEntity::new, ModItems.NYXIUM_DARK_BARRIER.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientPedestalBlockEntity>> ANCIENT_PEDESTAL =
      BLOCK_ENTITY_TYPES.register(
         "ancient_pedestal",
         () -> BlockEntityType.Builder.of(AncientPedestalBlockEntity::new, ModItems.ANCIENT_PEDESTAL.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DarkWormholePortalBlockEntity>> DARK_WORMHOLE_PORTAL =
      BLOCK_ENTITY_TYPES.register(
         "dark_wormhole_portal",
         () -> BlockEntityType.Builder.of(DarkWormholePortalBlockEntity::new, ModItems.DARK_WORMHOLE_PORTAL.get()).build(null)
      );

   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArgentiteFilterBlockEntity>> ARGENTITE_FILTER =
      BLOCK_ENTITY_TYPES.register(
         "argentite_filter",
         () -> BlockEntityType.Builder.of(ArgentiteFilterBlockEntity::new, ModItems.ARGENTITE_FILTER.get()).build(null)
      );

   public static void init(IEventBus bus) {
      BLOCK_ENTITY_TYPES.register(bus);
   }
}

