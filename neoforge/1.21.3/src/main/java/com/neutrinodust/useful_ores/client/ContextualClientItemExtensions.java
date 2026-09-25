package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;


@EventBusSubscriber(modid = "useful_ores", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ContextualClientItemExtensions implements IClientItemExtensions {
    private BlockEntityWithoutLevelRenderer renderer;

    private ContextualClientItemExtensions() {}

    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new ContextualClientItemExtensions(),
                ModItems.METEOR_STAFF.get(), ModItems.NYXIUMNITE_STAFF.get());
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) renderer = new ContextualStaticItemRenderer();
        return renderer;
    }
}
