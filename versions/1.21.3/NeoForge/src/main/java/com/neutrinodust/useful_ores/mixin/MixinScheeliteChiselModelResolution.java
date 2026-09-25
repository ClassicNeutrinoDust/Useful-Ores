package com.neutrinodust.useful_ores.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Resolves the Scheelite Chisel's authored 3D model at the legacy ItemRenderer model-resolution
 * boundary. 1.21.4's item definition performs this selection before the actual render pipeline;
 * doing it here avoids relying solely on a late render-argument replacement.
 */
@Mixin(ItemRenderer.class)
public abstract class MixinScheeliteChiselModelResolution {
    private static final ModelResourceLocation HAND_MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath("useful_ores", "item/scheelite_chisel_hand"));

    @Inject(
            method = "getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("RETURN"),
            cancellable = true,
            require = 1,
            remap = true)
    private void usefulOres$resolveChisel(ItemStack stack, Level level, LivingEntity entity, int seed,
                                          CallbackInfoReturnable<BakedModel> cir) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null || !"useful_ores".equals(id.getNamespace()) || !"scheelite_chisel".equals(id.getPath())) {
            return;
        }

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(HAND_MODEL);
        if (model != null && model != Minecraft.getInstance().getModelManager().getMissingModel()) {
            cir.setReturnValue(model);
        }
    }
}
