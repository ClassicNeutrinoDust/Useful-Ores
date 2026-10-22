package com.neutrinodust.useful_ores.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class ModPotionContents {
    public static final ModPotionContents EMPTY = new ModPotionContents(Optional.empty(), Optional.empty(), List.of());

    private final Optional<Potion> potion;
    private final Optional<Integer> customColor;
    private final List<MobEffectInstance> customEffects;

    public ModPotionContents(Potion potion) {
        this(Optional.of(potion), Optional.empty(), List.of());
    }

    public ModPotionContents(Optional<Potion> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects) {
        this.potion = potion;
        this.customColor = customColor;
        this.customEffects = customEffects;
    }

    private static boolean hasDefaultContents(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION
            || item == Items.TIPPED_ARROW;
    }

    private static boolean hasPotionTag(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && (tag.contains("Potion", Tag.TAG_STRING) || tag.contains("CustomPotionEffects", Tag.TAG_LIST)
            || tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC));
    }

    public static boolean has(ItemStack stack) {
        return hasPotionTag(stack) || hasDefaultContents(stack);
    }

    @Nullable
    public static ModPotionContents get(ItemStack stack) {
        if (!hasPotionTag(stack)) return hasDefaultContents(stack) ? EMPTY : null;
        CompoundTag tag = stack.getTag();
        Optional<Potion> potion = tag.contains("Potion", Tag.TAG_STRING)
            ? Optional.of(PotionUtils.getPotion(stack)) : Optional.empty();
        Optional<Integer> color = tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC)
            ? Optional.of(tag.getInt("CustomPotionColor")) : Optional.empty();
        return new ModPotionContents(potion, color, PotionUtils.getCustomEffects(stack));
    }

    public static ModPotionContents getOrEmpty(ItemStack stack) {
        ModPotionContents c = get(stack);
        return c == null ? EMPTY : c;
    }

    public static void set(ItemStack stack, ModPotionContents contents) {
        remove(stack);
        contents.potion.ifPresent(p -> PotionUtils.setPotion(stack, p));
        if (!contents.customEffects.isEmpty()) PotionUtils.setCustomEffects(stack, contents.customEffects);
        contents.customColor.ifPresent(c -> stack.getOrCreateTag().putInt("CustomPotionColor", c));
    }

    public static void remove(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        tag.remove("Potion");
        tag.remove("CustomPotionEffects");
        tag.remove("CustomPotionColor");
        if (tag.isEmpty()) stack.setTag(null);
    }

    public Optional<Potion> potion() { return potion; }

    public Optional<ResourceLocation> potionId() {
        return potion.map(BuiltInRegistries.POTION::getKey);
    }

    public boolean is(Potion other) { return potion.isPresent() && potion.get() == other; }

    public List<MobEffectInstance> customEffects() { return customEffects; }

    public Iterable<MobEffectInstance> getAllEffects() {
        List<MobEffectInstance> all = new ArrayList<>();
        potion.ifPresent(p -> all.addAll(p.getEffects()));
        all.addAll(customEffects);
        return all;
    }

    public boolean hasEffects() {
        return !customEffects.isEmpty() || potion.map(p -> !p.getEffects().isEmpty()).orElse(false);
    }

    public void forEachEffect(Consumer<MobEffectInstance> consumer) {
        for (MobEffectInstance e : getAllEffects()) consumer.accept(new MobEffectInstance(e));
    }

    public int getColor() {
        int rgb = customColor.orElseGet(() -> PotionUtils.getColor((java.util.Collection<MobEffectInstance>) toCollection()));
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    private List<MobEffectInstance> toCollection() {
        List<MobEffectInstance> list = new ArrayList<>();
        for (MobEffectInstance e : getAllEffects()) list.add(e);
        return list;
    }
}
