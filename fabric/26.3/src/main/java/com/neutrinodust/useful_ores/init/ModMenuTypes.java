package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.filter.ArgentiteFilterMenu;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

   public static final MenuType<SolariteFurnaceMenu> SOLARITE_FURNACE = Registry.register(
      BuiltInRegistries.MENU,
      Identifier.fromNamespaceAndPath(ModRegisters.MODID, "solarite_furnace"),
      new MenuType<>(SolariteFurnaceMenu::new, FeatureFlags.VANILLA_SET)
   );

   public static final MenuType<ArgentiteFilterMenu> ARGENTITE_FILTER = Registry.register(
      BuiltInRegistries.MENU,
      Identifier.fromNamespaceAndPath(ModRegisters.MODID, "argentite_filter"),
      new MenuType<>(ArgentiteFilterMenu::new, FeatureFlags.VANILLA_SET)
   );

   public static void init() {
   }
}

