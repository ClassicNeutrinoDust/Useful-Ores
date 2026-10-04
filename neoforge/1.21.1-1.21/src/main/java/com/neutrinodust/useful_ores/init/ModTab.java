package com.neutrinodust.useful_ores.init;

import java.util.function.Supplier;
import com.neutrinodust.useful_ores.attribution.AttributedEffects;
import com.neutrinodust.useful_ores.attribution.ModAttributedItems;
import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTab {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "useful_ores");
   public static final Supplier<CreativeModeTab> TAB_USEFUL_ORES = TABS.register(
      "tab_useful_ores",
      () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ARCANITE_ITEMS.get(1).asItem())).displayItems((features, event) -> {
         for (DeferredHolder<Item, ? extends Item> item : ModRegisters.ITEMS.getEntries()) {
            Item it = item.get();

            if (isAttributedItem(it)) continue;

            if (it == ModItems.ELECTRIC_RING.get()) continue;
            event.accept((ItemLike) it);
         }

         for (var attributedItem : ModAttributedItems.ALL) {
            Item it = attributedItem.get();
            for (AttributedEffects effect : AttributedEffects.values()) {
               ItemStack stack = new ItemStack(it);
               stack.set(ModDataComponents.ATTRIBUTED_EFFECT.get(), effect.id());
               event.accept(stack);
            }
         }
      }).title(Component.translatable("item_group.useful_ores.tab_useful_ores")).withSearchBar().build()
   );

   private static boolean isAttributedItem(Item item) {
      for (var attributedItem : ModAttributedItems.ALL) {
         if (attributedItem.get() == item) return true;
      }
      return false;
   }

   public static void init(IEventBus bus) {
      TABS.register(bus);
   }
}

