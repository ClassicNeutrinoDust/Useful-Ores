package com.neutrinodust.useful_ores.barrier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;














public final class ItemReversion {
    private ItemReversion() {}

    private static final Logger LOGGER = LoggerFactory.getLogger("useful_ores/ItemReversion");
    private static final int MAX_SUBDIVISION_DEPTH = 2;

    public record Fraction(long numerator, long denominator) {
        public Fraction {
            if (denominator <= 0) throw new IllegalArgumentException("Denominator must be positive");
            if (numerator < 0) throw new IllegalArgumentException("Numerator must not be negative");
            long g = gcd(numerator, denominator);
            numerator /= g;
            denominator /= g;
        }

        public static Fraction zero() {
            return new Fraction(0, 1);
        }

        public Fraction add(long addNumerator, long addDenominator) {
            if (addNumerator <= 0) return this;
            long n = numerator * addDenominator + addNumerator * denominator;
            long d = denominator * addDenominator;
            return new Fraction(n, d);
        }

        private static long gcd(long a, long b) {
            a = Math.abs(a);
            b = Math.abs(b);
            while (b != 0) {
                long t = a % b;
                a = b;
                b = t;
            }
            return a == 0 ? 1 : a;
        }
    }

    public record ReversionResult(List<ItemStack> outputs, Map<Item, Fraction> residue) {}

    private record ReversionEntry(int outputCount, Map<Item, Integer> ingredients, String recipeId) {}
    private record Subdivision(Item source, Item target, int targetCount, int sourceCount, String recipeId) {}

    private static volatile RecipeManager cachedRecipeManagerIdentity;
    private static volatile Map<Item, ReversionEntry> cachedReversionMap;
    private static volatile Map<Item, Subdivision> cachedSubdivisionMap;

    public static ReversionResult revert(ItemStack input, Level level, Map<Item, Fraction> existingResidue) {
        if (input.isEmpty()) return new ReversionResult(List.of(), existingResidue);

        Item item = input.getItem();
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) return new ReversionResult(List.of(), existingResidue);

        
        
        ItemStack vanillaStack = new ItemStack(item, input.getCount());
        if (!isSafeStateForReversion(input, vanillaStack)) {
            return new ReversionResult(List.of(), existingResidue);
        }

        
        if (id.getPath().startsWith("stripped_")) {
            Identifier unstripped = Identifier.fromNamespaceAndPath(
                id.getNamespace(), id.getPath().substring("stripped_".length()));
            Item direct = BuiltInRegistries.ITEM.getValue(unstripped);
            if (direct != null && direct != net.minecraft.world.item.Items.AIR) {
                return new ReversionResult(List.of(new ItemStack(direct, input.getCount())), existingResidue);
            }
            return new ReversionResult(List.of(), existingResidue);
        }

        Tables tables = getOrBuildTables(level);
        ReversionEntry entry = tables.reversionMap().get(item);
        if (entry == null) return new ReversionResult(List.of(), existingResidue);

        Map<Item, Fraction> ledger = new HashMap<>(existingResidue);
        for (Map.Entry<Item, Integer> ingredient : entry.ingredients().entrySet()) {
            long numerator = (long) ingredient.getValue() * input.getCount();
            ledger.merge(ingredient.getKey(), new Fraction(numerator, entry.outputCount()),
                ItemReversion::addFraction);
        }

        List<ItemStack> outputs = drainWholeQuantities(ledger);
        materializeSubdivisions(ledger, outputs, tables.subdivisionMap(), item);
        removeZeroEntries(ledger);

