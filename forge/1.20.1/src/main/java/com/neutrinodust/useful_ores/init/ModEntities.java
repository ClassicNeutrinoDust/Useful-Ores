package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.entity.MeteorBombEntity;
import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModEntities {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(Registries.ENTITY_TYPE, "useful_ores");

   public static final RegistryObject<EntityType<MeteoriteProjectile>> METEORITE_PROJECTILE =
      ENTITY_TYPES.register("meteorite_projectile",
         () -> EntityType.Builder.<MeteoriteProjectile>of(MeteoriteProjectile::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build("meteorite_projectile"));

   public static final RegistryObject<EntityType<MeteorBombEntity>> METEOR_BOMB =
      ENTITY_TYPES.register("meteor_bomb",
         () -> EntityType.Builder.<MeteorBombEntity>of(MeteorBombEntity::new, MobCategory.MISC)
            .sized(0.35F, 0.35F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build("meteor_bomb"));

   public static final RegistryObject<EntityType<SolariteBatteryMinecartEntity>> SOLARITE_BATTERY_MINECART =
      ENTITY_TYPES.register("solarite_battery_minecart",
         () -> EntityType.Builder.<SolariteBatteryMinecartEntity>of(SolariteBatteryMinecartEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.7F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build("solarite_battery_minecart"));

   public static final RegistryObject<EntityType<NyxiumniteCubeProjectile>> NYXIUMNITE_CUBE_PROJECTILE =
      ENTITY_TYPES.register("nyxiumnite_cube_projectile",
         () -> EntityType.Builder.<NyxiumniteCubeProjectile>of(NyxiumniteCubeProjectile::new, MobCategory.MISC)
            .sized(0.35F, 0.35F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build("nyxiumnite_cube_projectile"));

   public static final RegistryObject<EntityType<com.neutrinodust.useful_ores.item.ScheeliteArrowItem.ScheeliteArrow>> SCHEELITE_ARROW =
      ENTITY_TYPES.register("scheelite_arrow",
         () -> EntityType.Builder.<com.neutrinodust.useful_ores.item.ScheeliteArrowItem.ScheeliteArrow>of(
               com.neutrinodust.useful_ores.item.ScheeliteArrowItem.ScheeliteArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("scheelite_arrow"));

   public static final RegistryObject<EntityType<com.neutrinodust.useful_ores.item.ArcaniteArrowItem.ArcaniteArrow>> ARCANITE_ARROW =
      ENTITY_TYPES.register("arcanite_arrow",
         () -> EntityType.Builder.<com.neutrinodust.useful_ores.item.ArcaniteArrowItem.ArcaniteArrow>of(
               com.neutrinodust.useful_ores.item.ArcaniteArrowItem.ArcaniteArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("arcanite_arrow"));

   public static final RegistryObject<EntityType<com.neutrinodust.useful_ores.item.NyxiumArrowItem.NyxiumArrow>> NYXIUM_ARROW =
      ENTITY_TYPES.register("nyxium_arrow",
         () -> EntityType.Builder.<com.neutrinodust.useful_ores.item.NyxiumArrowItem.NyxiumArrow>of(
               com.neutrinodust.useful_ores.item.NyxiumArrowItem.NyxiumArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("nyxium_arrow"));

   public static void init(IEventBus bus) {
      ENTITY_TYPES.register(bus);
   }
}

