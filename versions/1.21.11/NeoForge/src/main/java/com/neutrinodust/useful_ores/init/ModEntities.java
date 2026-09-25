package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.entity.MeteorBombEntity;
import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(Registries.ENTITY_TYPE, "useful_ores");

   public static final DeferredHolder<EntityType<?>, EntityType<MeteoriteProjectile>> METEORITE_PROJECTILE =
      ENTITY_TYPES.register("meteorite_projectile",
         () -> EntityType.Builder.<MeteoriteProjectile>of(MeteoriteProjectile::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(ResourceKey.create(Registries.ENTITY_TYPE,
               Identifier.fromNamespaceAndPath("useful_ores", "meteorite_projectile"))));

   public static final DeferredHolder<EntityType<?>, EntityType<MeteorBombEntity>> METEOR_BOMB =
      ENTITY_TYPES.register("meteor_bomb",
         () -> EntityType.Builder.<MeteorBombEntity>of(MeteorBombEntity::new, MobCategory.MISC)
            .sized(0.35F, 0.35F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(ResourceKey.create(Registries.ENTITY_TYPE,
               Identifier.fromNamespaceAndPath("useful_ores", "meteor_bomb"))));

   public static final DeferredHolder<EntityType<?>, EntityType<SolariteBatteryMinecartEntity>> SOLARITE_BATTERY_MINECART =
      ENTITY_TYPES.register("solarite_battery_minecart",
         () -> EntityType.Builder.<SolariteBatteryMinecartEntity>of(SolariteBatteryMinecartEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.7F)
            .passengerAttachments(0.2F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .build(ResourceKey.create(Registries.ENTITY_TYPE,
               Identifier.fromNamespaceAndPath("useful_ores", "solarite_battery_minecart"))));

   public static final DeferredHolder<EntityType<?>, EntityType<NyxiumniteCubeProjectile>> NYXIUMNITE_CUBE_PROJECTILE =
      ENTITY_TYPES.register("nyxiumnite_cube_projectile",
         () -> EntityType.Builder.<NyxiumniteCubeProjectile>of(NyxiumniteCubeProjectile::new, MobCategory.MISC)
            .sized(0.35F, 0.35F)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build(ResourceKey.create(Registries.ENTITY_TYPE,
               Identifier.fromNamespaceAndPath("useful_ores", "nyxiumnite_cube_projectile"))));

   public static void init(IEventBus bus) {
      ENTITY_TYPES.register(bus);
   }
}

