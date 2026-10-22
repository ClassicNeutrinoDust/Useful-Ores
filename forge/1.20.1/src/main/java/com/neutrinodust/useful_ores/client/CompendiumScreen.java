package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.compendium.CompendiumData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;
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
   private int tabW = 100;

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
      int margin = 6;
      int gap = 6;
      tabW = 100;
      panelW = Math.min(340, this.width - tabW - gap - margin * 2);
      if (panelW < 250) {
         tabW = 70;
         panelW = Math.min(340, this.width - tabW - gap - margin * 2);
      }
      panelW = Math.max(160, panelW);
      panelH = Math.max(110, Math.min(240, this.height - 70));
      panelX = (this.width - panelW) / 2;
      int minPanelX = margin + tabW + gap;
      if (panelX < minPanelX) panelX = minPanelX;
      panelY = (this.height - panelH) / 2;

      chapterIndex = Math.min(chapterIndex, CompendiumData.CHAPTERS.size() - 1);
      pageIndex = 0;
      subPageIndex = 0;
      subPages = List.of();

      rebuildButtons();
   }

   private static String chapterTranslationKey(String name) {
      return switch (name) {
         case "Ores & Materials" -> "compendium.chapter.ores_materials";
         case "Redstone" -> "compendium.chapter.redstone";
         case "Tools & Combat" -> "compendium.chapter.tools_combat";
         case "Utility" -> "compendium.chapter.utility";
         default -> "compendium.chapter.misc";
      };
   }

   private void rebuildButtons() {
      this.clearWidgets();

      List<CompendiumData.Chapter> chapters = CompendiumData.CHAPTERS;
      int tabH = Math.max(12, Math.min(20, (panelH - (chapters.size() - 1) * 4) / chapters.size()));
      int tabX = panelX - tabW - 6;
      for (int i = 0; i < chapters.size(); i++) {
         int idx = i;
         Component label = Component.translatable(chapterTranslationKey(chapters.get(i).name));
         this.addRenderableWidget(Button.builder(label, btn -> navigateTo(idx, 0))
            .bounds(tabX, panelY + i * (tabH + 4), tabW, tabH).build());
      }

      int navY = panelY + panelH + 8;
      int bw = Math.min(70, (panelW - 12) / 4);

      backButton = this.addRenderableWidget(Button.builder(Component.translatable("compendium.button.back"), btn -> navigateTo(chapterIndex, 0))
         .bounds(panelX, navY, bw, 20).build());

      prevButton = this.addRenderableWidget(Button.builder(Component.translatable("compendium.button.previous"), btn -> {
         if (subPageIndex > 0) subPageIndex--;
         updateNavButtonStates();
      }).bounds(panelX + bw + 4, navY, bw, 20).build());

      nextButton = this.addRenderableWidget(Button.builder(Component.translatable("compendium.button.next"), btn -> {
         if (subPageIndex < subPages.size() - 1) subPageIndex++;
         updateNavButtonStates();
      }).bounds(panelX + panelW - bw * 2 - 4, navY, bw, 20).build());

      this.addRenderableWidget(Button.builder(Component.translatable("compendium.button.done"), btn -> this.onClose())
         .bounds(panelX + panelW - bw, navY, bw, 20).build());

      updateNavButtonStates();
   }

   private void drawRecipeArrow(GuiGraphics g, int x, int y) {
      for (int pass = 0; pass < 2; pass++) {
         int o = pass == 0 ? 1 : 0;
         int body = pass == 0 ? 0x40000000 : 0xFF4A4A4A;
         int hi = pass == 0 ? 0x40000000 : 0xFF7A7A7A;
         g.fill(x + o, y + 3 + o, x + 7 + o, y + 6 + o, body);
         g.fill(x + o, y + 3 + o, x + 7 + o, y + 4 + o, hi);
         for (int c = 0; c < 5; c++) {
            int h = 4 - c;
            g.fill(x + 6 + c + o, y + 4 - h + o, x + 7 + c + o, y + 5 + h + o, c == 0 ? hi : body);
         }
      }
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

   private record PotionPair(Potion a, Potion b) {}

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
      return id != null ? BuiltInRegistries.ITEM.get(id) : null;
   }

   private static ItemStack examplePotionStack(Potion potion) {
      ItemStack stack = new ItemStack(Items.POTION);
      PotionUtils.setPotion(stack, potion);
      return stack;
   }

   private static ItemStack exampleMergedVialStack(Item vialItem, PotionPair pair) {
      List<MobEffectInstance> merged = new ArrayList<>();
      merged.addAll(pair.a().getEffects());
      merged.addAll(pair.b().getEffects());
      ItemStack stack = new ItemStack(vialItem);
      PotionUtils.setCustomEffects(stack, merged);
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
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
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

      graphics.drawString(this.font, localizedPageTitle(page.title), textX, textY, TITLE_COLOR, false);
      textY += this.font.lineHeight + 8;

      if (page.gridEntries != null) {

         int gridBottom = panelY + panelH - 8;
         renderGrid(graphics, page.gridEntries, textX, textY, textWidth, gridBottom, mouseX, mouseY);
      } else {
         renderSubPage(graphics, page, textX, textY, textWidth, mouseX, mouseY);
         if (!hoveredRecipeStack.isEmpty()) {
            graphics.renderTooltip(this.font, hoveredRecipeStack, mouseX, mouseY);
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
               graphics.blit(imgId, textX, textY, 0.0F, 0.0F,
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
      graphics.blit(BANNER_TEXTURE,
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
         Item item = BuiltInRegistries.ITEM.get(id);
         if (item == null) continue;
         names.add(Component.translatable(item.getDescriptionId()).getString());
      }
      if (names.isEmpty()) return 0;

      String joined = String.join("  \u2022  ", names);
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
         Item item = BuiltInRegistries.ITEM.get(id);
         if (item == null) continue;
         names.add(Component.translatable(item.getDescriptionId()).getString());
      }
      if (names.isEmpty()) return 0;
      String joined = String.join("  \u2022  ", names);
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
         drawRecipeArrow(graphics, arrowX, gridY + RECIPE_CELL + 5);
         drawRecipeSlot(graphics, recipe.outputItemId, arrowX + 18, gridY + RECIPE_CELL);
      } else if (recipe.type == CompendiumData.Recipe.Type.BREWING) {
         int bx = x + 2;

         graphics.blit(BREWING_STAND_TEXTURE, bx, gridY,
            0.0F, 0.0F, BREW_PANEL_W, BREW_PANEL_H, 256, 256);

         placeStandItem(graphics, BLAZE_POWDER, bx + BREW_FUEL_X, gridY + BREW_FUEL_Y);
         placeStandItem(graphics, recipe.brewingIngredient, bx + BREW_INGREDIENT_X, gridY + BREW_INGREDIENT_Y);

         int arrowX = bx + BREW_PANEL_W + 6;
         int arrowY = gridY + (BREW_PANEL_H - this.font.lineHeight) / 2;
         drawRecipeArrow(graphics, arrowX, arrowY);

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
         drawRecipeArrow(graphics, arrowX, gridY + RECIPE_CELL + 5);
         drawRecipeSlot(graphics, recipe.outputItemId, arrowX + 18, gridY + RECIPE_CELL);
      }
   }

   private int renderRecipe(GuiGraphics graphics, CompendiumData.Recipe recipe, int x, int y) {
      graphics.drawString(this.font, Component.translatable("compendium.recipe"), x, y, TITLE_COLOR, false);
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

      int arrowX = x + gridWidth + 6;
      int arrowY = gridY + RECIPE_CELL + (RECIPE_CELL - this.font.lineHeight) / 2;
      drawRecipeArrow(graphics, arrowX, gridY + RECIPE_CELL + 5);

      int outX = arrowX + 16;
      int outY = gridY + RECIPE_CELL;
      drawRecipeSlot(graphics, recipe.outputItemId, outX, outY);
      if (recipe.outputCount > 1) {
         ResourceLocation outId = ResourceLocation.tryParse(recipe.outputItemId);
         Item outItem = outId != null ? BuiltInRegistries.ITEM.get(outId) : null;
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
               String label = entry.label != null ? localizedPageTitleText(entry.label) : labelForItem(ids.get(0));
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
               String label = entry.label != null ? localizedPageTitleText(entry.label) : labelForItem(ids.get(0));
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
      return rid != null ? BuiltInRegistries.ITEM.get(rid) : null;
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
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {

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
      return super.mouseScrolled(mouseX, mouseY, scrollY);
   }

   private static Component localizedPageTitle(String title) {
      return Component.literal(localizedPageTitleText(title));
   }

   private static String localizedMaterialTitleName(String material, String base) {
      return switch (base) {
         case "es" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanita"; case "argentite" -> "argentita"; case "chromite" -> "cromita"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurita"; case "ilmenite" -> "ilmenita"; case "lonsdaleite" -> "lonsdale\u00edta"; case "nyxium" -> "nyxium"; case "osmium" -> "osmio"; case "painite" -> "painita"; case "phosgene" -> "fosgeno"; case "scheelite" -> "scheelita"; case "solarite" -> "solarita"; case "sperrylite" -> "sperrylita"; case "subspace" -> "subespacio"; case "voidshard" -> "esquirla del vac\u00edo"; case "zephyrite" -> "zefirita"; default -> material; };
         case "pt" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanita"; case "argentite" -> "argentita"; case "chromite" -> "cromita"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurita"; case "ilmenite" -> "ilmenita"; case "lonsdaleite" -> "lonsdale\u00edta"; case "nyxium" -> "nyxium"; case "osmium" -> "\u00f3smio"; case "painite" -> "painita"; case "phosgene" -> "fosg\u00eanio"; case "scheelite" -> "scheelita"; case "solarite" -> "solarita"; case "sperrylite" -> "sperrylita"; case "subspace" -> "subespa\u00e7o"; case "voidshard" -> "estilha\u00e7o do vazio"; case "zephyrite" -> "zefirita"; default -> material; };
         case "fr" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanite"; case "argentite" -> "argentite"; case "chromite" -> "chromite"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurite"; case "ilmenite" -> "ilm\u00e9nite"; case "lonsdaleite" -> "lonsdal\u00e9ite"; case "nyxium" -> "nyxium"; case "osmium" -> "osmium"; case "painite" -> "painite"; case "phosgene" -> "phosg\u00e8ne"; case "scheelite" -> "scheelite"; case "solarite" -> "solarite"; case "sperrylite" -> "sperrylite"; case "subspace" -> "sous-espace"; case "voidshard" -> "\u00e9clat du vide"; case "zephyrite" -> "z\u00e9phyrite"; default -> material; };
         case "it" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanite"; case "argentite" -> "argentite"; case "chromite" -> "cromite"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurite"; case "ilmenite" -> "ilmenite"; case "lonsdaleite" -> "lonsdaleite"; case "nyxium" -> "nyxium"; case "osmium" -> "osmio"; case "painite" -> "painite"; case "phosgene" -> "fosgene"; case "scheelite" -> "scheelite"; case "solarite" -> "solarite"; case "sperrylite" -> "sperrylite"; case "subspace" -> "sottospazio"; case "voidshard" -> "scheggia del vuoto"; case "zephyrite" -> "zefirite"; default -> material; };
         case "ru" -> switch (material.toLowerCase()) { case "arcanite" -> "\u0430\u0440\u043a\u0430\u043d\u0438\u0442"; case "argentite" -> "\u0430\u0440\u0433\u0435\u043d\u0442\u0438\u0442"; case "chromite" -> "\u0445\u0440\u043e\u043c\u0438\u0442"; case "enderium" -> "\u044d\u043d\u0434\u0435\u0440\u0438\u0443\u043c"; case "fulgurite" -> "\u0444\u0443\u043b\u044c\u0433\u0443\u0440\u0438\u0442"; case "ilmenite" -> "\u0438\u043b\u044c\u043c\u0435\u043d\u0438\u0442"; case "lonsdaleite" -> "\u043b\u043e\u043d\u0441\u0434\u0435\u0439\u043b\u0438\u0442"; case "nyxium" -> "\u043d\u0438\u043a\u0441\u0438\u0443\u043c"; case "osmium" -> "\u043e\u0441\u043c\u0438\u0439"; case "painite" -> "\u043f\u0435\u0439\u043d\u0438\u0442"; case "phosgene" -> "\u0444\u043e\u0441\u0433\u0435\u043d"; case "scheelite" -> "\u0448\u0435\u0435\u043b\u0438\u0442"; case "solarite" -> "\u0441\u043e\u043b\u044f\u0440\u0438\u0442"; case "sperrylite" -> "\u0441\u043f\u0435\u0440\u0440\u0438\u043b\u0438\u0442"; case "subspace" -> "\u043f\u043e\u0434\u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u043e"; case "voidshard" -> "\u043e\u0441\u043a\u043e\u043b\u043e\u043a \u043f\u0443\u0441\u0442\u043e\u0442\u044b"; case "zephyrite" -> "\u0437\u0435\u0444\u0438\u0440\u0438\u0442"; default -> material; };
         case "zh" -> switch (material.toLowerCase()) { case "arcanite" -> "\u5965\u672f\u77f3"; case "argentite" -> "\u8f89\u94f6\u77ff"; case "chromite" -> "\u94ec\u94c1\u77ff"; case "enderium" -> "\u672b\u5f71\u7d20"; case "fulgurite" -> "\u96f7\u51fb\u77f3"; case "ilmenite" -> "\u949b\u94c1\u77ff"; case "lonsdaleite" -> "\u516d\u65b9\u91d1\u521a\u77f3"; case "nyxium" -> "\u6697\u591c\u7d20"; case "osmium" -> "\u9507"; case "painite" -> "\u7ea2\u787c\u9499\u94dd\u77f3"; case "phosgene" -> "\u5149\u6c14"; case "scheelite" -> "\u767d\u94a8\u77ff"; case "solarite" -> "\u65e5\u8000\u77f3"; case "sperrylite" -> "\u7837\u94c2\u77ff"; case "subspace" -> "\u4e9a\u7a7a\u95f4"; case "voidshard" -> "\u865a\u7a7a\u788e\u7247"; case "zephyrite" -> "\u548c\u98ce\u77f3"; default -> material; };
         default -> material;
      };
   }

   private static String localizedPageTitleText(String title) {
      String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
      String base = lang.contains("_") ? lang.substring(0, lang.indexOf('_')) : lang;
      if (title.equals("Special Arrows")) return switch (base) {
         case "es" -> "Flechas especiales"; case "pt" -> "Flechas especiais";
         case "fr" -> "Fl\u00e8ches sp\u00e9ciales"; case "it" -> "Frecce speciali";
         case "ru" -> "\u041e\u0441\u043e\u0431\u044b\u0435 \u0441\u0442\u0440\u0435\u043b\u044b"; case "zh" -> "\u7279\u6b8a\u7bad\u77e2"; default -> title;
      };
      if (title.equals("Pickaxe Tier List")) return switch (base) {
         case "es" -> "Niveles de pico"; case "pt" -> "N\u00edveis de picareta";
         case "fr" -> "Niveaux de pioche"; case "it" -> "Livelli dei picconi";
         case "ru" -> "\u0423\u0440\u043e\u0432\u043d\u0438 \u043a\u0438\u0440\u043e\u043a"; case "zh" -> "\u9550\u7684\u7b49\u7ea7"; default -> title;
      };
      if (title.equals("Attributed Subspace Gear")) return switch (base) {
         case "es" -> "Equipo de subespacio atribuido"; case "pt" -> "Equipamento de subspace atribu\u00eddo";
         case "fr" -> "\u00c9quipement de subspace attribu\u00e9"; case "it" -> "Equipaggiamento subspace attribuito";
         case "ru" -> "\u0410\u0442\u0440\u0438\u0431\u0443\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0435 \u0441\u043d\u0430\u0440\u044f\u0436\u0435\u043d\u0438\u0435 \u0441\u0430\u0431\u0441\u043f\u0435\u0439\u0441\u0430"; case "zh" -> "\u9644\u6548\u6b21\u5143\u88c5\u5907"; default -> title;
      };
      if (title.equals("Painite Fury")) return switch (base) {
         case "es" -> "Furia de painita"; case "pt" -> "F\u00faria da painite";
         case "fr" -> "Furie de painite"; case "it" -> "Furia della painite";
         case "ru" -> "\u042f\u0440\u043e\u0441\u0442\u044c \u043f\u0430\u0439\u043d\u0438\u0442\u0430"; case "zh" -> "\u75db\u77f3\u72c2\u6012"; default -> title;
      };
      if (title.equals("Chromite Piston")) return switch (base) {
         case "es" -> "Pist\u00f3n de cromita"; case "pt" -> "Pist\u00e3o de cromita"; case "fr" -> "Piston en chromite";
         case "it" -> "Pistone in cromite"; case "ru" -> "\u041f\u043e\u0440\u0448\u0435\u043d\u044c \u0438\u0437 \u0445\u0440\u043e\u043c\u0438\u0442\u0430"; case "zh" -> "\u94ec\u94c1\u77ff\u6d3b\u585e"; default -> title;
      };
      if (title.equals("Wireless Redstone Relay")) return switch (base) {
         case "es" -> "Rel\u00e9 de redstone inal\u00e1mbrico"; case "pt" -> "Rel\u00e9 de redstone sem fio"; case "fr" -> "Relais de redstone sans fil";
         case "it" -> "Rel\u00e8 di redstone senza fili"; case "ru" -> "\u0411\u0435\u0441\u043f\u0440\u043e\u0432\u043e\u0434\u043d\u043e\u0435 \u0440\u0435\u043b\u0435 \u0440\u0435\u0434\u0441\u0442\u043e\u0443\u043d\u0430"; case "zh" -> "\u65e0\u7ebf\u7ea2\u77f3\u7ee7\u7535\u5668"; default -> title;
      };
      if (title.equals("Redstone Clock")) return switch (base) {
         case "es" -> "Reloj de redstone"; case "pt" -> "Rel\u00f3gio de redstone"; case "fr" -> "Horloge de redstone";
         case "it" -> "Orologio di redstone"; case "ru" -> "\u0427\u0430\u0441\u044b \u0440\u0435\u0434\u0441\u0442\u043e\u0443\u043d\u0430"; case "zh" -> "\u7ea2\u77f3\u65f6\u949f"; default -> title;
      };
      if (title.equals("AND Gate")) return switch (base) {
         case "es" -> "Puerta AND"; case "pt" -> "Porta AND"; case "fr" -> "Porte ET"; case "it" -> "Porta AND";
         case "ru" -> "\u042d\u043b\u0435\u043c\u0435\u043d\u0442 \u0418"; case "zh" -> "\u4e0e\u95e8"; default -> title;
      };
      if (title.equals("NAND Gate")) return switch (base) {
         case "es" -> "Puerta NAND"; case "pt" -> "Porta NAND"; case "fr" -> "Porte NON-ET"; case "it" -> "Porta NAND";
         case "ru" -> "\u042d\u043b\u0435\u043c\u0435\u043d\u0442 \u0418-\u041d\u0415"; case "zh" -> "\u4e0e\u975e\u95e8"; default -> title;
      };
      if (title.equals("OR Gate")) return switch (base) {
         case "es" -> "Puerta OR"; case "pt" -> "Porta OR"; case "fr" -> "Porte OU"; case "it" -> "Porta OR";
         case "ru" -> "\u042d\u043b\u0435\u043c\u0435\u043d\u0442 \u0418\u041b\u0418"; case "zh" -> "\u6216\u95e8"; default -> title;
      };
      if (title.equals("NOR Gate")) return switch (base) {
         case "es" -> "Puerta NOR"; case "pt" -> "Porta NOR"; case "fr" -> "Porte NON-OU"; case "it" -> "Porta NOR";
         case "ru" -> "\u042d\u043b\u0435\u043c\u0435\u043d\u0442 \u0418\u041b\u0418-\u041d\u0415"; case "zh" -> "\u6216\u975e\u95e8"; default -> title;
      };
      if (title.equals("XOR Gate")) return switch (base) {
         case "es" -> "Puerta XOR"; case "pt" -> "Porta XOR"; case "fr" -> "Porte OU exclusif"; case "it" -> "Porta XOR";
         case "ru" -> "\u042d\u043b\u0435\u043c\u0435\u043d\u0442 \u0438\u0441\u043a\u043b\u044e\u0447\u0430\u044e\u0449\u0435\u0435 \u0418\u041b\u0418"; case "zh" -> "\u5f02\u6216\u95e8"; default -> title;
      };
      if (title.equals("NOT Gate")) return switch (base) {
         case "es" -> "Puerta NOT"; case "pt" -> "Porta NOT"; case "fr" -> "Inverseur"; case "it" -> "Porta NOT";
         case "ru" -> "\u0418\u043d\u0432\u0435\u0440\u0442\u043e\u0440 \u041d\u0415"; case "zh" -> "\u975e\u95e8"; default -> title;
      };
      if (title.equals("Argentite Filter")) return switch (base) {
         case "es" -> "Filtro de argentita"; case "pt" -> "Filtro de argentita"; case "fr" -> "Filtre en argentite";
         case "it" -> "Filtro in argentite"; case "ru" -> "\u0424\u0438\u043b\u044c\u0442\u0440 \u0438\u0437 \u0430\u0440\u0433\u0435\u043d\u0442\u0438\u0442\u0430"; case "zh" -> "\u8f89\u94f6\u77ff\u8fc7\u6ee4\u5668"; default -> title;
      };
      if (title.equals("Fulgurite Electric Trap")) return switch (base) {
         case "es" -> "Trampa el\u00e9ctrica de fulgurita"; case "pt" -> "Armadilha el\u00e9trica de fulgurita"; case "fr" -> "Pi\u00e8ge \u00e9lectrique en fulgurite";
         case "it" -> "Trappola elettrica in fulgurite"; case "ru" -> "\u042d\u043b\u0435\u043a\u0442\u0440\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u043b\u043e\u0432\u0443\u0448\u043a\u0430 \u0438\u0437 \u0444\u0443\u043b\u044c\u0433\u0443\u0440\u0438\u0442\u0430"; case "zh" -> "\u95ea\u7535\u77f3\u7535\u51fb\u9677\u9631"; default -> title;
      };
      if (title.equals("Catalytic Vial")) return switch (base) {
         case "es" -> "Vial catal\u00edtico"; case "pt" -> "Frasco catal\u00edtico"; case "fr" -> "Fiole catalytique";
         case "it" -> "Fiala catalitica"; case "ru" -> "\u041a\u0430\u0442\u0430\u043b\u0438\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u043e\u0441\u0443\u0434"; case "zh" -> "\u50ac\u5316\u836f\u5242\u74f6"; default -> title;
      };
      if (title.equals("Arcanite XP Jar")) return switch (base) {
         case "es" -> "Frasco de experiencia de arcanita"; case "pt" -> "Jarro de XP de arcanita"; case "fr" -> "Bocal d\u2019XP en arcanite";
         case "it" -> "Barattolo XP di arcanite"; case "ru" -> "\u0411\u0430\u043d\u043a\u0430 \u043e\u043f\u044b\u0442\u0430 \u0438\u0437 \u0430\u0440\u043a\u0430\u043d\u0438\u0442\u0430"; case "zh" -> "\u5965\u672f\u77ff\u7ecf\u9a8c\u7f50"; default -> title;
      };
      if (title.equals("Nyxiumnite Staff")) return switch (base) {
         case "es" -> "Bast\u00f3n de nyxiumnita"; case "pt" -> "Cajado de nyxiumnita"; case "fr" -> "B\u00e2ton de nyxiumnite";
         case "it" -> "Bastone di nyxiumnite"; case "ru" -> "\u041f\u043e\u0441\u043e\u0445 \u043d\u0438\u043a\u0441\u0438\u0443\u043c\u043d\u0438\u0442\u0430"; case "zh" -> "\u6b21\u5143\u954d\u6676\u6cd5\u6756"; default -> title;
      };
      if (title.equals("Meteor Staff")) return switch (base) {
         case "es" -> "Bast\u00f3n de meteorito"; case "pt" -> "Cajado de meteorito"; case "fr" -> "B\u00e2ton m\u00e9t\u00e9orique";
         case "it" -> "Bastone meteorico"; case "ru" -> "\u041c\u0435\u0442\u0435\u043e\u0440\u0438\u0442\u043d\u044b\u0439 \u043f\u043e\u0441\u043e\u0445"; case "zh" -> "\u9668\u77f3\u6cd5\u6756"; default -> title;
      };
      if (title.equals("Nyxium-Arcanite Core")) return switch (base) {
         case "es" -> "N\u00facleo de nyxium y arcanita"; case "pt" -> "N\u00facleo de Nyxium-Arcanita"; case "fr" -> "Noyau nyxium-arcanite";
         case "it" -> "Nucleo Nyxium-Arcanite"; case "ru" -> "\u042f\u0434\u0440\u043e \u043d\u0438\u043a\u0441\u0438\u0443\u043c-\u0430\u0440\u043a\u0430\u043d\u0438\u0442"; case "zh" -> "\u6b21\u5143\u954d\u6676\u5965\u672f\u6838\u5fc3"; default -> title;
      };
      if (title.equals("Volatile Nyxiumnite Core")) return switch (base) {
         case "es" -> "N\u00facleo de nyxiumnita vol\u00e1til"; case "pt" -> "N\u00facleo de nyxiumnita vol\u00e1til"; case "fr" -> "Noyau de nyxiumnite volatile";
         case "it" -> "Nucleo di nyxiumnite volatile"; case "ru" -> "\u041d\u0435\u0441\u0442\u0430\u0431\u0438\u043b\u044c\u043d\u043e\u0435 \u044f\u0434\u0440\u043e \u043d\u0438\u043a\u0441\u0438\u0443\u043c\u043d\u0438\u0442\u0430"; case "zh" -> "\u4e0d\u7a33\u5b9a\u6b21\u5143\u954d\u6676\u6838\u5fc3"; default -> title;
      };
      if (title.equals("Meteorite Core Shard")) return switch (base) {
         case "es" -> "Fragmento del n\u00facleo de meteorito"; case "pt" -> "Fragmento do n\u00facleo de meteorito"; case "fr" -> "\u00c9clat de noyau de m\u00e9t\u00e9orite";
         case "it" -> "Scheggia del nucleo di meteorite"; case "ru" -> "\u041e\u0441\u043a\u043e\u043b\u043e\u043a \u044f\u0434\u0440\u0430 \u043c\u0435\u0442\u0435\u043e\u0440\u0438\u0442\u0430"; case "zh" -> "\u9668\u77f3\u6838\u5fc3\u788e\u7247"; default -> title;
      };
      if (title.equals("Volatile Meteorite Core")) return switch (base) {
         case "es" -> "N\u00facleo de meteorito vol\u00e1til"; case "pt" -> "N\u00facleo de meteorito vol\u00e1til"; case "fr" -> "Noyau de m\u00e9t\u00e9orite volatile";
         case "it" -> "Nucleo di meteorite volatile"; case "ru" -> "\u041d\u0435\u0441\u0442\u0430\u0431\u0438\u043b\u044c\u043d\u043e\u0435 \u044f\u0434\u0440\u043e \u043c\u0435\u0442\u0435\u043e\u0440\u0438\u0442\u0430"; case "zh" -> "\u4e0d\u7a33\u5b9a\u9668\u77f3\u6838\u5fc3"; default -> title;
      };
      if (title.equals("Silver Core Arcanite Shard")) return switch (base) {
         case "es" -> "Fragmento de arcanita con n\u00facleo de plata"; case "pt" -> "Fragmento de arcanita com n\u00facleo de prata"; case "fr" -> "\u00c9clat d\u2019arcanite \u00e0 noyau d\u2019argent";
         case "it" -> "Scheggia di arcanite con nucleo d\u2019argento"; case "ru" -> "\u041e\u0441\u043a\u043e\u043b\u043e\u043a \u0430\u0440\u043a\u0430\u043d\u0438\u0442\u0430 \u0441 \u0441\u0435\u0440\u0435\u0431\u0440\u044f\u043d\u044b\u043c \u044f\u0434\u0440\u043e\u043c"; case "zh" -> "\u94f6\u82af\u5965\u672f\u788e\u7247"; default -> title;
      };
      if (title.equals("Dark Core")) return switch (base) {
         case "es" -> "N\u00facleo oscuro"; case "pt" -> "N\u00facleo sombrio"; case "fr" -> "Noyau sombre";
         case "it" -> "Nucleo oscuro"; case "ru" -> "\u0422\u0451\u043c\u043d\u043e\u0435 \u044f\u0434\u0440\u043e"; case "zh" -> "\u6697\u5f71\u6838\u5fc3"; default -> title;
      };
      if (title.endsWith(" Armor Set")) {
         String material = title.substring(0, title.length() - " Armor Set".length());
         String translated = localizedMaterialTitleName(material, base);
         return switch (base) {
            case "es" -> "Armadura de " + translated;
            case "pt" -> "Conjunto de armadura de " + translated;
            case "fr" -> "Armure en " + translated;
            case "it" -> "Armatura di " + translated;
            case "ru" -> "\u041a\u043e\u043c\u043f\u043b\u0435\u043a\u0442 \u0431\u0440\u043e\u043d\u0438: " + translated;
            case "zh" -> translated + "\u76d4\u7532\u5957\u88c5";
            default -> title;
         };
      }
      if (title.endsWith(" Weapons & Tools")) {
         String material = title.substring(0, title.length() - " Weapons & Tools".length());
         String translated = localizedMaterialTitleName(material, base);
         return switch (base) {
            case "es" -> "Armas y herramientas de " + translated;
            case "pt" -> "Armas e ferramentas de " + translated;
            case "fr" -> "Armes et outils en " + translated;
            case "it" -> "Armi e strumenti di " + translated;
            case "ru" -> "\u041e\u0440\u0443\u0436\u0438\u0435 \u0438 \u0438\u043d\u0441\u0442\u0440\u0443\u043c\u0435\u043d\u0442\u044b: " + translated;
            case "zh" -> translated + "\u6b66\u5668\u4e0e\u5de5\u5177";
            default -> title;
         };
      }
      if (title.equals("Farseeker Crystal")) return switch (base) { case "es" -> "Cristal buscador"; case "pt" -> "Cristal Farseeker"; case "fr" -> "Cristal Farseeker"; case "it" -> "Cristallo Farseeker"; case "ru" -> "\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b \u0438\u0441\u043a\u0430\u0442\u0435\u043b\u044f"; case "zh" -> "\u5bfb\u8ff9\u6c34\u6676"; default -> title; };
      if (title.equals("Extruding Crystal Ore")) return switch (base) { case "es" -> "Mineral de cristal extrusor"; case "pt" -> "Min\u00e9rio de Cristal Extrusor"; case "fr" -> "Minerai de cristal extrud\u00e9"; case "it" -> "Minerale di cristallo estruso"; case "ru" -> "\u0420\u0443\u0434\u0430 \u044d\u043a\u0441\u0442\u0440\u0443\u0434\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u0430"; case "zh" -> "\u6324\u51fa\u6c34\u6676\u77ff\u77f3"; default -> title; };
      if (title.equals("Extruding Crystal Nugget")) return switch (base) { case "es" -> "Pepita de cristal extrusor"; case "pt" -> "Pepita de Cristal Extrusor"; case "fr" -> "P\u00e9pite de cristal extrud\u00e9"; case "it" -> "Pepita di cristallo estruso"; case "ru" -> "\u0421\u0430\u043c\u043e\u0440\u043e\u0434\u043e\u043a \u044d\u043a\u0441\u0442\u0440\u0443\u0434\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u0430"; case "zh" -> "\u6324\u51fa\u6c34\u6676\u7c92"; default -> title; };
      if (title.equals("Extruding Crystal")) return switch (base) { case "es" -> "Cristal extrusor"; case "pt" -> "Cristal Extrusor"; case "fr" -> "Cristal extrud\u00e9"; case "it" -> "Cristallo estruso"; case "ru" -> "\u042d\u043a\u0441\u0442\u0440\u0443\u0434\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439 \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b"; case "zh" -> "\u6324\u51fa\u6c34\u6676"; default -> title; };
      if (title.equals("Extruding Nether Star")) return switch (base) { case "es" -> "Estrella del Nether extrusora"; case "pt" -> "Estrela do Nether Extrusora"; case "fr" -> "\u00c9toile du Nether extrud\u00e9e"; case "it" -> "Stella del Nether estrusa"; case "ru" -> "\u042d\u043a\u0441\u0442\u0440\u0443\u0434\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u0430\u044f \u0437\u0432\u0435\u0437\u0434\u0430 \u041d\u0435\u0437\u0435\u0440\u0430"; case "zh" -> "\u6324\u51fa\u4e0b\u754c\u4e4b\u661f"; default -> title; };
      if (title.equals("Heated Shard")) return switch (base) { case "es" -> "Fragmento calentado"; case "pt" -> "Fragmento Aquecido"; case "fr" -> "\u00c9clat chauff\u00e9"; case "it" -> "Scheggia riscaldata"; case "ru" -> "\u041d\u0430\u0433\u0440\u0435\u0442\u044b\u0439 \u043e\u0441\u043a\u043e\u043b\u043e\u043a"; case "zh" -> "\u70ed\u80fd\u788e\u7247"; default -> title; };
      if (title.equals("Meteor Bomb")) return switch (base) { case "es" -> "Bomba de meteorito"; case "pt" -> "Bomba de Meteorito"; case "fr" -> "Bombe m\u00e9t\u00e9oritique"; case "it" -> "Bomba meteorica"; case "ru" -> "\u041c\u0435\u0442\u0435\u043e\u0440\u0438\u0442\u043d\u0430\u044f \u0431\u043e\u043c\u0431\u0430"; case "zh" -> "\u9668\u77f3\u70b8\u5f39"; default -> title; };
      if (title.equals("Phosgene Potion Powders")) return switch (base) { case "es" -> "Polvos de poci\u00f3n de fosgeno"; case "pt" -> "P\u00f3s de Po\u00e7\u00e3o de Fosg\u00eanio"; case "fr" -> "Poudres de potion au phosg\u00e8ne"; case "it" -> "Polveri per pozioni al fosgene"; case "ru" -> "\u041f\u043e\u0440\u043e\u0448\u043a\u0438 \u0444\u043e\u0441\u0433\u0435\u043d\u043e\u0432\u043e\u0433\u043e \u0437\u0435\u043b\u044c\u044f"; case "zh" -> "\u5149\u6c14\u836f\u6c34\u7c89\u672b"; default -> title; };
      if (title.equals("Lonsdaleite Layer Glue")) return switch (base) { case "es" -> "Pegamento de capa de lonsdale\u00edta"; case "pt" -> "Cola de Camada de Lonsdale\u00edta"; case "fr" -> "Colle \u00e0 couche de lonsdal\u00e9ite"; case "it" -> "Colla per strato di lonsdaleite"; case "ru" -> "\u041a\u043b\u0435\u0439 \u0434\u043b\u044f \u0441\u043b\u043e\u044f \u043b\u043e\u043d\u0441\u0434\u0435\u0439\u043b\u0438\u0442\u0430"; case "zh" -> "\u6717\u65af\u4ee3\u5c14\u77f3\u6d82\u5c42\u80f6"; default -> title; };
      if (title.equals("Ancient Pedestal")) return switch (base) { case "es" -> "Pedestal antiguo"; case "pt" -> "Pedestal Antigo"; case "fr" -> "Pi\u00e9destal ancien"; case "it" -> "Piedistallo antico"; case "ru" -> "\u0414\u0440\u0435\u0432\u043d\u0438\u0439 \u043f\u044c\u0435\u0434\u0435\u0441\u0442\u0430\u043b"; case "zh" -> "\u8fdc\u53e4\u57fa\u5ea7"; default -> title; };
      if (title.equals("Titanium Rail")) return switch (base) { case "es" -> "Ra\u00edl de titanio"; case "pt" -> "Trilho de Tit\u00e2nio"; case "fr" -> "Rail en titane"; case "it" -> "Binario in titanio"; case "ru" -> "\u0422\u0438\u0442\u0430\u043d\u043e\u0432\u044b\u0435 \u0440\u0435\u043b\u044c\u0441\u044b"; case "zh" -> "\u949b\u94c1\u8f68"; default -> title; };
      if (title.equals("Titanium Controller Rail")) return switch (base) { case "es" -> "Ra\u00edl controlador de titanio"; case "pt" -> "Trilho Controlador de Tit\u00e2nio"; case "fr" -> "Rail contr\u00f4leur en titane"; case "it" -> "Binario controllore in titanio"; case "ru" -> "\u0422\u0438\u0442\u0430\u043d\u043e\u0432\u044b\u0435 \u0440\u0435\u043b\u044c\u0441\u044b-\u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0430\u0442\u0435\u043b\u0438"; case "zh" -> "\u949b\u63a7\u5236\u94c1\u8f68"; default -> title; };
      if (title.equals("Enderium Rail")) return switch (base) { case "es" -> "Ra\u00edl de enderio"; case "pt" -> "Trilho de Enderium"; case "fr" -> "Rail en enderium"; case "it" -> "Binario di enderium"; case "ru" -> "\u0420\u0435\u043b\u044c\u0441\u044b \u0438\u0437 \u044d\u043d\u0434\u0435\u0440\u0438\u0443\u043c\u0430"; case "zh" -> "\u672b\u5f71\u94c1\u8f68"; default -> title; };
      if (title.equals("Solarite Rail")) return switch (base) { case "es" -> "Ra\u00edl de solarita"; case "pt" -> "Trilho de Solarite"; case "fr" -> "Rail en solarite"; case "it" -> "Binario di Solarite"; case "ru" -> "\u0420\u0435\u043b\u044c\u0441\u044b Solarite"; case "zh" -> "\u65e5\u8000\u77ff\u94c1\u8f68"; default -> title; };
      if (title.equals("Solarite Furnace")) return switch (base) { case "es" -> "Horno de solarita"; case "pt" -> "Fornalha de Solarite"; case "fr" -> "Fourneau de solarite"; case "it" -> "Fornace di Solarite"; case "ru" -> "\u041f\u0435\u0447\u044c Solarite"; case "zh" -> "\u65e5\u8000\u77ff\u7194\u7089"; default -> title; };
      if (title.equals("Solar Battery")) return switch (base) { case "es" -> "Bater\u00eda solarita"; case "pt" -> "Bateria de Solarite"; case "fr" -> "Batterie de solarite"; case "it" -> "Batteria di Solarite"; case "ru" -> "\u0411\u0430\u0442\u0430\u0440\u0435\u044f Solarite"; case "zh" -> "\u65e5\u8000\u77ff\u7535\u6c60"; default -> title; };
      if (title.equals("Solarite Battery Minecart")) return switch (base) { case "es" -> "Vagoneta con bater\u00eda de solarita"; case "pt" -> "Vag\u00e3o com Bateria de Solarite"; case "fr" -> "Wagonnet \u00e0 batterie de solarite"; case "it" -> "Carrello con batteria di Solarite"; case "ru" -> "\u0412\u0430\u0433\u043e\u043d\u0435\u0442\u043a\u0430 \u0441 \u0431\u0430\u0442\u0430\u0440\u0435\u0435\u0439 Solarite"; case "zh" -> "\u65e5\u8000\u77ff\u7535\u6c60\u77ff\u8f66"; default -> title; };
      if (title.equals("Super Beacon")) return switch (base) { case "es" -> "Superbaliza"; case "pt" -> "Super Sinalizador"; case "fr" -> "Super-balise"; case "it" -> "Super Faro"; case "ru" -> "\u0421\u0443\u043f\u0435\u0440-\u043c\u0430\u044f\u043a"; case "zh" -> "\u8d85\u7ea7\u4fe1\u6807"; default -> title; };
      if (title.equals("Nyxium Dark Barrier")) return switch (base) { case "es" -> "Barrera oscura de nyxium"; case "pt" -> "Barreira Sombria de Nyxium"; case "fr" -> "Barri\u00e8re sombre en Nyxium"; case "it" -> "Barriera oscura di Nyxium"; case "ru" -> "\u0422\u0451\u043c\u043d\u044b\u0439 \u0431\u0430\u0440\u044c\u0435\u0440 \u0438\u0437 Nyxium"; case "zh" -> "Nyxium \u6697\u80fd\u5c4f\u969c"; default -> title; };
      if (title.equals("Scheelite Chisel")) return switch (base) { case "es" -> "Cincel de scheelita"; case "pt" -> "Cinzel de Scheelita"; case "fr" -> "Ciseau en scheelite"; case "it" -> "Scalpello di scheelite"; case "ru" -> "\u0428\u0435\u0435\u043b\u0438\u0442\u043e\u0432\u044b\u0439 \u0440\u0435\u0437\u0435\u0446"; case "zh" -> "\u767d\u94a8\u77ff\u51ff\u5b50"; default -> title; };
      if (title.equals("Titanium Lock")) return switch (base) { case "es" -> "Cerradura de titanio"; case "pt" -> "Fechadura de Tit\u00e2nio"; case "fr" -> "Serrure en titane"; case "it" -> "Serratura in titanio"; case "ru" -> "\u0422\u0438\u0442\u0430\u043d\u043e\u0432\u044b\u0439 \u0437\u0430\u043c\u043e\u043a"; case "zh" -> "\u949b\u9501"; default -> title; };
      if (title.equals("Titanium Key")) return switch (base) { case "es" -> "Llave de titanio"; case "pt" -> "Chave de Tit\u00e2nio"; case "fr" -> "Cl\u00e9 en titane"; case "it" -> "Chiave di titanio"; case "ru" -> "\u0422\u0438\u0442\u0430\u043d\u043e\u0432\u044b\u0439 \u043a\u043b\u044e\u0447"; case "zh" -> "\u949b\u94a5\u5319"; default -> title; };
      if (title.equals("Crashed Meteorite")) return switch (base) { case "es" -> "Meteorito estrellado"; case "pt" -> "Meteorito Ca\u00eddo"; case "fr" -> "M\u00e9t\u00e9orite \u00e9cras\u00e9e"; case "it" -> "Meteorite precipitato"; case "ru" -> "\u0423\u043f\u0430\u0432\u0448\u0438\u0439 \u043c\u0435\u0442\u0435\u043e\u0440\u0438\u0442"; case "zh" -> "\u5760\u843d\u9668\u77f3"; default -> title; };
      if (title.equals("Ancient Pedestal Chamber")) return switch (base) { case "es" -> "C\u00e1mara del pedestal antiguo"; case "pt" -> "C\u00e2mara do Pedestal Antigo"; case "fr" -> "Chambre du pi\u00e9destal ancien"; case "it" -> "Camera del Piedistallo Antico"; case "ru" -> "\u041a\u0430\u043c\u0435\u0440\u0430 \u0434\u0440\u0435\u0432\u043d\u0435\u0433\u043e \u043f\u044c\u0435\u0434\u0435\u0441\u0442\u0430\u043b\u0430"; case "zh" -> "\u8fdc\u53e4\u57fa\u5ea7\u5bc6\u5ba4"; default -> title; };
      if (title.equals("Torches")) return switch (base) { case "es" -> "Antorchas"; case "pt" -> "Tochas"; case "fr" -> "Torches"; case "it" -> "Torce"; case "ru" -> "\u0424\u0430\u043a\u0435\u043b\u044b"; case "zh" -> "\u706b\u628a"; default -> title; };
      if (title.equals("Lanterns")) return switch (base) { case "es" -> "Faroles"; case "pt" -> "Lanternas"; case "fr" -> "Lanternes"; case "it" -> "Lanterne"; case "ru" -> "\u0424\u043e\u043d\u0430\u0440\u0438"; case "zh" -> "\u706f\u7b3c"; default -> title; };
      if (title.equals("Campfires")) return switch (base) { case "es" -> "Fogatas"; case "pt" -> "Fogueiras"; case "fr" -> "Feux de camp"; case "it" -> "Fuochi da campo"; case "ru" -> "\u041a\u043e\u0441\u0442\u0440\u044b"; case "zh" -> "\u8425\u706b"; default -> title; };
      return title;
   }
}
