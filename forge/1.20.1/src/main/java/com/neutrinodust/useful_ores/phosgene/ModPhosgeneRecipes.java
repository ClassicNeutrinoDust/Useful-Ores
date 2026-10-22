package com.neutrinodust.useful_ores.phosgene;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import java.util.Locale;

public final class ModPhosgeneRecipes {
 public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,"useful_ores");
 public static final RegistryObject<RecipeSerializer<PhosgenePotionPowderRecipe>> POTION_POWDER_SERIALIZER=SERIALIZERS.register("phosgene_potion_powder",()->new RecipeSerializer<>(){
  @Override public PhosgenePotionPowderRecipe fromJson(ResourceLocation id,JsonObject json){String v=GsonHelper.getAsString(json,"category","misc");CraftingBookCategory c;try{c=CraftingBookCategory.valueOf(v.toUpperCase(Locale.ROOT));}catch(IllegalArgumentException e){c=CraftingBookCategory.MISC;}return new PhosgenePotionPowderRecipe(id,c);}
  @Override public PhosgenePotionPowderRecipe fromNetwork(ResourceLocation id,FriendlyByteBuf buf){return new PhosgenePotionPowderRecipe(id,buf.readEnum(CraftingBookCategory.class));}
  @Override public void toNetwork(FriendlyByteBuf buf,PhosgenePotionPowderRecipe recipe){buf.writeEnum(recipe.category());}
 });
 public static void init(IEventBus bus){SERIALIZERS.register(bus);}
 private ModPhosgeneRecipes(){}
}
