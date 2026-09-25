package com.neutrinodust.useful_ores.mixin;

import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartRenderer.class)
public class MixinAbstractMinecartRendererTexture {

    @Shadow
    private static Identifier MINECART_LOCATION;

    private static final Identifier SOLARITE_MINECART_TEXTURE =
        Identifier.fromNamespaceAndPath("useful_ores", "textures/entity/solarite_battery_minecart.png");

    @Redirect(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/AbstractMinecartRenderer;MINECART_LOCATION:Lnet/minecraft/resources/Identifier;"
        )
    )
    private Identifier useful_ores$redirectMinecartTexture() {
        if ((Object) this instanceof com.neutrinodust.useful_ores.client.SolariteMinecartRenderer) {
            return SOLARITE_MINECART_TEXTURE;
        }
        return MINECART_LOCATION;
    }
}

