package com.neutrinodust.useful_ores.attribution;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModAttributionRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,
                    "useful_ores");

    private static final MapCodec<AttributedIngotRecipe> ATTRIBUTED_INGOT_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    CraftingBookCategory.CODEC.optionalFieldOf(
                            "category", CraftingBookCategory.MISC
                    ).forGetter(AttributedIngotRecipe::category)
            ).apply(instance, AttributedIngotRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AttributedIngotRecipe> ATTRIBUTED_INGOT_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(i -> CraftingBookCategory.values()[i], Enum::ordinal),
                    AttributedIngotRecipe::category,
                    AttributedIngotRecipe::new
            );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AttributedIngotRecipe>>
            ATTRIBUTED_INGOT_SERIALIZER = SERIALIZERS.register("attributed_ingot",
            () -> serializer(ATTRIBUTED_INGOT_CODEC, ATTRIBUTED_INGOT_STREAM_CODEC));

    private static final MapCodec<AttributedGearRecipe> ATTRIBUTED_GEAR_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    com.mojang.serialization.Codec.INT.fieldOf("required_ingots")
                            .forGetter(ModAttributionRecipes::requiredIngotsOf),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("required_sticks", 0)
                            .forGetter(ModAttributionRecipes::requiredSticksOf),
                    com.mojang.serialization.Codec.BOOL.optionalFieldOf("is_weapon", false)
                            .forGetter(ModAttributionRecipes::isWeaponOf),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("result")
                            .forGetter(ModAttributionRecipes::outputOf),
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.EQUIPMENT)
                            .forGetter(AttributedGearRecipe::category),
                    com.mojang.serialization.Codec.STRING.listOf().optionalFieldOf("pattern", List.of())
                            .forGetter(ModAttributionRecipes::patternOf),
                    com.mojang.serialization.Codec.BOOL.optionalFieldOf("mirrored", false)
                            .forGetter(AttributedGearRecipe::isMirrored)
            ).apply(instance, (ingots, sticks, weapon, item, cat, pattern, mirrored) ->
                    new AttributedGearRecipe(ingots, sticks, weapon, () -> item, cat, pattern, mirrored)));

    private static final StreamCodec<RegistryFriendlyByteBuf, AttributedGearRecipe> ATTRIBUTED_GEAR_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,  ModAttributionRecipes::requiredIngotsOf,
                    ByteBufCodecs.VAR_INT,  ModAttributionRecipes::requiredSticksOf,
                    ByteBufCodecs.BOOL,     ModAttributionRecipes::isWeaponOf,
                    ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM),
                                            ModAttributionRecipes::outputOf,
                    ByteBufCodecs.idMapper(i -> CraftingBookCategory.values()[i], Enum::ordinal),
                                            AttributedGearRecipe::category,
                    ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                                            ModAttributionRecipes::patternOf,
                    ByteBufCodecs.BOOL,     AttributedGearRecipe::isMirrored,
                    (ingots, sticks, weapon, item, cat, pattern, mirrored) ->
                            new AttributedGearRecipe(ingots, sticks, weapon, () -> item, cat, pattern, mirrored)
            );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AttributedGearRecipe>>
            ATTRIBUTED_GEAR_SERIALIZER = SERIALIZERS.register("attributed_gear",
            () -> serializer(ATTRIBUTED_GEAR_CODEC, ATTRIBUTED_GEAR_STREAM_CODEC));

    private static int     requiredIngotsOf(AttributedGearRecipe r) { return r.getRequiredIngots(); }
    private static int     requiredSticksOf(AttributedGearRecipe r) { return r.getRequiredSticks(); }
    private static boolean isWeaponOf(AttributedGearRecipe r)       { return r.isWeapon(); }
    private static Item    outputOf(AttributedGearRecipe r)          { return r.getOutput(); }
    private static List<String> patternOf(AttributedGearRecipe r)    { return r.getPattern(); }

    private static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeSerializer<T> serializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new RecipeSerializer<>() {
            @Override public MapCodec<T> codec() { return codec; }
            @Override public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }
        };
    }

    public static void init(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}

