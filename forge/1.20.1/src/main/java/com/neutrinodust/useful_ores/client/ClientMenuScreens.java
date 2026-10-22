package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.ModMenuTypes;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "useful_ores", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientMenuScreens {
    private ClientMenuScreens() {}

    @SubscribeEvent
    public static void registerScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.SUPER_BEACON.get(), BeaconScreen::new);
            MenuScreens.register(ModMenuTypes.SOLARITE_FURNACE.get(), SolariteFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.ARGENTITE_FILTER.get(), ArgentiteFilterScreen::new);
        });
    }
}
