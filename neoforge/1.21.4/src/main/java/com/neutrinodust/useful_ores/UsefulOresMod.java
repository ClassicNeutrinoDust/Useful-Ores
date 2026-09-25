package com.neutrinodust.useful_ores;

import com.neutrinodust.useful_ores.attribution.AttributedArmorEvents;
import com.neutrinodust.useful_ores.armor.OsmiumArmorEvents;
import com.neutrinodust.useful_ores.armor.LonsdaleiteArmorEvents;
import com.neutrinodust.useful_ores.bioluminescence.BioluminescentAxeEvents;
import com.neutrinodust.useful_ores.bioluminescence.BioluminescentEvents;
import com.neutrinodust.useful_ores.bioluminescence.BioluminescentSyncEvents;
import com.neutrinodust.useful_ores.blastproof.BlastproofEvents;
import com.neutrinodust.useful_ores.blastproof.BlastproofHudEvents;
import com.neutrinodust.useful_ores.attribution.ModAttributedItems;
import com.neutrinodust.useful_ores.attribution.ModAttributionRecipes;
import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayHudEvents;
import com.neutrinodust.useful_ores.block.WirelessRelayGossipTicker;
import com.neutrinodust.useful_ores.network.ModNetworking;
import com.neutrinodust.useful_ores.phosgene.ModPhosgeneRecipes;
import com.neutrinodust.useful_ores.phosgene.PhosgenePowderEvents;
import com.neutrinodust.useful_ores.farseeker.FarseekerBlockEvents;
import com.neutrinodust.useful_ores.farseeker.FarseekerLootEvents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerComponents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerRecipes;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalMapLootEvents;
import com.neutrinodust.useful_ores.event.OreDropLootEvents;
import com.neutrinodust.useful_ores.event.OreFireIgnitionEvents;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.init.ModConfig;
import com.neutrinodust.useful_ores.init.ModEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.init.ModMaterials;
import com.neutrinodust.useful_ores.init.ModOreFireBlocks;
import com.neutrinodust.useful_ores.init.ModSpearSounds;
import com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks;
import com.neutrinodust.useful_ores.lock.ChestLockEvents;
import com.neutrinodust.useful_ores.lock.ModLockComponents;
import com.neutrinodust.useful_ores.init.ModStructurePieceTypes;
import com.neutrinodust.useful_ores.init.ModTitaniumRailBlocks;
import com.neutrinodust.useful_ores.init.ModStructureTypes;
import com.neutrinodust.useful_ores.init.ModTab;
import com.neutrinodust.useful_ores.lighting.ModVoidshardLightBlocks;
import com.neutrinodust.useful_ores.lighting.VoidshardDarknessHandler;
import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import com.neutrinodust.useful_ores.init.ModMenuTypes;
import com.neutrinodust.useful_ores.solar.ModSolarComponents;
import com.neutrinodust.useful_ores.solar.SolarBatteryEvents;
import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarEvents;
import com.neutrinodust.useful_ores.xpjar.ModXpJarComponents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import com.neutrinodust.useful_ores.painite.PainiteMobEquipment;
import com.neutrinodust.useful_ores.overworld.OverworldMobEquipment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("useful_ores")
public class UsefulOresMod {
   public UsefulOresMod(IEventBus eventBus, ModContainer container) {
      Constants.LOG.info("Hello NeoForge world!");

      ModConfig.register(container);

      ModMaterials.onConfigLoad();

      CommonClass.init();
      ModItems.init(eventBus);
      ModEntities.init(eventBus);
      ModStructureTypes.init(eventBus);
      ModStructurePieceTypes.init(eventBus);
      ModTab.init(eventBus);

      ModDataComponents.init(eventBus);
      ModAttributionRecipes.init(eventBus);
      ModPhosgeneRecipes.init(eventBus);
      ModAttributedItems.init(eventBus);
      NeoForge.EVENT_BUS.register(new AttributedArmorEvents());
      NeoForge.EVENT_BUS.register(new OsmiumArmorEvents());
      NeoForge.EVENT_BUS.register(new LonsdaleiteArmorEvents());
      NeoForge.EVENT_BUS.addListener(PainiteMobEquipment::onEntityJoinLevel);
      NeoForge.EVENT_BUS.addListener(OverworldMobEquipment::onEntityJoinLevel);

      ModFarseekerComponents.init(eventBus);
      ModFarseekerRecipes.init(eventBus);
      NeoForge.EVENT_BUS.register(new FarseekerBlockEvents());
      NeoForge.EVENT_BUS.register(new FarseekerLootEvents());
      com.neutrinodust.useful_ores.pedestal.AncientPedestalMapLootFunction.init(eventBus);
      NeoForge.EVENT_BUS.register(new AncientPedestalMapLootEvents());
      NeoForge.EVENT_BUS.register(new OreDropLootEvents());

      ModParticleTypes.init(eventBus);
      ModVoidshardLightBlocks.init(eventBus);
      com.neutrinodust.useful_ores.lighting.ModOreLightBlocks.init(eventBus);
      NeoForge.EVENT_BUS.register(new VoidshardDarknessHandler());

      ModBlockEntities.init(eventBus);

      NeoForge.EVENT_BUS.register(new BlastproofEvents());
      NeoForge.EVENT_BUS.register(new BlastproofHudEvents());

      NeoForge.EVENT_BUS.register(new BioluminescentEvents());
      NeoForge.EVENT_BUS.register(new BioluminescentSyncEvents());
      NeoForge.EVENT_BUS.register(new BioluminescentAxeEvents());
      NeoForge.EVENT_BUS.register(new PhosgenePowderEvents());

      ModSpearSounds.init(eventBus);
      ModNetworking.init(eventBus);
      NeoForge.EVENT_BUS.register(new WirelessRedstoneRelayHudEvents());
      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.block.rail.EnderiumRailHudEvents());
      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.entity.SolariteMinecartHudEvents());

      NeoForge.EVENT_BUS.register(new WirelessRelayGossipTicker());

      ModSolarComponents.init(eventBus);
      ModMenuTypes.init(eventBus);
      NeoForge.EVENT_BUS.register(new SolarBatteryEvents());

      ModXpJarComponents.init(eventBus);
      NeoForge.EVENT_BUS.register(new ArcaniteXpJarEvents());
      com.neutrinodust.useful_ores.solar.SolariteFurnaceCapabilities.init(eventBus);

      ModOreFireBlocks.init(eventBus);
      eventBus.addListener((FMLCommonSetupEvent event) -> ModOreFireBlocks.buildIgnitionMap());
      NeoForge.EVENT_BUS.register(new OreFireIgnitionEvents());

      ModColoredCampfireBlocks.init(eventBus);

      ModTitaniumRailBlocks.init(eventBus);
      com.neutrinodust.useful_ores.init.ModEnderiumRailBlocks.init(eventBus);
      com.neutrinodust.useful_ores.init.ModSolariteRailBlocks.init(eventBus);

      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailGlobalEvents());
      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.block.rail.EnderiumRailLaunchEnforcer());

      ModLockComponents.init(eventBus);
      NeoForge.EVENT_BUS.register(new ChestLockEvents());
      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.lock.ChestLockSyncEvents());

      com.neutrinodust.useful_ores.barrier.ModBarrierSounds.init(eventBus);
      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.barrier.NyxiumBarrierBreakEvents());

      com.neutrinodust.useful_ores.pedestal.ModPedestalComponents.init(eventBus);
      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.pedestal.PedestalBreakEvents());

      NeoForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.compendium.CompendiumFirstJoinEvents());
   }
}

