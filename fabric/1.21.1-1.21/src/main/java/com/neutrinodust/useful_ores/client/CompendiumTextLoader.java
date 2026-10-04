package com.neutrinodust.useful_ores.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompendiumTextLoader {

   private static final Map<String, List<String>> CACHE = new HashMap<>();

   public static List<String> getParagraphs(String textFile) {
      return CACHE.computeIfAbsent(textFile, CompendiumTextLoader::load);
   }

   private static List<String> load(String textFile) {
      String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();

      ResourceLocation id = ResourceLocation.tryParse("useful_ores:compendium/" + lang + "/" + textFile + ".txt");
      if (id == null || !exists(id)) {
         id = ResourceLocation.tryParse("useful_ores:compendium/en_us/" + textFile + ".txt");
      }
      if (id == null) {
         return List.of("(missing text file: " + textFile + ")");
      }

      try (BufferedReader reader = Minecraft.getInstance().getResourceManager().openAsReader(id)) {
         List<String> paragraphs = new ArrayList<>();
         StringBuilder current = new StringBuilder();
         String line;
         while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
               if (!current.isEmpty()) {
                  paragraphs.add(current.toString());
                  current.setLength(0);
               }
            } else {
               if (!current.isEmpty()) current.append(' ');
               current.append(line.trim());
            }
         }
         if (!current.isEmpty()) paragraphs.add(current.toString());
         return paragraphs;
      } catch (Exception e) {
         return List.of("(could not load text file: " + textFile + ")");
      }
   }

   private static boolean exists(ResourceLocation id) {
      try {
         Minecraft.getInstance().getResourceManager().open(id).close();
         return true;
      } catch (Exception e) {
         return false;
      }
   }
}

