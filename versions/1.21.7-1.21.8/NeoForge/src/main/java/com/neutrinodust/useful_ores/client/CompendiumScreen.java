package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.compendium.CompendiumData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompendiumScreen extends Screen {

   private static final Pattern LINK_PATTERN = Pattern.compile("\\[([^\\]]+)]\\(([^/]+)/([^)]+)\\)");

   private static final int PANEL_BG = 0xFFC6C6C6;
   private static final int PANEL_BEVEL_LIGHT = 0xFFFFFFFF;
   private static final int PANEL_BEVEL_DARK = 0xFF555555;
   private static final int PANEL_OUTER_BORDER = 0xFF000000;
   private static final int SLOT_BG = 0xFF8B8B8B;
   private static final int SLOT_HOVER_BG = 0xFFA6D0FF;
   private static final int SLOT_BEVEL_DARK = 0xFF373737;
   private static final int SLOT_BEVEL_LIGHT = 0xFFFFFFFF;
   private static final int TITLE_COLOR = 0xFF404040;
   private static final int BODY_COLOR = 0xFF404040;
   private static final int LINK_COLOR = 0xFF1E4FCC;
   private static final int LINK_HOVER_COLOR = 0xFF3D6EEF;
   private static final int TAB_TEXT_COLOR = 0xFF404040;
   private static final int LABEL_COLOR = 0xFF262626;
   private static final int LABEL_HOVER_COLOR = 0xFF1E4FCC;
   private static final int COUNTER_COLOR = 0xFF6B6B6B;

   private int chapterIndex = 0;
   private int pageIndex = 0;
   private int subPageIndex = 0;
   private int gridScroll = 0;

   private int panelX, panelY, panelW, panelH;

   private final List<LinkHitbox> linkHitboxes = new ArrayList<>();

   private final List<GridHitbox> gridHitboxes = new ArrayList<>();

   private List<SubPage> subPages = List.of();

   private ItemStack hoveredRecipeStack = ItemStack.EMPTY;
   private int currentMouseX;
   private int currentMouseY;

   private Button backButton;
   private Button prevButton;
   private Button nextButton;

   private static final int GRID_CELL_W = 150;
   private static final int GRID_CELL_H = 112;
   private static final int GRID_ICON_SIZE = 16;
   private static final int GRID_SLOT_SIZE = 32;

   private record LinkHitbox(int x, int y, int width, int height, int targetChapter, int targetPage) {
      boolean contains(double mx, double my) {
         return mx >= x && mx < x + width && my >= y && my < y + height;
      }
   }

   private record GridHitbox(int x, int y, int width, int height, int targetChapter, int targetPage) {
      boolean contains(double mx, double my) {
         return mx >= x && mx < x + width && my >= y && my < y + height;
      }
   }

   private record Word(String text, int linkChapter, int linkPage) {
      boolean isLink() {
         return linkChapter >= 0;
      }
   }

   private record Line(List<Word> words, boolean blank) {
      static Line of(List<Word> words) {
         return new Line(words, false);
      }
      static Line blankLine() {
         return new Line(List.of(), true);
      }
   }

   private record SubPage(List<Line> lines, boolean showImage, int recipeStart, int recipeEnd) {
      boolean showRecipeAndItems() { return recipeStart >= 0 && recipeEnd >= recipeStart; }
   }

   public CompendiumScreen() {
      super(Component.translatable("item.useful_ores.useful_ores_compendium"));
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   protected void init() {
      panelW = Math.min(340, this.width - 40);
      panelH = Math.min(240, this.height - 70);
      panelX = (this.width - panelW) / 2;
      panelY = (this.height - panelH) / 2;

      chapterIndex = Math.min(chapterIndex, CompendiumData.CHAPTERS.size() - 1);
      pageIndex = 0;
      subPageIndex = 0;
      subPages = List.of();

      rebuildButtons();
   }

   private void rebuildButtons() {
      this.clearWidgets();

      List<CompendiumData.Chapter> chapters = CompendiumData.CHAPTERS;
      int tabW = 100;
      int tabH = 20;
      int tabX = panelX - tabW - 6;
      for (int i = 0; i < chapters.size(); i++) {
         int idx = i;
         Component label = Component.literal(chapters.get(i).name);
         this.addRenderableWidget(Button.builder(label, btn -> navigateTo(idx, 0))
            .bounds(tabX, panelY + i * (tabH + 4), tabW, tabH).build());
      }

      int navY = panelY + panelH + 8;

      backButton = this.addRenderableWidget(Button.builder(Component.literal("< Back"), btn -> navigateTo(chapterIndex, 0))
         .bounds(panelX, navY, 70, 20).build());

      prevButton = this.addRenderableWidget(Button.builder(Component.literal("<< Prev"), btn -> {
         if (subPageIndex > 0) subPageIndex--;
      }).bounds(panelX + 74, navY, 70, 20).build());

      nextButton = this.addRenderableWidget(Button.builder(Component.literal("Next >>"), btn -> {
         if (subPageIndex < subPages.size() - 1) subPageIndex++;
      }).bounds(panelX + panelW - 144, navY, 70, 20).build());

      this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> this.onClose())
         .bounds(panelX + panelW - 70, navY, 70, 20).build());

      updateNavButtonStates();
   }

   private void updateNavButtonStates() {
      boolean onDetailPage = pageIndex > 0;
      if (backButton != null) backButton.visible = onDetailPage;
      if (prevButton != null) {
         prevButton.visible = onDetailPage;
         prevButton.active = onDetailPage && subPageIndex > 0;
      }
      if (nextButton != null) {
         nextButton.visible = onDetailPage;
         nextButton.active = onDetailPage && subPageIndex < subPages.size() - 1;
      }
   }

   private void navigateTo(int chapter, int page) {
      this.chapterIndex = chapter;
      this.pageIndex = page;
      this.gridScroll = 0;
      this.subPageIndex = 0;
      this.subPages = page > 0 ? buildSubPages(currentChapterAt(chapter).pages.get(page), panelW - 24) : List.of();
      updateNavButtonStates();
   }

   private CompendiumData.Chapter currentChapterAt(int chapter) {
      return CompendiumData.CHAPTERS.get(chapter);
   }

   private static int[] findPage(String chapterName, String pageTitle) {
      List<CompendiumData.Chapter> chapters = CompendiumData.CHAPTERS;
      for (int c = 0; c < chapters.size(); c++) {
         CompendiumData.Chapter chapter = chapters.get(c);
         if (!chapter.name.equalsIgnoreCase(chapterName.trim())) continue;
         for (int p = 0; p < chapter.pages.size(); p++) {
            if (chapter.pages.get(p).title.equalsIgnoreCase(pageTitle.trim())) {
               return new int[]{c, p};
            }
         }
      }
      return null;
   }

   private CompendiumData.Chapter currentChapter() {
      return CompendiumData.CHAPTERS.get(chapterIndex);
   }

   private CompendiumData.Page currentPage() {
      List<CompendiumData.Page> pages = currentChapter().pages;
      int idx = Math.min(pageIndex, pages.size() - 1);
      return pages.get(idx);
   }

   private static List<Word> tokenize(String paragraph) {
      List<Word> words = new ArrayList<>();
      Matcher m = LINK_PATTERN.matcher(paragraph);
      int last = 0;
      while (m.find()) {
         addPlainWords(words, paragraph.substring(last, m.start()));
         String label = m.group(1);
         int[] target = findPage(m.group(2), m.group(3));
         int targetChapter = target != null ? target[0] : -1;
         int targetPage = target != null ? target[1] : -1;
         for (String w : label.split(" ")) {
            if (!w.isEmpty()) words.add(new Word(w, targetChapter, targetPage));
         }
         last = m.end();
      }
      addPlainWords(words, paragraph.substring(last));
      return words;
   }

   private static void addPlainWords(List<Word> out, String text) {
      for (String w : text.split(" ")) {
         if (!w.isEmpty()) out.add(new Word(w, -1, -1));
      }
   }

   private List<Line> wrapParagraphs(List<String> paragraphs, int textWidth) {
      List<Line> lines = new ArrayList<>();
      int spaceWidth = this.font.width(" ");
      for (int p = 0; p < paragraphs.size(); p++) {
         List<Word> words = tokenize(paragraphs.get(p));
         List<Word> currentLine = new ArrayList<>();
         int lineWidth = 0;
         for (Word word : words) {
            int wordWidth = this.font.width(word.text());
            if (!currentLine.isEmpty() && lineWidth + spaceWidth + wordWidth > textWidth) {
               lines.add(Line.of(currentLine));
               currentLine = new ArrayList<>();
               lineWidth = 0;
            }
            if (!currentLine.isEmpty()) lineWidth += spaceWidth;
            currentLine.add(word);
            lineWidth += wordWidth;
         }
         if (!currentLine.isEmpty()) lines.add(Line.of(currentLine));
         if (p < paragraphs.size() - 1) lines.add(Line.blankLine());
      }
      return lines;
   }

   private List<String> wrapPlainText(String text, int maxWidth) {
      List<String> lines = new ArrayList<>();
      StringBuilder current = new StringBuilder();
      for (String word : text.split(" ")) {
         if (word.isEmpty()) continue;
         String candidate = current.isEmpty() ? word : current + " " + word;
         if (this.font.width(candidate) <= maxWidth) {
            current = new StringBuilder(candidate);
         } else {
            if (!current.isEmpty()) {
               lines.add(current.toString());
               current = new StringBuilder();
            }
            if (this.font.width(word) > maxWidth) {
               String remaining = word;
               while (this.font.width(remaining) > maxWidth) {
                  String chunk = this.font.plainSubstrByWidth(remaining, maxWidth);
                  if (chunk.isEmpty()) break;
                  lines.add(chunk);
                  remaining = remaining.substring(chunk.length());
               }
               current = new StringBuilder(remaining);
            } else {
               current = new StringBuilder(word);
            }
         }
      }
      if (!current.isEmpty()) lines.add(current.toString());
      return lines;
   }

   private String[] wrapLabelTwoLines(String label, int maxWidth) {
      List<String> wrapped = wrapPlainText(label, maxWidth);
      if (wrapped.isEmpty()) return new String[]{""};
      if (wrapped.size() == 1) return new String[]{wrapped.get(0)};
      String first = wrapped.get(0);
      String second = String.join(" ", wrapped.subList(1, wrapped.size()));
      if (this.font.width(second) > maxWidth) {
         second = this.font.plainSubstrByWidth(second, maxWidth - this.font.width("..")) + "..";
      }
      return new String[]{first, second};
   }

   private static final ResourceLocation BANNER_TEXTURE =
      ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/gui/compendium/mod_banner.png");

   private static final ResourceLocation BREWING_STAND_TEXTURE =
      ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/brewing_stand.png");

   private static final int BREW_PANEL_W = 176;
   private static final int BREW_PANEL_H = 82;
   private static final int BREW_FUEL_X = 17, BREW_FUEL_Y = 17;
   private static final int BREW_INGREDIENT_X = 79, BREW_INGREDIENT_Y = 17;
   private static final int[] BREW_BOTTLE_X = {56, 79, 102};
   private static final int[] BREW_BOTTLE_Y = {51, 57, 51};
   private static final String BLAZE_POWDER = "minecraft:blaze_powder";

   private static final int BREW_CYCLE_MS = 1500;

   private record PotionPair(Holder<Potion> a, Holder<Potion> b) {}

   private static final List<PotionPair> VIAL_EXAMPLE_PAIRS = List.of(
      new PotionPair(Potions.FIRE_RESISTANCE, Potions.WATER_BREATHING),
      new PotionPair(Potions.STRENGTH, Potions.SWIFTNESS),
      new PotionPair(Potions.REGENERATION, Potions.HEALING)
   );

   private record VialCycleFrame(List<ItemStack> bottleStacks, ItemStack outputStack) {}

   private static int nonNullBottleCount(String[] bottles) {
      int count = 0;
      for (String b : bottles) {
         if (b != null) count++;
      }
      return count;
   }

   private static Item resolveItem(String itemId) {
      ResourceLocation id = ResourceLocation.tryParse(itemId);
      return id != null ? BuiltInRegistries.ITEM.getValue(id) : null;
   }

   private static ItemStack examplePotionStack(Holder<Potion> potion) {
      ItemStack stack = new ItemStack(Items.POTION);
      stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
      return stack;
   }

   private static ItemStack exampleMergedVialStack(Item vialItem, PotionPair pair) {
      List<MobEffectInstance> merged = new ArrayList<>();
      merged.addAll(pair.a().value().getEffects());
      merged.addAll(pair.b().value().getEffects());
      PotionContents contents = new PotionContents(Optional.empty(), Optional.empty(), merged, Optional.empty());
      ItemStack stack = new ItemStack(vialItem);
      stack.set(DataComponents.POTION_CONTENTS, contents);
      return stack;
   }

   private VialCycleFrame vialCycleFor(CompendiumData.Recipe recipe) {
      if (recipe.type != CompendiumData.Recipe.Type.BREWING || recipe.outputItemId == null) return null;

      boolean isFuse = recipe.outputItemId.equals("useful_ores:sperrylite_catalytic_vial")
         && nonNullBottleCount(recipe.brewingBottles) == 3;
      boolean isSplash = recipe.outputItemId.equals("useful_ores:sperrylite_catalytic_vial_splash");
      boolean isLingering = recipe.outputItemId.equals("useful_ores:sperrylite_catalytic_vial_lingering");
      if (!isFuse && !isSplash && !isLingering) return null;

      int idx = (int) ((System.currentTimeMillis() / BREW_CYCLE_MS) % VIAL_EXAMPLE_PAIRS.size());
      PotionPair pair = VIAL_EXAMPLE_PAIRS.get(idx);

      if (isFuse) {
         Item vial = resolveItem("useful_ores:sperrylite_catalytic_vial");
         if (vial == null) return null;
         List<ItemStack> bottles = List.of(
            examplePotionStack(pair.a()),
            new ItemStack(vial),
            examplePotionStack(pair.b())
         );
         return new VialCycleFrame(bottles, exampleMergedVialStack(vial, pair));
      }
      if (isSplash) {
         Item vial = resolveItem("useful_ores:sperrylite_catalytic_vial");
         Item splash = resolveItem("useful_ores:sperrylite_catalytic_vial_splash");
         if (vial == null || splash == null) return null;
         return new VialCycleFrame(List.of(exampleMergedVialStack(vial, pair)), exampleMergedVialStack(splash, pair));
      }
      Item splash = resolveItem("useful_ores:sperrylite_catalytic_vial_splash");
      Item lingering = resolveItem("useful_ores:sperrylite_catalytic_vial_lingering");
      if (splash == null || lingering == null) return null;
      return new VialCycleFrame(List.of(exampleMergedVialStack(splash, pair)), exampleMergedVialStack(lingering, pair));
   }

   private static final int BANNER_NATIVE_W = 897;
   private static final int BANNER_NATIVE_H = 285;

   private static int bannerHeight(int textWidth) {
      return Math.max(1, textWidth * BANNER_NATIVE_H / BANNER_NATIVE_W);
   }

   private List<SubPage> buildSubPages(CompendiumData.Page page, int textWidth) {
      List<String> paragraphs = page.textFile != null
         ? CompendiumTextLoader.getParagraphs(page.textFile)
         : page.paragraphs;
      List<Line> allLines = wrapParagraphs(paragraphs, textWidth);

      int lineUnit = this.font.lineHeight + 2;
      int topInset = 12;
      int titleHeight = this.font.lineHeight + 8;
      int bottomMargin = 22;
      int availableHeight = panelH - topInset - titleHeight - bottomMargin;
      int firstImageHeight = page.imageId != null
         ? Math.max(1, page.imageHeight * Math.min(page.imageWidth, textWidth) / page.imageWidth)
         : bannerHeight(textWidth);
      int firstImageGap = 8;

      List<SubPage> result = new ArrayList<>();
      int i = 0;
      boolean first = true;
      while (i < allLines.size()) {
         int imageReserve = first ? firstImageHeight + firstImageGap : 0;
         int capacityHeight = availableHeight - imageReserve;
         int linesPerPage = Math.max(1, capacityHeight / lineUnit);
         int end = Math.min(allLines.size(), i + linesPerPage);
         result.add(new SubPage(new ArrayList<>(allLines.subList(i, end)), first, -1, -1));
         i = end;
         first = false;
      }
      if (result.isEmpty()) result.add(new SubPage(List.of(), true, -1, -1));

      List<CompendiumData.Recipe> recipes = page.recipes != null && !page.recipes.isEmpty()
         ? page.recipes
         : (page.recipe != null ? List.of(page.recipe) : List.of());
      if (!recipes.isEmpty() || (page.showItemNames && !page.itemIds.isEmpty())) {

         SubPage last = result.get(result.size() - 1);
         boolean lastIsAlsoFirst = result.size() == 1;
         int lastImageReserve = lastIsAlsoFirst ? firstImageHeight + firstImageGap : 0;
         int lastUsedHeight = last.lines().size() * lineUnit + lastImageReserve;
         int lastRemaining = availableHeight - lastUsedHeight;
         int totalBlockHeight = recipeAndItemsHeight(page, textWidth);

         if (totalBlockHeight > 0 && totalBlockHeight <= lastRemaining) {
            result.set(result.size() - 1,
               new SubPage(last.lines(), last.showImage(), 0, recipes.size()));
         } else {

            boolean hasBrewing = recipes.stream().anyMatch(r -> r.type == CompendiumData.Recipe.Type.BREWING);
            int colsForPaging = hasBrewing ? 1 : 2;
            int rowH = recipeGroupRowHeight();
            int rowsPerPage = Math.max(1, (availableHeight - this.font.lineHeight - 12) / rowH);
            int recipesPerPage = rowsPerPage * colsForPaging;
            if (!recipes.isEmpty()) {
               for (int start = 0; start < recipes.size(); start += recipesPerPage) {
                  int end = Math.min(recipes.size(), start + recipesPerPage);
                  result.add(new SubPage(List.of(), false, start, end));
               }
            } else {
               result.add(new SubPage(List.of(), false, 0, 0));
            }
         }
      }
      return result;
   }

   private int recipeAndItemsHeight(CompendiumData.Page page, int width) {
      List<CompendiumData.Recipe> recipes = page.recipes != null && !page.recipes.isEmpty()
         ? page.recipes
         : (page.recipe != null ? List.of(page.recipe) : List.of());
      int height = 0;
      if (!recipes.isEmpty()) {
         boolean hasBrewing = recipes.stream().anyMatch(r -> r.type == CompendiumData.Recipe.Type.BREWING);
         int cols = (recipes.size() >= 2 && !hasBrewing) ? 2 : 1;
         int rows = (int)Math.ceil(recipes.size() / (double)cols);
         height += this.font.lineHeight + 4 + rows * recipeGroupRowHeight() + 8;
      }
      if (page.showItemNames && !page.itemIds.isEmpty()) {
         if (height > 0) height += 2;
         height += itemNameRowHeight(page.itemIds, width);
      }
      return height;
   }

   private int recipeGroupRowHeight() {
      return this.font.lineHeight + 4 + Math.max(RECIPE_CELL * 3, BREW_PANEL_H) + 8;
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      linkHitboxes.clear();
      gridHitboxes.clear();
      hoveredRecipeStack = ItemStack.EMPTY;
      currentMouseX = mouseX;
      currentMouseY = mouseY;

      graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);

      drawBevel(graphics, panelX, panelY, panelW, panelH, PANEL_BG, PANEL_BEVEL_LIGHT, PANEL_BEVEL_DARK);
      graphics.fill(panelX - 1, panelY - 1, panelX + panelW + 1, panelY, PANEL_OUTER_BORDER);
      graphics.fill(panelX - 1, panelY + panelH, panelX + panelW + 1, panelY + panelH + 1, PANEL_OUTER_BORDER);
      graphics.fill(panelX - 1, panelY, panelX, panelY + panelH, PANEL_OUTER_BORDER);
      graphics.fill(panelX + panelW, panelY, panelX + panelW + 1, panelY + panelH, PANEL_OUTER_BORDER);

      CompendiumData.Page page = currentPage();

      int textX = panelX + 12;
      int textY = panelY + 12;
      int textWidth = panelW - 24;

      graphics.drawString(this.font, Component.literal(page.title), textX, textY, TITLE_COLOR, false);
      textY += this.font.lineHeight + 8;

      if (page.gridEntries != null) {

         int gridBottom = panelY + panelH - 8;
         renderGrid(graphics, page.gridEntries, textX, textY, textWidth, gridBottom, mouseX, mouseY);
      } else {
         renderSubPage(graphics, page, textX, textY, textWidth, mouseX, mouseY);
         if (!hoveredRecipeStack.isEmpty()) {
            graphics.setTooltipForNextFrame(this.font, hoveredRecipeStack, mouseX, mouseY);
         }

         if (subPages.size() > 1) {
            String counter = (subPageIndex + 1) + " / " + subPages.size();
            graphics.drawString(this.font, Component.literal(counter),
               panelX + panelW - this.font.width(counter) - 8, panelY + panelH - 12, COUNTER_COLOR, false);
         }
      }
   }

   private void renderSubPage(GuiGraphics graphics, CompendiumData.Page page,
                               int textX, int textY, int textWidth, int mouseX, int mouseY) {
      if (subPages.isEmpty()) return;
      SubPage sub = subPages.get(Math.min(subPageIndex, subPages.size() - 1));

      if (sub.showImage()) {
         if (page.imageId != null) {
            ResourceLocation imgId = ResourceLocation.tryParse(page.imageId);
            if (imgId != null) {

               int destW = Math.min(page.imageWidth, textWidth);
               int destH = Math.max(1, page.imageHeight * destW / page.imageWidth);
               graphics.enableScissor(textX, textY,
                  Math.min(textX + destW, panelX + panelW - 8),
                  panelY + panelH - 8);
               graphics.blit(RenderPipelines.GUI_TEXTURED, imgId, textX, textY, 0.0F, 0.0F,
                  destW, destH, destW, destH);
               graphics.disableScissor();
               textY += destH + 8;
            }
         } else {
            int bh = bannerHeight(textWidth);
            drawModBanner(graphics, textX, textY, textWidth, bh);
            textY += bh + 8;
         }
      }

      int spaceWidth = this.font.width(" ");
      for (Line line : sub.lines()) {
         if (line.blank()) {
            textY += this.font.lineHeight + 2;
            continue;
         }
         int lineX = textX;
         for (Word word : line.words()) {
            int wordWidth = this.font.width(word.text());
            if (word.isLink()) {
               boolean hovered = mouseX >= lineX && mouseX < lineX + wordWidth
                  && mouseY >= textY && mouseY < textY + this.font.lineHeight;
               int color = hovered ? LINK_HOVER_COLOR : LINK_COLOR;
               graphics.drawString(this.font,
                  Component.literal(word.text()).withStyle(ChatFormatting.UNDERLINE),
                  lineX, textY, color, false);
               linkHitboxes.add(new LinkHitbox(lineX, textY, wordWidth, this.font.lineHeight,
                  word.linkChapter(), word.linkPage()));
            } else {
               graphics.drawString(this.font, Component.literal(word.text()), lineX, textY, BODY_COLOR, false);
            }
            lineX += wordWidth + spaceWidth;
         }
         textY += this.font.lineHeight + 2;
      }

      if (sub.showRecipeAndItems()) {
         List<CompendiumData.Recipe> allRecipes = page.recipes != null && !page.recipes.isEmpty()
            ? page.recipes : (page.recipe != null ? List.of(page.recipe) : List.of());
         int start = Math.max(0, Math.min(sub.recipeStart(), allRecipes.size()));
         int end = Math.max(start, Math.min(sub.recipeEnd(), allRecipes.size()));
         List<CompendiumData.Recipe> recipes = allRecipes.subList(start, end);
         if (!recipes.isEmpty()) textY += renderRecipeGroup(graphics, recipes, textX, textY, textWidth);
         if (page.showItemNames && !page.itemIds.isEmpty() && sub.recipeEnd() >= allRecipes.size()) {
            if (!recipes.isEmpty()) textY += 2;
            renderItemNameRow(graphics, page.itemIds, textX, textY, textWidth);
         }
      }
   }

   private void drawModBanner(GuiGraphics graphics, int x, int y, int width, int height) {
      graphics.enableScissor(x, y, x + width, y + height);
      graphics.blit(RenderPipelines.GUI_TEXTURED, BANNER_TEXTURE,
         x, y, 0.0F, 0.0F,
         width, height,
         width, height);
      graphics.disableScissor();
   }

   private int renderItemNameRow(GuiGraphics graphics, List<String> itemIds, int x, int y, int width) {
      List<String> names = new ArrayList<>();
      for (String itemId : itemIds) {
         ResourceLocation id = ResourceLocation.tryParse(itemId);
         if (id == null) continue;
         Item item = BuiltInRegistries.ITEM.getValue(id);
         if (item == null) continue;
         names.add(Component.translatable(item.getDescriptionId()).getString());
      }
      if (names.isEmpty()) return 0;

      String joined = String.join("  •  ", names);
      List<String> lines = wrapPlainText(joined, width);
      for (String line : lines) {
         graphics.drawString(this.font, Component.literal(line), x, y, TITLE_COLOR, false);
         y += this.font.lineHeight + 2;
      }
      return lines.size() * (this.font.lineHeight + 2);
   }

   private int itemNameRowHeight(List<String> itemIds, int width) {
      List<String> names = new ArrayList<>();
      for (String itemId : itemIds) {
         ResourceLocation id = ResourceLocation.tryParse(itemId);
         if (id == null) continue;
         Item item = BuiltInRegistries.ITEM.getValue(id);
         if (item == null) continue;
         names.add(Component.translatable(item.getDescriptionId()).getString());
      }
      if (names.isEmpty()) return 0;
      String joined = String.join("  •  ", names);
      return wrapPlainText(joined, width).size() * (this.font.lineHeight + 2);
   }

   private static final int RECIPE_CELL = 18;

   private int renderRecipeGroup(GuiGraphics graphics, List<CompendiumData.Recipe> recipes, int x, int y, int width) {

      boolean hasBrewing = recipes.stream().anyMatch(r -> r.type == CompendiumData.Recipe.Type.BREWING);
      int cols = (recipes.size() >= 2 && !hasBrewing) ? 2 : 1;
      int colW = width / cols;
      int rowH = recipeGroupRowHeight();
      for (int n = 0; n < recipes.size(); n++) {
         int col = n % cols;
         int row = n / cols;
         int rx = x + col * colW;
         int ry = y + row * rowH;

         renderRecipeCompact(graphics, recipes.get(n), rx, ry, colW, cols == 1);
      }
      int rows = (int)Math.ceil(recipes.size() / (double)cols);
      return rows * rowH;
   }

   private void renderRecipeCompact(GuiGraphics graphics, CompendiumData.Recipe recipe, int x, int y, int width, boolean leftAlign) {
      String outName = labelForItem(recipe.outputItemId);
      String[] nameLines = wrapLabelTwoLines(outName, width - 4);
      int nameY = y;
      for (String line : nameLines) {
         int lineX = x;
         if (!leftAlign) {
            int w = this.font.width(line);
            lineX = x + Math.max(0, (width - w) / 2);
         }
         graphics.drawString(this.font, Component.literal(line), lineX, nameY, LABEL_COLOR, false);
         nameY += this.font.lineHeight + 1;
      }
      int gridY = y + this.font.lineHeight * 2 + 3;
      if (recipe.type == CompendiumData.Recipe.Type.SMELTING) {
         drawRecipeSlot(graphics, recipe.smeltingInput, x + 2, gridY + RECIPE_CELL);
         int arrowX = x + RECIPE_CELL + 6;
         graphics.drawString(this.font, Component.literal("→"), arrowX, gridY + RECIPE_CELL + 2, BODY_COLOR, false);
         drawRecipeSlot(graphics, recipe.outputItemId, arrowX + 18, gridY + RECIPE_CELL);
      } else if (recipe.type == CompendiumData.Recipe.Type.BREWING) {
         int bx = x + 2;

         graphics.blit(RenderPipelines.GUI_TEXTURED, BREWING_STAND_TEXTURE, bx, gridY,
            0.0F, 0.0F, BREW_PANEL_W, BREW_PANEL_H, 256, 256);

         placeStandItem(graphics, BLAZE_POWDER, bx + BREW_FUEL_X, gridY + BREW_FUEL_Y);
         placeStandItem(graphics, recipe.brewingIngredient, bx + BREW_INGREDIENT_X, gridY + BREW_INGREDIENT_Y);

         int arrowX = bx + BREW_PANEL_W + 6;
         int arrowY = gridY + (BREW_PANEL_H - this.font.lineHeight) / 2;
         graphics.drawString(this.font, Component.literal("→"), arrowX, arrowY, BODY_COLOR, false);

         VialCycleFrame cycle = vialCycleFor(recipe);
         if (cycle != null) {

            if (cycle.bottleStacks().size() == 3) {
               placeStandItem(graphics, cycle.bottleStacks().get(0), bx + BREW_BOTTLE_X[0], gridY + BREW_BOTTLE_Y[0]);
               placeStandItem(graphics, cycle.bottleStacks().get(1), bx + BREW_BOTTLE_X[1], gridY + BREW_BOTTLE_Y[1]);
               placeStandItem(graphics, cycle.bottleStacks().get(2), bx + BREW_BOTTLE_X[2], gridY + BREW_BOTTLE_Y[2]);
            } else {
               placeStandItem(graphics, cycle.bottleStacks().get(0), bx + BREW_BOTTLE_X[1], gridY + BREW_BOTTLE_Y[1]);
            }
            drawRecipeSlotStack(graphics, cycle.outputStack(), arrowX + 12, arrowY - 1);
         } else {

            if (nonNullBottleCount(recipe.brewingBottles) == 3) {
               for (int i = 0; i < 3; i++) {
                  placeStandItem(graphics, recipe.brewingBottles[i], bx + BREW_BOTTLE_X[i], gridY + BREW_BOTTLE_Y[i]);
               }
            } else {
               for (String bottle : recipe.brewingBottles) {
                  if (bottle != null) {
                     placeStandItem(graphics, bottle, bx + BREW_BOTTLE_X[1], gridY + BREW_BOTTLE_Y[1]);
                     break;
                  }
               }
            }
            drawRecipeSlot(graphics, recipe.outputItemId, arrowX + 12, arrowY - 1);
         }
      } else {
         int gx = x + 2;
         for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            drawRecipeSlot(graphics, recipe.grid[i], gx + col * RECIPE_CELL, gridY + row * RECIPE_CELL);
         }
         int arrowX = gx + RECIPE_CELL * 3 + 6;
         graphics.drawString(this.font, Component.literal("→"), arrowX, gridY + RECIPE_CELL + 2, BODY_COLOR, false);
         drawRecipeSlot(graphics, recipe.outputItemId, arrowX + 18, gridY + RECIPE_CELL);
      }
   }

   private int renderRecipe(GuiGraphics graphics, CompendiumData.Recipe recipe, int x, int y) {
      graphics.drawString(this.font, Component.literal("Recipe"), x, y, TITLE_COLOR, false);
      int gridY = y + this.font.lineHeight + 4;

      int gridWidth;
      if (recipe.type == CompendiumData.Recipe.Type.SMELTING) {
         gridWidth = RECIPE_CELL;
         drawRecipeSlot(graphics, recipe.smeltingInput, x, gridY + RECIPE_CELL);
      } else if (recipe.type == CompendiumData.Recipe.Type.BREWING) {
         gridWidth = RECIPE_CELL * 3;
         drawRecipeSlot(graphics, recipe.brewingIngredient, x + RECIPE_CELL, gridY);
         for (int i = 0; i < 3; i++) {
            drawRecipeSlot(graphics, recipe.brewingBottles[i], x + i * RECIPE_CELL, gridY + RECIPE_CELL);
         }
      } else {
         gridWidth = RECIPE_CELL * 3;
         for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            drawRecipeSlot(graphics, recipe.grid[i], x + col * RECIPE_CELL, gridY + row * RECIPE_CELL);
         }
      }

      String arrow = "=>";
      int arrowX = x + gridWidth + 6;
      int arrowY = gridY + RECIPE_CELL + (RECIPE_CELL - this.font.lineHeight) / 2;
      graphics.drawString(this.font, Component.literal(arrow), arrowX, arrowY, BODY_COLOR, false);

      int outX = arrowX + this.font.width(arrow) + 8;
      int outY = gridY + RECIPE_CELL;
      drawRecipeSlot(graphics, recipe.outputItemId, outX, outY);
      if (recipe.outputCount > 1) {
         ResourceLocation outId = ResourceLocation.tryParse(recipe.outputItemId);
         Item outItem = outId != null ? BuiltInRegistries.ITEM.getValue(outId) : null;
         if (outItem != null) {
            graphics.renderItemDecorations(this.font, new ItemStack(outItem, recipe.outputCount), outX, outY);
         }
      }

      return recipeVisualHeight(recipe);
   }

   private int recipeVisualHeight(CompendiumData.Recipe recipe) {
      int gridRows = switch (recipe.type) {
         case SMELTING -> 1;
         case BREWING -> 2;
         case CRAFTING -> 3;
      };
      return this.font.lineHeight + 4 + (gridRows * RECIPE_CELL) + 8;
   }

   private void drawRecipeSlot(GuiGraphics graphics, String itemId, int x, int y) {
      Item item = itemId != null ? resolveItem(itemId) : null;
      drawRecipeSlotStack(graphics, item != null ? new ItemStack(item) : ItemStack.EMPTY, x, y);
   }

   private void drawRecipeSlotStack(GuiGraphics graphics, ItemStack stack, int x, int y) {
      drawSlot(graphics, x, y, RECIPE_CELL, RECIPE_CELL, false);
      if (stack == null || stack.isEmpty()) return;
      graphics.renderItem(stack, x + 1, y + 1);
      if (currentMouseX >= x && currentMouseX < x + RECIPE_CELL
            && currentMouseY >= y && currentMouseY < y + RECIPE_CELL) {
         hoveredRecipeStack = stack;
      }
   }

   private void placeStandItem(GuiGraphics graphics, String itemId, int x, int y) {
      if (itemId == null) return;
      Item item = resolveItem(itemId);
      if (item == null) return;
      placeStandItem(graphics, new ItemStack(item), x, y);
   }

   private void placeStandItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
      if (stack == null || stack.isEmpty()) return;
      graphics.renderItem(stack, x, y);
      if (currentMouseX >= x && currentMouseX < x + 16
            && currentMouseY >= y && currentMouseY < y + 16) {
         hoveredRecipeStack = stack;
      }
   }

   private void drawBevel(GuiGraphics graphics, int x, int y, int w, int h, int fill, int light, int dark) {
      graphics.fill(x, y, x + w, y + h, fill);
      graphics.fill(x, y, x + w, y + 2, light);
      graphics.fill(x, y, x + 2, y + h, light);
      graphics.fill(x, y + h - 2, x + w, y + h, dark);
      graphics.fill(x + w - 2, y, x + w, y + h, dark);
   }

   private void drawSlot(GuiGraphics graphics, int x, int y, int w, int h, boolean hovered) {
      int fill = hovered ? SLOT_HOVER_BG : SLOT_BG;
      graphics.fill(x, y, x + w, y + h, fill);
      graphics.fill(x, y, x + w, y + 1, SLOT_BEVEL_DARK);
      graphics.fill(x, y, x + 1, y + h, SLOT_BEVEL_DARK);
      graphics.fill(x, y + h - 1, x + w, y + h, SLOT_BEVEL_LIGHT);
      graphics.fill(x + w - 1, y, x + w, y + h, SLOT_BEVEL_LIGHT);
   }

   private void renderGrid(GuiGraphics graphics, List<CompendiumData.GridEntry> entries,
                           int areaX, int areaTop, int areaWidth, int areaBottom,
                           int mouseX, int mouseY) {

      int columns = Math.min(2, Math.max(1, areaWidth / GRID_CELL_W));
      int gridWidth = columns * GRID_CELL_W;
      int startX = areaX + Math.max(0, (areaWidth - gridWidth) / 2);
      graphics.enableScissor(areaX, areaTop, areaX + areaWidth, areaBottom);

      int col = 0, row = 0;
      for (CompendiumData.GridEntry entry : entries) {
         int cellX = startX + col * GRID_CELL_W;
         int cellY = areaTop + row * GRID_CELL_H - gridScroll;
         if (cellY + GRID_CELL_H >= areaTop && cellY <= areaBottom) {
            boolean hovered = mouseX >= cellX && mouseX < cellX + GRID_CELL_W
               && mouseY >= cellY && mouseY < cellY + GRID_CELL_H
               && mouseY >= areaTop && mouseY < areaBottom;
            drawSlot(graphics, cellX + 2, cellY + 2, GRID_CELL_W - 4, GRID_CELL_H - 4, hovered);

            List<String> ids = entry.itemIds != null && !entry.itemIds.isEmpty()
               ? entry.itemIds : List.of(entry.itemId);
            if (ids.size() <= 5) {
               int iconBox = 20;
               int totalH = ids.size() * iconBox;
               int sy = cellY + 8 + Math.max(0, (GRID_CELL_H - 34 - totalH) / 2);
               for (int n = 0; n < ids.size(); n++) {
                  int sx = cellX + 8;
                  int by = sy + n * iconBox;
                  drawSlot(graphics, sx, by, iconBox, iconBox, hovered);
                  Item item = itemFromId(ids.get(n));
                  if (item != null) graphics.renderItem(new ItemStack(item), sx + 2, by + 2);
               }
               String label = entry.label != null ? entry.label : labelForItem(ids.get(0));
               renderTileLabel(graphics, label, cellX + 32, cellY + 12, GRID_CELL_W - 40, hovered);
            } else {
               int iconBox = 20;
               int colsMini = Math.min(7, ids.size());
               int rowsMini = (int)Math.ceil(ids.size() / (double)colsMini);
               int totalW = colsMini * iconBox;
               int sx0 = cellX + Math.max(2, (GRID_CELL_W - totalW) / 2);
               int sy0 = cellY + 7;
               for (int n = 0; n < ids.size(); n++) {
                  int sx = sx0 + (n % colsMini) * iconBox;
                  int sy = sy0 + (n / colsMini) * iconBox;
                  drawSlot(graphics, sx, sy, iconBox, iconBox, hovered);
                  Item item = itemFromId(ids.get(n));
                  if (item != null) graphics.renderItem(new ItemStack(item), sx + 2, sy + 2);
               }
               String label = entry.label != null ? entry.label : labelForItem(ids.get(0));
               renderTileLabel(graphics, label, cellX + 4, sy0 + rowsMini * iconBox + 4, GRID_CELL_W - 8, hovered);
            }

            int hitTop = Math.max(cellY, areaTop);
            int hitBottom = Math.min(cellY + GRID_CELL_H, areaBottom);
            if (hitBottom > hitTop) {
               gridHitboxes.add(new GridHitbox(cellX, hitTop, GRID_CELL_W, hitBottom - hitTop,
                  findChapterIndex(entry.targetChapter), findPageIndexIn(entry.targetChapter, entry.targetPage)));
            }
         }
         col++;
         if (col >= columns) { col = 0; row++; }
      }
      graphics.disableScissor();
   }

   private Item itemFromId(String id) {
      ResourceLocation rid = ResourceLocation.tryParse(id);
      return rid != null ? BuiltInRegistries.ITEM.getValue(rid) : null;
   }

   private String labelForItem(String id) {
      Item item = itemFromId(id);
      return item != null ? Component.translatable(item.getDescriptionId()).getString() : id;
   }

   private void renderTileLabel(GuiGraphics graphics, String label, int x, int y, int width, boolean hovered) {
      String[] lines = wrapLabelTwoLines(label, width);
      int color = hovered ? LABEL_HOVER_COLOR : LABEL_COLOR;
      for (String line : lines) {
         int w = this.font.width(line);
         graphics.drawString(this.font, Component.literal(line), x + Math.max(0, (width - w) / 2), y, color, false);
         y += this.font.lineHeight + 1;
      }
   }

   private int gridContentHeight(int areaWidth) {
      CompendiumData.Page page = currentPage();
      if (page.gridEntries == null) return 0;
      int columns = Math.min(2, Math.max(1, areaWidth / GRID_CELL_W));
      int rows = (int) Math.ceil(page.gridEntries.size() / (double) columns);
      return rows * GRID_CELL_H;
   }

   private static int findChapterIndex(String chapterName) {
      List<CompendiumData.Chapter> chapters = CompendiumData.CHAPTERS;
      for (int c = 0; c < chapters.size(); c++) {
         if (chapters.get(c).name.equalsIgnoreCase(chapterName.trim())) return c;
      }
      return -1;
   }

   private static int findPageIndexIn(String chapterName, String pageTitle) {
      int[] target = findPage(chapterName, pageTitle);
      return target != null ? target[1] : -1;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      for (LinkHitbox hitbox : linkHitboxes) {
         if (hitbox.contains(mouseX, mouseY)) {
            navigateTo(hitbox.targetChapter(), hitbox.targetPage());
            rebuildButtons();
            return true;
         }
      }
      for (GridHitbox hitbox : gridHitboxes) {
         if (hitbox.contains(mouseX, mouseY) && hitbox.targetChapter() >= 0 && hitbox.targetPage() >= 0) {
            navigateTo(hitbox.targetChapter(), hitbox.targetPage());
            rebuildButtons();
            return true;
         }
      }
      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

      CompendiumData.Page page = currentPage();
      if (page.gridEntries != null) {
         int textWidth = panelW - 24;
         int contentHeight = gridContentHeight(textWidth);
         int areaTop = panelY + 12 + this.font.lineHeight + 8;
         int areaBottom = panelY + panelH - 8;
         int viewportHeight = areaBottom - areaTop;
         int maxScroll = Math.max(0, contentHeight - viewportHeight);
         gridScroll = Math.max(0, Math.min(maxScroll, gridScroll - (int) (scrollY * GRID_CELL_H / 2)));
         return true;
      }
      return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
   }
}

