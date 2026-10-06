package com.neutrinodust.useful_ores.integration.jei;

import com.neutrinodust.useful_ores.attribution.ModAttributedItems;
import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.init.ModRegisters;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class UsefulOresJeiPlugin implements IModPlugin {
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(ModRegisters.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (var registered : ModAttributedItems.ALL) {
            registration.registerSubtypeInterpreter(
                    registered.get(),
                    (ItemStack stack, mezz.jei.api.ingredients.subtypes.UidContext context) ->
                            stack.get(ModDataComponents.ATTRIBUTED_EFFECT)
            );
        }
    }
}
