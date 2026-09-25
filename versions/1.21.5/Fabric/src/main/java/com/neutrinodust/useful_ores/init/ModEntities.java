package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.entity.MeteorBombEntity;
import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
   private static final String MODID = ModRegisters.MODID;

   public static final EntityType<MeteoriteProjectile> METEORITE_PROJECTILE = register(
      "meteorite_projectile",
      key -> EntityType.Builder.<MeteoriteProjectile>of(MeteoriteProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .build(key)
   );

   public static final EntityType<MeteorBombEntity> METEOR_BOMB = register(
      "meteor_bomb",
      key -> EntityType.Builder.<MeteorBombEntity>of(MeteorBombEntity::new, MobCategory.MISC)
         .sized(0.35F, 0.35F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .build(key)
   );

   public static final EntityType<SolariteBatteryMinecartEntity> SOLARITE_BATTERY_MINECART = register(
      "solarite_battery_minecart",
      key -> EntityType.Builder.<SolariteBatteryMinecartEntity>of(SolariteBatteryMinecartEntity::new, MobCategory.MISC)
         .sized(0.98F, 0.7F)
            .passengerAttachments(0.2F)
         .clientTrackingRange(8)
         .updateInterval(3)
         .build(key)
   );

   public static final EntityType<com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile> NYXIUMNITE_CUBE_PROJECTILE = register(
      "nyxiumnite_cube_projectile",
      key -> EntityType.Builder.<com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile>of(
            com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile::new, MobCategory.MISC)
         .sized(0.35F, 0.35F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .build(key)
   );

   private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(
      String name, java.util.function.Function<ResourceKey<EntityType<?>>, EntityType<T>> builder
   ) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, name);
      ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
      return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.apply(key));
   }

   public static void init() {
   }
}

