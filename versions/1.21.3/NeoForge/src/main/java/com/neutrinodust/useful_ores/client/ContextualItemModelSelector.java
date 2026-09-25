package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.attribution.ModDataComponents;
import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarBlockEntity;
import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Emulates the 1.21.4 item-model definition graph at the 1.21.3 render boundary.
 * It returns null when vanilla's canonical baked model should be used unchanged.
 *
 * Important 1.21.3 rule: contextual models are kept on the side-loaded/standalone
 * registration path.  We do NOT switch ModelResourceLocation variants, because that
 * destabilized NeoForge's model/resource-reload lifecycle in the V82 build.
 */
public final class ContextualItemModelSelector {
    private static final String NS = "useful_ores";

    private ContextualItemModelSelector() {}

    public static BakedModel select(BakedModel original, ItemStack stack, ItemDisplayContext context) {
        BakedModel resolved = resolve(stack, context);
        return isUsable(resolved) ? resolved : original;
    }

    /** Resolve only the contextual model, without requiring a vanilla/original model. */
    public static BakedModel resolve(ItemStack stack, ItemDisplayContext context) {
        String path = alternatePath(stack, context);
        if (path == null) return null;

        BakedModel selected = getModel(path);
        if (!isUsable(selected)) return null;

        if (isFlatModel(path)) {
            boolean guiNoShade = context == ItemDisplayContext.GUI && isAffectedGuiIcon(path);
            return FlatLightingBakedModel.wrap(selected, guiNoShade);
        }
        return selected;
    }

    public static String alternatePath(ItemStack stack, ItemDisplayContext context) {
        if (stack.isEmpty()) return null;
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null || !NS.equals(id.getNamespace())) return null;
        String item = id.getPath();

        if (item.endsWith("_spear")) {
            if ("painite_spear".equals(item)) {
                boolean fury = stack.get(ModDataComponents.PAINITE_FURY_TOOL) != null;
                if (context == ItemDisplayContext.GUI) {
                    return fury ? "painite_spear_fury" : null;
                }
                if (isHandContext(context)) {
                    return fury ? "painite_spear_in_hand_fury" : "painite_spear_in_hand";
                }
                return null;
            }
            return isHandContext(context) ? item + "_in_hand" : null;
        }

        if ("scheelite_chisel".equals(item)) {
            if (context == ItemDisplayContext.GUI) return "scheelite_chisel_icon";
            if (isHandContext(context)) return "scheelite_chisel_hand";
            return "scheelite_chisel";
        }

        if ("meteor_staff".equals(item) || "nyxiumnite_staff".equals(item)) {
            return isFlatContext(context) ? item + "_flat" : null;
        }

        if ("arcanite_xp_jar".equals(item)) {
            if (context == ItemDisplayContext.GROUND) return "arcanite_xp_jar_ground";
            int xp = ArcaniteXpJarItem.getStoredXp(stack);
            int level = Math.clamp(
                    Math.round(7.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP), 0, 7);
            return "arcanite_xp_jar_icon_" + level;
        }

        return null;
    }

    private static boolean isHandContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    private static boolean isFlatContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.GUI
                || context == ItemDisplayContext.GROUND
                || context == ItemDisplayContext.FIXED;
    }

    private static boolean isAffectedGuiIcon(String path) {
        return "meteor_staff_flat".equals(path)
                || "nyxiumnite_staff_flat".equals(path)
                || path.startsWith("arcanite_xp_jar_icon_");
    }

    private static boolean isFlatModel(String path) {
        return "meteor_staff_flat".equals(path)
                || "nyxiumnite_staff_flat".equals(path)
                || "scheelite_chisel_icon".equals(path)
                || path.startsWith("arcanite_xp_jar_icon_");
    }

    private static BakedModel getModel(String path) {
        try {
            return Minecraft.getInstance().getModelManager().getModel(
                    ModelResourceLocation.standalone(
                            ResourceLocation.fromNamespaceAndPath(NS, "item/" + path)));
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isUsable(BakedModel model) {
        return model != null && model != Minecraft.getInstance().getModelManager().getMissingModel();
    }
}
