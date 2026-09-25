package com.neutrinodust.useful_ores.phosgene;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModPhosgeneRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "useful_ores");

    private static final MapCodec<PhosgenePotionPowderRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                            .forGetter(r -> CraftingBookCategory.MISC)
            ).apply(instance, PhosgenePotionPowderRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, PhosgenePotionPowderRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(i -> CraftingBookCategory.values()[i], Enum::ordinal),
                    r -> CraftingBookCategory.MISC,
                    (cat) -> new PhosgenePotionPowderRecipe(cat)
            );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PhosgenePotionPowderRecipe>>
            POTION_POWDER_SERIALIZER = SERIALIZERS.register("phosgene_potion_powder",
            () -> new RecipeSerializer<PhosgenePotionPowderRecipe>() {
                @Override
                public MapCodec<PhosgenePotionPowderRecipe> codec() {
                    return CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, PhosgenePotionPowderRecipe> streamCodec() {
                    return STREAM_CODEC;
                }
            });

    public static void init(IEventBus bus) {
        SERIALIZERS.register(bus);
    }

    private ModPhosgeneRecipes() {}
}

