package com.neutrinodust.useful_ores;

import com.neutrinodust.useful_ores.attribution.ModAttributedItems;
import com.neutrinodust.useful_ores.attribution.ModAttributionRecipes;
import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.barrier.ModBarrierSounds;
import com.neutrinodust.useful_ores.barrier.NyxiumBarrierBreakEvents;
import com.neutrinodust.useful_ores.bioluminescence.BioluminescentAxeEvents;
import com.neutrinodust.useful_ores.bioluminescence.BioluminescentEvents;
import com.neutrinodust.useful_ores.bioluminescence.BioluminescentSyncEvents;
import com.neutrinodust.useful_ores.blastproof.BlastproofEvents;
import com.neutrinodust.useful_ores.block.WirelessRedstoneRelayHudEvents;
import com.neutrinodust.useful_ores.block.WirelessRelayGossipTicker;
import com.neutrinodust.useful_ores.block.rail.EnderiumRailLaunchEnforcer;
import com.neutrinodust.useful_ores.event.OreDropLootEvents;
import com.neutrinodust.useful_ores.event.OreFireIgnitionEvents;
import com.neutrinodust.useful_ores.farseeker.FarseekerLootEvents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerComponents;
import com.neutrinodust.useful_ores.farseeker.ModFarseekerRecipes;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks;
import com.neutrinodust.useful_ores.init.ModConfig;
import com.neutrinodust.useful_ores.init.ModEnderiumRailBlocks;
import com.neutrinodust.useful_ores.init.ModEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.init.ModMaterials;
import com.neutrinodust.useful_ores.init.ModMenuTypes;
import com.neutrinodust.useful_ores.init.ModOreFireBlocks;
import com.neutrinodust.useful_ores.init.ModSolariteRailBlocks;
import com.neutrinodust.useful_ores.init.ModStructurePieceTypes;
import com.neutrinodust.useful_ores.init.ModStructureTypes;
import com.neutrinodust.useful_ores.init.ModTab;
import com.neutrinodust.useful_ores.init.ModTitaniumRailBlocks;
import com.neutrinodust.useful_ores.lighting.ModOreLightBlocks;
import com.neutrinodust.useful_ores.lighting.ModVoidshardLightBlocks;
import com.neutrinodust.useful_ores.lock.ChestLockEvents;
import com.neutrinodust.useful_ores.lock.ModLockComponents;
import com.neutrinodust.useful_ores.network.ModNetworking;
import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import com.neutrinodust.useful_ores.pedestal.ModPedestalComponents;
import com.neutrinodust.useful_ores.pedestal.PedestalBreakEvents;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalMapLootEvents;
import com.neutrinodust.useful_ores.phosgene.PhosgenePowderEvents;
import com.neutrinodust.useful_ores.phosgene.ModPhosgeneRecipes;
import com.neutrinodust.useful_ores.solar.ModSolarComponents;
import com.neutrinodust.useful_ores.xpjar.ModXpJarComponents;
import com.neutrinodust.useful_ores.worldgen.ModBiomeModifiers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import com.neutrinodust.useful_ores.painite.PainiteMobEquipment;
import com.neutrinodust.useful_ores.overworld.OverworldMobEquipment;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class UsefulOresMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");

        ModConfig.register();
        com.neutrinodust.useful_ores.block.WirelessRedstoneRelayLifecycleEvents.register();

        ModMaterials.onConfigLoad();

        CommonClass.init();
        ModItems.init();
        ModEntities.init();
        ModStructureTypes.init();
        ModStructurePieceTypes.init();
        ModTab.init();

        ModBiomeModifiers.init();
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> PainiteMobEquipment.onEntityLoad(entity, world));
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> OverworldMobEquipment.onEntityLoad(entity, world));

        ModDataComponents.init();
        ModAttributionRecipes.init();
        ModPhosgeneRecipes.init();
        ModAttributedItems.init();

        ModFarseekerComponents.init();
        ModFarseekerRecipes.init();
        FarseekerLootEvents.register();
        AncientPedestalMapLootEvents.register();

        OreDropLootEvents.register();

        ModParticleTypes.init();
        ModVoidshardLightBlocks.init();
        ModOreLightBlocks.init();

        ModBlockEntities.init();

        UseBlockCallback.EVENT.register(new BlastproofEvents()::onUseBlock);

        PlayerBlockBreakEvents.AFTER.register(new BioluminescentEvents()::onBlockBreak);
        UseBlockCallback.EVENT.register(new BioluminescentAxeEvents()::onUseBlock);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                BioluminescentSyncEvents.sendFullSync(handler.getPlayer()));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                com.neutrinodust.useful_ores.compendium.CompendiumFirstJoinEvents.onLogin(handler.getPlayer()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                com.neutrinodust.useful_ores.phosgene.PhosgenePowderSync.sendFullSync(handler.getPlayer()));
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) ->
                com.neutrinodust.useful_ores.phosgene.PhosgenePowderSync.sendFullSync(player));

        ModNetworking.init();
        UseBlockCallback.EVENT.register(WirelessRedstoneRelayHudEvents.INSTANCE::onUseBlock);

        ServerTickEvents.END_SERVER_TICK.register(WirelessRelayGossipTicker::driveTick);

        ModSolarComponents.init();
        ModMenuTypes.init();

        ModBarrierSounds.init();
        ModXpJarComponents.init();

        ModOreFireBlocks.init();
        ModOreFireBlocks.buildIgnitionMap();
        UseBlockCallback.EVENT.register(new OreFireIgnitionEvents()::onUseBlock);

        ModColoredCampfireBlocks.init();

        ModTitaniumRailBlocks.init();
        ModEnderiumRailBlocks.init();
        ModSolariteRailBlocks.init();
        ServerTickEvents.END_SERVER_TICK.register(new EnderiumRailLaunchEnforcer()::onServerTick);

        ModLockComponents.init();
        UseBlockCallback.EVENT.register(new ChestLockEvents()::onUseBlock);
        PlayerBlockBreakEvents.BEFORE.register(new ChestLockEvents()::onBeforeBreak);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                com.neutrinodust.useful_ores.lock.ChestLockSyncEvents.sendFullSync(handler.getPlayer()));

        PlayerBlockBreakEvents.BEFORE.register(new NyxiumBarrierBreakEvents()::onBeforeBreak);

        ModPedestalComponents.init();
        PhosgenePowderEvents.register();
        PlayerBlockBreakEvents.BEFORE.register(new PedestalBreakEvents()::onBeforeBreak);
    }
}

