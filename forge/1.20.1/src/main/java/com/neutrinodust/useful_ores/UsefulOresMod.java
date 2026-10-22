package com.neutrinodust.useful_ores;

import com.neutrinodust.useful_ores.attribution.AttributedArmorEvents;
import com.neutrinodust.useful_ores.armor.OsmiumArmorEvents;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import com.neutrinodust.useful_ores.painite.PainiteMobEquipment;
import com.neutrinodust.useful_ores.overworld.OverworldMobEquipment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod("useful_ores")
public class UsefulOresMod {
   @SuppressWarnings("removal")
   public UsefulOresMod() {
      IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
      Constants.LOG.info("Initializing Useful Ores for Forge 1.20.1");

      ModConfig.register();
      com.neutrinodust.useful_ores.config.ModConditions.init();
      com.neutrinodust.useful_ores.config.ModBiomeModifierTypes.init(eventBus);
      com.neutrinodust.useful_ores.attribution.ModLegacyEffects.init(eventBus);
      com.neutrinodust.useful_ores.attribution.ModLegacyPotions.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.attribution.ModLegacyEffectEvents());

      ModMaterials.onConfigLoad();
      ModMaterials.registerArmorMaterials(eventBus);

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
      MinecraftForge.EVENT_BUS.register(new AttributedArmorEvents());
      MinecraftForge.EVENT_BUS.register(new OsmiumArmorEvents());
      MinecraftForge.EVENT_BUS.addListener(PainiteMobEquipment::onEntityJoinLevel);
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.painite.PainiteBreakSpeedEvents());
      MinecraftForge.EVENT_BUS.addListener(OverworldMobEquipment::onEntityJoinLevel);

      ModFarseekerComponents.init(eventBus);
      ModFarseekerRecipes.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new FarseekerLootEvents());
      com.neutrinodust.useful_ores.pedestal.AncientPedestalMapLootFunction.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new AncientPedestalMapLootEvents());
      MinecraftForge.EVENT_BUS.register(new OreDropLootEvents());

      ModParticleTypes.init(eventBus);
      ModVoidshardLightBlocks.init(eventBus);
      com.neutrinodust.useful_ores.lighting.ModOreLightBlocks.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new VoidshardDarknessHandler());

      ModBlockEntities.init(eventBus);

      MinecraftForge.EVENT_BUS.register(new BlastproofEvents());
      MinecraftForge.EVENT_BUS.register(new BlastproofHudEvents());

      MinecraftForge.EVENT_BUS.register(new BioluminescentEvents());
      MinecraftForge.EVENT_BUS.register(new BioluminescentSyncEvents());
      MinecraftForge.EVENT_BUS.register(new BioluminescentAxeEvents());
      MinecraftForge.EVENT_BUS.register(new PhosgenePowderEvents());

      ModSpearSounds.init(eventBus);
      ModNetworking.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new WirelessRedstoneRelayHudEvents());
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.block.rail.EnderiumRailHudEvents());
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.entity.SolariteMinecartHudEvents());

      MinecraftForge.EVENT_BUS.register(new WirelessRelayGossipTicker());

      ModSolarComponents.init(eventBus);
      ModMenuTypes.init(eventBus);

      ModXpJarComponents.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new ArcaniteXpJarEvents());
      com.neutrinodust.useful_ores.solar.SolariteFurnaceCapabilities.init(eventBus);

      ModOreFireBlocks.init(eventBus);
      eventBus.addListener((FMLCommonSetupEvent event) -> ModOreFireBlocks.buildIgnitionMap());
      MinecraftForge.EVENT_BUS.register(new OreFireIgnitionEvents());

      ModColoredCampfireBlocks.init(eventBus);

      ModTitaniumRailBlocks.init(eventBus);
      com.neutrinodust.useful_ores.init.ModEnderiumRailBlocks.init(eventBus);
      com.neutrinodust.useful_ores.init.ModSolariteRailBlocks.init(eventBus);

      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailGlobalEvents());
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.block.rail.EnderiumRailLaunchEnforcer());

      ModLockComponents.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new ChestLockEvents());
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.lock.ChestLockSyncEvents());

      com.neutrinodust.useful_ores.barrier.ModBarrierSounds.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.barrier.NyxiumBarrierBreakEvents());

      com.neutrinodust.useful_ores.pedestal.ModPedestalComponents.init(eventBus);
      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.pedestal.PedestalBreakEvents());

      MinecraftForge.EVENT_BUS.register(new com.neutrinodust.useful_ores.compendium.CompendiumFirstJoinEvents());
   }
}

