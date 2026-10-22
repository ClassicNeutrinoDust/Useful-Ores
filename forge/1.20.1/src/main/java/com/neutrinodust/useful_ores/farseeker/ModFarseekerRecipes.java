package com.neutrinodust.useful_ores.farseeker;

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

public final class ModFarseekerRecipes {
 public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,"useful_ores");
 public static final RegistryObject<RecipeSerializer<FarseekerToolRecipe>> FARSEEKER_TOOL_SERIALIZER=SERIALIZERS.register("farseeker_tool",()->new RecipeSerializer<>(){
  @Override public FarseekerToolRecipe fromJson(ResourceLocation id,JsonObject json){String v=GsonHelper.getAsString(json,"category","equipment");CraftingBookCategory c;try{c=CraftingBookCategory.valueOf(v.toUpperCase(Locale.ROOT));}catch(IllegalArgumentException e){c=CraftingBookCategory.EQUIPMENT;}return new FarseekerToolRecipe(id,c);}
  @Override public FarseekerToolRecipe fromNetwork(ResourceLocation id,FriendlyByteBuf buf){return new FarseekerToolRecipe(id,buf.readEnum(CraftingBookCategory.class));}
  @Override public void toNetwork(FriendlyByteBuf buf,FarseekerToolRecipe recipe){buf.writeEnum(recipe.category());}
 });
 public static void init(IEventBus bus){SERIALIZERS.register(bus);}
 private ModFarseekerRecipes(){}
}
