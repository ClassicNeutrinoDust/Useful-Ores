package com.neutrinodust.useful_ores.attribution;

import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potions;

public enum AttributedEffects {

    SPEED("speed", potions(Potions.SWIFTNESS, Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS),
            true, false, true, eff(MobEffects.MOVEMENT_SPEED, 0)),
    SLOWNESS("slowness", potions(Potions.SLOWNESS, Potions.LONG_SLOWNESS, Potions.STRONG_SLOWNESS),
            false, false, true, eff(MobEffects.MOVEMENT_SLOWDOWN, 0)),
    STRENGTH("strength", potions(Potions.STRENGTH, Potions.LONG_STRENGTH, Potions.STRONG_STRENGTH),
            true, false, true, eff(MobEffects.DAMAGE_BOOST, 0)),
    WEAKNESS("weakness", potions(Potions.WEAKNESS, Potions.LONG_WEAKNESS),
            false, false, true, eff(MobEffects.WEAKNESS, 0)),
    POISON("poison", potions(Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON),
            false, false, true, eff(MobEffects.POISON, 0)),
    REGENERATION("regeneration", potions(Potions.REGENERATION, Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION),
            true, false, true, eff(MobEffects.REGENERATION, 0)),
    JUMP_BOOST("jump_boost", potions(Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING),
            true, false, true, eff(MobEffects.JUMP, 0)),
    WATER_BREATHING("water_breathing", potions(Potions.WATER_BREATHING, Potions.LONG_WATER_BREATHING),
            true, false, true, eff(MobEffects.WATER_BREATHING, 0)),
    FIRE_RESISTANCE("fire_resistance", potions(Potions.FIRE_RESISTANCE, Potions.LONG_FIRE_RESISTANCE),
            true, false, true, eff(MobEffects.FIRE_RESISTANCE, 0)),
    NIGHT_VISION("night_vision", potions(Potions.NIGHT_VISION, Potions.LONG_NIGHT_VISION),
            true, false, true, eff(MobEffects.NIGHT_VISION, 0)),
    INVISIBILITY("invisibility", potions(Potions.INVISIBILITY, Potions.LONG_INVISIBILITY),
            true, false, true, eff(MobEffects.INVISIBILITY, 0)),
    SLOW_FALLING("slow_falling", potions(Potions.SLOW_FALLING, Potions.LONG_SLOW_FALLING),
            true, false, true, eff(MobEffects.SLOW_FALLING, 0)),
    LUCK("luck", potions(Potions.LUCK),
            true, false, true, eff(MobEffects.LUCK, 0)),
    TURTLE_MASTER("turtle_master", potions(Potions.TURTLE_MASTER, Potions.LONG_TURTLE_MASTER, Potions.STRONG_TURTLE_MASTER),
            true, false, true, eff(MobEffects.DAMAGE_RESISTANCE, 2), eff(MobEffects.MOVEMENT_SLOWDOWN, 3)),
    WIND_CHARGED("wind_charged", potionSuppliers(ModLegacyPotions.WIND_CHARGED::get), false, false, true, eff(ModLegacyEffects.WIND_CHARGED::get, 0)),
    OOZING("oozing", potionSuppliers(ModLegacyPotions.OOZING::get), false, false, true, eff(ModLegacyEffects.OOZING::get, 0)),
    INFESTED("infestation", potionSuppliers(ModLegacyPotions.INFESTED::get), false, false, true, eff(ModLegacyEffects.INFESTED::get, 0)),
    WEAVING("weaving", potionSuppliers(ModLegacyPotions.WEAVING::get), false, false, true, eff(ModLegacyEffects.WEAVING::get, 0)),

    HEALING("healing", potions(Potions.HEALING, Potions.STRONG_HEALING), true, true, false),
    HARMING("harming", potions(Potions.HARMING, Potions.STRONG_HARMING), false, true, false);

    private final String id;
    private final Supplier<net.minecraft.world.item.alchemy.Potion>[] potions;
    private final boolean buffsAttacker;
    private final boolean instant;
    private final boolean armorEligible;
    private final EffectDef[] effects;

    @SafeVarargs
    AttributedEffects(String id, Supplier<net.minecraft.world.item.alchemy.Potion>[] potions,
                       boolean buffsAttacker, boolean instant, boolean armorEligible, EffectDef... effects) {
        this.id = id;
        this.potions = potions;
        this.buffsAttacker = buffsAttacker;
        this.instant = instant;
        this.armorEligible = armorEligible;
        this.effects = effects;
    }

    public String id() {
        return id;
    }

    public boolean buffsAttacker() {
        return buffsAttacker;
    }

    public boolean instant() {
        return instant;
    }

    public boolean armorEligible() {
        return armorEligible;
    }

    public MobEffectInstance[] freshInstances(int durationTicks) {
        MobEffectInstance[] out = new MobEffectInstance[effects.length];
        for (int i = 0; i < effects.length; i++) {
            EffectDef d = effects[i];
            out[i] = new MobEffectInstance(d.effect.get(), durationTicks, d.amplifier, true, false, true);
        }
        return out;
    }

    public MobEffectInstance[] freshHitInstances(int durationTicks) {
        MobEffectInstance[] out = new MobEffectInstance[effects.length];
        for (int i = 0; i < effects.length; i++) {
            EffectDef d = effects[i];
            out[i] = new MobEffectInstance(d.effect.get(), durationTicks, d.amplifier, false, true, true);
        }
        return out;
    }

    public static AttributedEffects byId(String id) {
        for (AttributedEffects e : values()) {
            if (e.id.equals(id)) return e;
        }
        return null;
    }

    public static AttributedEffects fromPotion(net.minecraft.world.item.alchemy.Potion potion) {
        for (AttributedEffects effect : values()) {
            for (Supplier<net.minecraft.world.item.alchemy.Potion> candidate : effect.potions) {
                if (candidate.get() == potion) return effect;
            }
        }
        return null;
    }

    @SafeVarargs
    private static Supplier<net.minecraft.world.item.alchemy.Potion>[] potions(net.minecraft.world.item.alchemy.Potion... values) {
        @SuppressWarnings("unchecked") Supplier<net.minecraft.world.item.alchemy.Potion>[] out = new Supplier[values.length];
        for (int i=0;i<values.length;i++) { final net.minecraft.world.item.alchemy.Potion value=values[i]; out[i]=()->value; }
        return out;
    }

    @SafeVarargs
    private static Supplier<net.minecraft.world.item.alchemy.Potion>[] potionSuppliers(Supplier<net.minecraft.world.item.alchemy.Potion>... values) {
        return values;
    }

    private record EffectDef(Supplier<MobEffect> effect, int amplifier) {}
    private static EffectDef eff(MobEffect effect, int amplifier) { return new EffectDef(() -> effect, amplifier); }
    private static EffectDef eff(Supplier<MobEffect> effect, int amplifier) { return new EffectDef(effect, amplifier); }

}

