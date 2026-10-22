package com.neutrinodust.useful_ores.mixin;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface MixinAbstractSoundInstanceAccessor {
    @Accessor("volume")
    void usefulOres$setVolume(float volume);

    @Accessor("pitch")
    void usefulOres$setPitch(float pitch);
}

