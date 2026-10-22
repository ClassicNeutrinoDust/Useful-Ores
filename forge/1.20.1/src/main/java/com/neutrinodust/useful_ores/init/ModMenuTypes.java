package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.filter.ArgentiteFilterMenu;
import com.neutrinodust.useful_ores.block.SuperBeaconMenu;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, "useful_ores");

    public static final RegistryObject<MenuType<SuperBeaconMenu>> SUPER_BEACON =
            MENU_TYPES.register("super_beacon",
                    () -> IForgeMenuType.create((windowId, inv, data) -> new SuperBeaconMenu(windowId, inv)));

    public static final RegistryObject<MenuType<SolariteFurnaceMenu>> SOLARITE_FURNACE =
            MENU_TYPES.register("solarite_furnace",
                    () -> IForgeMenuType.create((windowId, inv, data) -> new SolariteFurnaceMenu(windowId, inv)));

    public static final RegistryObject<MenuType<ArgentiteFilterMenu>> ARGENTITE_FILTER =
            MENU_TYPES.register("argentite_filter",
                    () -> IForgeMenuType.create((windowId, inv, data) -> new ArgentiteFilterMenu(windowId, inv)));

    public static void init(IEventBus bus) {
        MENU_TYPES.register(bus);
    }
}

