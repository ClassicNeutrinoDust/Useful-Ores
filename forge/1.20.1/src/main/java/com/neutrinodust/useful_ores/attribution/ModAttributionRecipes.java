package com.neutrinodust.useful_ores.attribution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ModAttributionRecipes {
 public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,"useful_ores");
 public static final RegistryObject<RecipeSerializer<AttributedIngotRecipe>> ATTRIBUTED_INGOT_SERIALIZER=SERIALIZERS.register("attributed_ingot",()->new RecipeSerializer<>(){
  @Override public AttributedIngotRecipe fromJson(ResourceLocation id,JsonObject json){return new AttributedIngotRecipe(id,category(json,CraftingBookCategory.MISC));}
  @Override public AttributedIngotRecipe fromNetwork(ResourceLocation id,FriendlyByteBuf buf){return new AttributedIngotRecipe(id,buf.readEnum(CraftingBookCategory.class));}
  @Override public void toNetwork(FriendlyByteBuf buf,AttributedIngotRecipe recipe){buf.writeEnum(recipe.category());}
 });
 public static final RegistryObject<RecipeSerializer<AttributedGearRecipe>> ATTRIBUTED_GEAR_SERIALIZER=SERIALIZERS.register("attributed_gear",()->new RecipeSerializer<>(){
  @Override public AttributedGearRecipe fromJson(ResourceLocation id,JsonObject json){
   int ingots=GsonHelper.getAsInt(json,"required_ingots"); int sticks=GsonHelper.getAsInt(json,"required_sticks",0); boolean weapon=GsonHelper.getAsBoolean(json,"is_weapon",false);
   Item result=BuiltInRegistries.ITEM.get(ResourceLocation.parse(GsonHelper.getAsString(json,"result"))); if(result==Items.AIR) throw new com.google.gson.JsonSyntaxException("Unknown result item in "+id);
   CraftingBookCategory category=category(json,CraftingBookCategory.EQUIPMENT); List<String> pattern=new ArrayList<>();
   if(json.has("pattern")){JsonArray a=GsonHelper.getAsJsonArray(json,"pattern");for(int i=0;i<a.size();i++)pattern.add(a.get(i).getAsString());}
   boolean mirrored=GsonHelper.getAsBoolean(json,"mirrored",false); return new AttributedGearRecipe(id,ingots,sticks,weapon,()->result,category,pattern,mirrored);
  }
  @Override public AttributedGearRecipe fromNetwork(ResourceLocation id,FriendlyByteBuf buf){
   int ingots=buf.readVarInt(),sticks=buf.readVarInt(); boolean weapon=buf.readBoolean(); Item result=BuiltInRegistries.ITEM.get(buf.readResourceLocation()); CraftingBookCategory category=buf.readEnum(CraftingBookCategory.class);
   int n=buf.readVarInt(); if(n<0||n>16)throw new IllegalArgumentException("Invalid attributed gear pattern length: "+n); List<String> pattern=new ArrayList<>(n);for(int i=0;i<n;i++)pattern.add(buf.readUtf(16)); boolean mirrored=buf.readBoolean();
   return new AttributedGearRecipe(id,ingots,sticks,weapon,()->result,category,pattern,mirrored);
  }
  @Override public void toNetwork(FriendlyByteBuf buf,AttributedGearRecipe r){buf.writeVarInt(r.getRequiredIngots());buf.writeVarInt(r.getRequiredSticks());buf.writeBoolean(r.isWeapon());buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(r.getOutput()));buf.writeEnum(r.category());buf.writeVarInt(r.getPattern().size());for(String row:r.getPattern())buf.writeUtf(row,16);buf.writeBoolean(r.isMirrored());}
 });
 private static CraftingBookCategory category(JsonObject json,CraftingBookCategory fallback){String value=GsonHelper.getAsString(json,"category",fallback.name().toLowerCase(Locale.ROOT));try{return CraftingBookCategory.valueOf(value.toUpperCase(Locale.ROOT));}catch(IllegalArgumentException e){return fallback;}}
 public static void init(IEventBus bus){SERIALIZERS.register(bus);}
 private ModAttributionRecipes(){}
}