        return new ReversionResult(outputs, Map.copyOf(ledger));
    }

    





    private static boolean isSafeStateForReversion(ItemStack input, ItemStack vanillaStack) {
        if (ItemStack.isSameItemSameComponents(input, vanillaStack)) return true;

        Identifier id = BuiltInRegistries.ITEM.getKey(input.getItem());
        if (id == null) return false;

        try {
            String key = id.toString();
            if ("useful_ores:solar_battery".equals(key)) {
                
                
                
                ItemStack sanitized = input.copy();
                sanitized.remove(com.neutrinodust.useful_ores.solar.ModSolarComponents.BATTERY_ENERGY);
                return ItemStack.isSameItemSameComponents(sanitized, vanillaStack);
            }

            if ("useful_ores:arcanite_xp_jar".equals(key)) {
                Integer xp = input.get(com.neutrinodust.useful_ores.xpjar.ModXpJarComponents.JAR_XP);
                boolean empty = xp == null || xp == 0;
                if (!empty) return false;
                ItemStack sanitized = input.copy();
                sanitized.remove(com.neutrinodust.useful_ores.xpjar.ModXpJarComponents.JAR_XP);
                sanitized.remove(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA);
                return ItemStack.isSameItemSameComponents(sanitized, vanillaStack);
            }
        } catch (Throwable ignored) {
            return false;
        }
        return false;
    }

    private static List<ItemStack> drainWholeQuantities(Map<Item, Fraction> ledger) {
        List<ItemStack> outputs = new ArrayList<>();
        for (Item item : new ArrayList<>(ledger.keySet())) {
            Fraction f = ledger.get(item);
            if (f == null || f.numerator() < f.denominator()) continue;

            long whole = f.numerator() / f.denominator();
            long remainder = f.numerator() % f.denominator();
            appendSplitStacks(outputs, item, whole);
            ledger.put(item, new Fraction(remainder, f.denominator()));
        }
        return outputs;
    }

    private static void materializeSubdivisions(Map<Item, Fraction> ledger,
                                                List<ItemStack> outputs,
                                                Map<Item, Subdivision> subdivisions,
                                                Item originalInput) {
        for (int depth = 0; depth < MAX_SUBDIVISION_DEPTH; depth++) {
            boolean changed = false;
            for (Item source : new ArrayList<>(ledger.keySet())) {
                Fraction f = ledger.get(source);
                Subdivision subdivision = subdivisions.get(source);
                if (f == null || subdivision == null || f.numerator() <= 0) continue;

                
                
                
                
                if (subdivision.target() == originalInput) continue;

                
                long scaledNumerator = f.numerator() * subdivision.targetCount();
                long scaledDenominator = f.denominator() * subdivision.sourceCount();
                long targetUnits = scaledNumerator / scaledDenominator;
                if (targetUnits <= 0) continue;

                appendSplitStacks(outputs, subdivision.target(), targetUnits);
                long remainingNumerator = scaledNumerator % scaledDenominator;
                ledger.put(source, new Fraction(remainingNumerator, scaledDenominator));
                changed = true;
            }
            if (!changed) break;
        }
    }

    private static void appendSplitStacks(List<ItemStack> outputs, Item item, long count) {
        if (item == null || item == net.minecraft.world.item.Items.AIR || count <= 0) return;
        int max = Math.max(1, new ItemStack(item, 1).getMaxStackSize());
        long remaining = count;
        while (remaining > 0) {
            int amount = (int) Math.min(remaining, max);
            outputs.add(new ItemStack(item, amount));
            remaining -= amount;
        }
    }

    private static Fraction addFraction(Fraction left, Fraction right) {
        return left.add(right.numerator(), right.denominator());
    }

    private static void removeZeroEntries(Map<Item, Fraction> ledger) {
        ledger.entrySet().removeIf(e -> e.getValue() == null || e.getValue().numerator() <= 0);
    }

    private record Tables(Map<Item, ReversionEntry> reversionMap,
                          Map<Item, Subdivision> subdivisionMap) {}

    private static Tables getOrBuildTables(Level level) {
        RecipeAccess access = level.recipeAccess();
        if (!(access instanceof RecipeManager recipeManager)) {
            return new Tables(Collections.emptyMap(), Collections.emptyMap());
        }

        if (cachedReversionMap != null && cachedSubdivisionMap != null
            && recipeManager == cachedRecipeManagerIdentity) {
            return new Tables(cachedReversionMap, cachedSubdivisionMap);
        }

        synchronized (ItemReversion.class) {
            if (cachedReversionMap != null && cachedSubdivisionMap != null
                && recipeManager == cachedRecipeManagerIdentity) {
                return new Tables(cachedReversionMap, cachedSubdivisionMap);
            }

            long start = System.nanoTime();
            Tables built = buildTables(level, recipeManager);
            cachedReversionMap = built.reversionMap();
            cachedSubdivisionMap = built.subdivisionMap();
            cachedRecipeManagerIdentity = recipeManager;

            LOGGER.info("[useful_ores] Nyxium Dark Barrier tables rebuilt: {} reverse entries, {} subdivision entries in {}ms",
                built.reversionMap().size(), built.subdivisionMap().size(),
                (System.nanoTime() - start) / 1_000_000.0);
            return built;
        }
    }

    private static Tables buildTables(Level level, RecipeManager recipeManager) {
        List<RecipeHolder<?>> recipes = new ArrayList<>(recipeManager.getRecipes());
        recipes.sort(Comparator.comparing(holder -> holder.id().toString()));

        Map<Item, ReversionEntry> reverse = new LinkedHashMap<>();
        Map<Item, Subdivision> subdivisions = new LinkedHashMap<>();

        
        
        collectRecipes(recipes, reverse, subdivisions, true, level.registryAccess());
        collectRecipes(recipes, reverse, subdivisions, false, level.registryAccess());

        return new Tables(Map.copyOf(reverse), Map.copyOf(subdivisions));
    }

    private static void collectRecipes(List<RecipeHolder<?>> recipes,
                                       Map<Item, ReversionEntry> reverse,
                                       Map<Item, Subdivision> subdivisions,
                                       boolean wantSingleOutput, net.minecraft.core.HolderLookup.Provider registries) {
        for (RecipeHolder<?> holder : recipes) {
            Recipe<?> recipe = holder.value();
            if (!(recipe instanceof CraftingRecipe craftingRecipe)) continue;

            try {
                List<Ingredient> ingredients = craftingRecipe.placementInfo().ingredients();
                if (ingredients.isEmpty()) continue;

                int width = ingredients.size();
                int height = 1;
                if (craftingRecipe instanceof ShapedRecipe shaped) {
                    width = shaped.getWidth();
                    height = shaped.getHeight();
                    if (width * height < ingredients.size()) {
                        width = ingredients.size();
                        height = 1;
                    }
                }
                if (width <= 0 || height <= 0 || width * height > 256) continue;

                List<ItemStack> gridItems = new ArrayList<>(width * height);
                for (int i = 0; i < width * height; i++) {
                    if (i < ingredients.size() && !ingredients.get(i).isEmpty()) {
                        gridItems.add(firstItemStack(ingredients.get(i)));
                    } else {
                        gridItems.add(ItemStack.EMPTY);
                    }
                }

                ItemStack result = craftingRecipe.assemble(CraftingInput.of(width, height, gridItems), registries);
                if (result == null || result.isEmpty()) continue;

                boolean singleOutput = result.getCount() == 1;
                if (singleOutput != wantSingleOutput) continue;

                Map<Item, Integer> tally = new LinkedHashMap<>();
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.isEmpty()) continue;
                    ItemStack resolved = resolveIngredient(ingredient, result);
                    if (resolved.isEmpty()) {
                        
                        resolved = firstItemStack(ingredient);
                    }
                    if (resolved.isEmpty()) continue;
                    tally.merge(resolved.getItem(), 1, Integer::sum);
                }

                if (tally.isEmpty() || tally.containsKey(result.getItem())) continue;

                
                
                
                
                
                
                boolean basicMaterialConversion = isBasicMaterialConversion(result.getItem(), tally);

                
                
                
                
                
                
                
                if (!basicMaterialConversion && !reverse.containsKey(result.getItem())) {
                    reverse.put(result.getItem(), new ReversionEntry(
                        result.getCount(), Map.copyOf(tally), holder.id().toString()));
                }

                
                
                
                if (result.getCount() > 1 && tally.size() == 1) {
                    Map.Entry<Item, Integer> only = tally.entrySet().iterator().next();
                    if (isLikelyUnitSubdivision(only.getKey(), result.getItem(), only.getValue(), result.getCount())) {
                        subdivisions.putIfAbsent(only.getKey(), new Subdivision(
                            only.getKey(), result.getItem(), result.getCount(), only.getValue(), holder.id().toString()));
                    }
                }
            } catch (Exception ex) {
                LOGGER.debug("[useful_ores] Skipping recipe {} while building Nyxium Dark Barrier tables: {}",
                    holder.id(), ex.toString());
            }
        }
    }

    private static ItemStack firstItemStack(Ingredient ingredient) {
        return ingredient.items().findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    private static ItemStack resolveIngredient(Ingredient ingredient, ItemStack result) {
        if (result == null || result.isEmpty()) return firstItemStack(ingredient);

        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
        if (resultId == null) return firstItemStack(ingredient);

        String[] outputTokens = tokens(resultId.getPath());
        ItemStack best = ItemStack.EMPTY;
        int bestScore = Integer.MIN_VALUE;

        for (ItemStack candidate : ingredient.items().map(ItemStack::new).toList()) {
            Identifier candidateId = BuiltInRegistries.ITEM.getKey(candidate.getItem());
            if (candidateId == null) continue;
            int score = familyScore(resultId, outputTokens, candidateId);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return bestScore > 0 ? best : firstItemStack(ingredient);
    }

    private static int familyScore(Identifier resultId, String[] resultTokens, Identifier candidateId) {
        int score = resultId.getNamespace().equals(candidateId.getNamespace()) ? 10 : 0;
        for (String outputToken : resultTokens) {
            for (String candidateToken : tokens(candidateId.getPath())) {
                if (outputToken.equals(candidateToken)) score += 50;
                else if (outputToken.length() >= 3 && candidateToken.length() >= 3
                    && (outputToken.startsWith(candidateToken) || candidateToken.startsWith(outputToken))) {
                    score += 12;
                }
            }
        }
        return score;
    }

    private static boolean isLikelyUnitSubdivision(Item source, Item target, int sourceCount, int targetCount) {
        if (sourceCount != 1 || targetCount <= 1) return false;

        Identifier sourceId = BuiltInRegistries.ITEM.getKey(source);
        Identifier targetId = BuiltInRegistries.ITEM.getKey(target);
        if (sourceId == null || targetId == null) return false;

        String s = sourceId.getPath();
        String t = targetId.getPath();

        
        if (s.endsWith("_ingot") && t.endsWith("_nugget")) return targetCount == 9;
        if ((s.endsWith("_block") || s.endsWith("_ore")) && t.endsWith("_ingot")) return true;
        if ((s.endsWith("_log") || s.endsWith("_wood") || s.endsWith("_stem") || s.endsWith("_hyphae"))
            && t.endsWith("_planks")) return true;
        if (s.endsWith("_block") && (t.endsWith("_gem") || t.endsWith("_shard") || t.endsWith("_fragment"))) return true;
        return false;
    }


    






    private static boolean isBasicMaterialConversion(Item result, Map<Item, Integer> tally) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result);
        if (resultId == null || tally.isEmpty()) return false;

        String resultPath = resultId.getPath();
        String resultFamily = materialFamily(resultPath);

        
        
        boolean bareResource = !resultPath.contains("_");
        if (bareResource) {
            for (Item ingredient : tally.keySet()) {
                Identifier ingredientId = BuiltInRegistries.ITEM.getKey(ingredient);
                if (ingredientId == null) continue;
                String ingredientPath = ingredientId.getPath();
                if (!resultFamily.isEmpty() && resultFamily.equals(materialFamily(ingredientPath))
                    && isMaterialForm(ingredientPath)) {
                    return true;
                }
            }
        }

        
        
        
        
        if (isTerminalMaterialForm(resultPath)) {
            boolean sameFamilyIngredient = false;
            for (Item ingredient : tally.keySet()) {
                Identifier ingredientId = BuiltInRegistries.ITEM.getKey(ingredient);
                if (ingredientId == null) continue;
                if (!resultFamily.isEmpty()
                    && resultFamily.equals(materialFamily(ingredientId.getPath()))) {
                    sameFamilyIngredient = true;
                    break;
                }
            }
            if (sameFamilyIngredient) return true;
        }

        return false;
    }

    private static boolean isTerminalMaterialForm(String path) {
        return path.endsWith("_nugget")
            || path.endsWith("_ingot")
            || path.endsWith("_gem")
            || path.endsWith("_dust")
            || path.endsWith("_raw")
            || path.endsWith("_fragment")
            || path.endsWith("_shard")
            || path.endsWith("_crystal")
            || path.endsWith("_ore")
            || path.endsWith("_block");
    }

    private static boolean isMaterialForm(String path) {
        return isTerminalMaterialForm(path)
            || path.startsWith("raw_");
    }

    private static String materialFamily(String path) {
        String family = path;
        if (family.startsWith("raw_")) family = family.substring(4);

        String[] suffixes = {
            "_nugget", "_ingot", "_block", "_ore", "_gem", "_dust",
            "_fragment", "_shard", "_crystal"
        };
        for (String suffix : suffixes) {
            if (family.endsWith(suffix)) {
                return family.substring(0, family.length() - suffix.length());
            }
        }
        return family;
    }

    private static String[] tokens(String path) {
        return path.split("_");
    }
}
