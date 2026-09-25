package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientMenuScreens {

    public static void register() {
        MenuScreens.register(ModMenuTypes.SOLARITE_FURNACE, SolariteFurnaceScreen::new);
        MenuScreens.register(ModMenuTypes.ARGENTITE_FILTER, ArgentiteFilterScreen::new);
    }

    private ClientMenuScreens() {}
}

