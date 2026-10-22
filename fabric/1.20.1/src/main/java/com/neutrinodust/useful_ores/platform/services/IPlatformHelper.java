package com.neutrinodust.useful_ores.platform.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();

   default String getEnvironmentName() {
      return this.isDevelopmentEnvironment() ? "development" : "production";
   }
}

