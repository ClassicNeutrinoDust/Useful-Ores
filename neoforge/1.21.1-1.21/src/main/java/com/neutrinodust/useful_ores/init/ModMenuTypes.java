package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.filter.ArgentiteFilterMenu;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, "useful_ores");

    public static final DeferredHolder<MenuType<?>, MenuType<SolariteFurnaceMenu>> SOLARITE_FURNACE =
            MENU_TYPES.register("solarite_furnace",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new SolariteFurnaceMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<ArgentiteFilterMenu>> ARGENTITE_FILTER =
            MENU_TYPES.register("argentite_filter",
                    () -> IMenuTypeExtension.create((windowId, inv, data) -> new ArgentiteFilterMenu(windowId, inv)));

    public static void init(IEventBus bus) {
        MENU_TYPES.register(bus);
    }
}

