package com.neutrinodust.useful_ores.platform;

import com.neutrinodust.useful_ores.Constants;
import com.neutrinodust.useful_ores.platform.services.IPlatformHelper;
import java.util.ServiceLoader;

public class Services {
   public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

   public static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
         .findFirst()
         .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}

