package com.neutrinodust.useful_ores.attribution;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potions;

public enum AttributedEffects {

    SPEED("speed", potions(Potions.SWIFTNESS, Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS),
            true, false, true, eff(MobEffects.SPEED, 0)),
    SLOWNESS("slowness", potions(Potions.SLOWNESS, Potions.LONG_SLOWNESS, Potions.STRONG_SLOWNESS),
            false, false, true, eff(MobEffects.SLOWNESS, 0)),
    STRENGTH("strength", potions(Potions.STRENGTH, Potions.LONG_STRENGTH, Potions.STRONG_STRENGTH),
            true, false, true, eff(MobEffects.STRENGTH, 0)),
    WEAKNESS("weakness", potions(Potions.WEAKNESS, Potions.LONG_WEAKNESS),
            false, false, true, eff(MobEffects.WEAKNESS, 0)),
    POISON("poison", potions(Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON),
            false, false, true, eff(MobEffects.POISON, 0)),
    REGENERATION("regeneration", potions(Potions.REGENERATION, Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION),
            true, false, true, eff(MobEffects.REGENERATION, 0)),
    JUMP_BOOST("jump_boost", potions(Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING),
            true, false, true, eff(MobEffects.JUMP_BOOST, 0)),
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
            true, false, true, eff(MobEffects.RESISTANCE, 2), eff(MobEffects.SLOWNESS, 3)),
    WIND_CHARGED("wind_charged", potions(Potions.WIND_CHARGED), false, false, true, eff(MobEffects.WIND_CHARGED, 0)),
    OOZING("oozing", potions(Potions.OOZING), false, false, true, eff(MobEffects.OOZING, 0)),
    INFESTED("infestation", potions(Potions.INFESTED), false, false, true, eff(MobEffects.INFESTED, 0)),
    WEAVING("weaving", potions(Potions.WEAVING), false, false, true, eff(MobEffects.WEAVING, 0)),

    HEALING("healing", potions(Potions.HEALING, Potions.STRONG_HEALING), true, true, false),
    HARMING("harming", potions(Potions.HARMING, Potions.STRONG_HARMING), false, true, false);

    private final String id;
    private final Holder<net.minecraft.world.item.alchemy.Potion>[] potions;
    private final boolean buffsAttacker;
    private final boolean instant;
    private final boolean armorEligible;
    private final EffectDef[] effects;

    @SafeVarargs
    AttributedEffects(String id, Holder<net.minecraft.world.item.alchemy.Potion>[] potions,
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
            out[i] = new MobEffectInstance(d.effect, durationTicks, d.amplifier, true, false, true);
        }
        return out;
    }

    public MobEffectInstance[] freshHitInstances(int durationTicks) {
        MobEffectInstance[] out = new MobEffectInstance[effects.length];
        for (int i = 0; i < effects.length; i++) {
            EffectDef d = effects[i];
            out[i] = new MobEffectInstance(d.effect, durationTicks, d.amplifier, false, true, true);
        }
        return out;
    }

    public static AttributedEffects byId(String id) {
        for (AttributedEffects e : values()) {
            if (e.id.equals(id)) return e;
        }
        return null;
    }

    public static AttributedEffects fromPotion(Holder<net.minecraft.world.item.alchemy.Potion> potionHolder) {
        for (AttributedEffects e : values()) {
            for (Holder<net.minecraft.world.item.alchemy.Potion> p : e.potions) {
                if (p.equals(potionHolder)) return e;
            }
        }
        return null;
    }

    @SafeVarargs
    private static Holder<net.minecraft.world.item.alchemy.Potion>[] potions(
            Holder<net.minecraft.world.item.alchemy.Potion>... potions) {
        return potions;
    }

    private record EffectDef(Holder<MobEffect> effect, int amplifier) {
    }

    private static EffectDef eff(Holder<MobEffect> effect, int amplifier) {
        return new EffectDef(effect, amplifier);
    }
}

