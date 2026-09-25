package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.attribution.AttributedEffects;
import com.neutrinodust.useful_ores.attribution.ModAttributedItems;
import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ModTab {
   public static final CreativeModeTab TAB_USEFUL_ORES = Registry.register(
      BuiltInRegistries.CREATIVE_MODE_TAB,
      Identifier.fromNamespaceAndPath(ModRegisters.MODID, "tab_useful_ores"),
      FabricCreativeModeTab.builder()
         .icon(() -> new ItemStack(ModItems.ARCANITE_ITEMS.get(1).asItem()))
         .displayItems((features, entries) -> {
            for (Item it : BuiltInRegistries.ITEM) {
               if (!BuiltInRegistries.ITEM.getKey(it).getNamespace().equals(ModRegisters.MODID)) continue;

               if (isAttributedItem(it)) continue;

               if (it == ModItems.ELECTRIC_RING.get()) continue;
               entries.accept((ItemLike) it);
            }

            for (var attributedItem : ModAttributedItems.ALL) {
               Item it = attributedItem.get();
               for (AttributedEffects effect : AttributedEffects.values()) {
                  ItemStack stack = new ItemStack(it);
                  stack.set(ModDataComponents.ATTRIBUTED_EFFECT, effect.id());
                  entries.accept(stack);
               }
            }
         })
         .title(Component.translatable("item_group.useful_ores.tab_useful_ores"))
         .build()
   );

   private static boolean isAttributedItem(Item item) {
      for (var attributedItem : ModAttributedItems.ALL) {
         if (attributedItem.get() == item) return true;
      }
      return false;
   }

   public static void init() {
   }
}

