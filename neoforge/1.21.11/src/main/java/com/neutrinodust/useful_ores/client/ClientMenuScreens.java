package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public class ClientMenuScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SOLARITE_FURNACE.get(), SolariteFurnaceScreen::new);
        event.register(ModMenuTypes.ARGENTITE_FILTER.get(), ArgentiteFilterScreen::new);
    }
}

