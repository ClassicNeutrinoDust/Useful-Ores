package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class SolariteFurnaceScreen extends AbstractContainerScreen<SolariteFurnaceMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/gui/container/solarite_furnace.png");

    public SolariteFurnaceScreen(SolariteFurnaceMenu menu, net.minecraft.world.entity.player.Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

        int arrowX = x + 91;
        int arrowY = y + 34;
        final int arrowW = 23, arrowH = 15;

        int progress = menu.getCookProgressScaled(arrowW);
        if (progress > 0) {
            graphics.enableScissor(arrowX, arrowY, arrowX + progress, arrowY + arrowH);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, arrowX, arrowY, 181.0F, 4.0F, arrowW, arrowH, 256, 256);
            graphics.disableScissor();
        }

        int barX = x + 9;
        int barY = y + 17;
        int barWidth = 16;
        int barHeight = 52;
        int filled;
        float barU;
        int mode = menu.getSourceMode();
        if (mode == SolariteFurnaceBlockEntity.MODE_SUN) {
            filled = barHeight;
            barU = 176.0F;
        } else if (mode == SolariteFurnaceBlockEntity.MODE_BATTERY) {
            int pct = menu.getBatteryEnergyPercent();
            filled = Math.round(barHeight * pct / 100.0F);
            barU = 192.0F;
        } else {
            filled = 0;
            barU = 176.0F;
        }

        if (filled > 0) {
            int clipTop = barY + (barHeight - filled);
            graphics.enableScissor(barX, clipTop, barX + barWidth, barY + barHeight);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, barU, 20.0F, barWidth, barHeight, 256, 256);
            graphics.disableScissor();
        }
    }

    private static final int TEXT_COLOR = 0xFF404040;

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, TEXT_COLOR, false);

        String label;
        int color;
        switch (menu.getSourceMode()) {
            case SolariteFurnaceBlockEntity.MODE_SUN -> {
                label = "Solar Powered";
                color = 0xFFFFD966;
            }
            case SolariteFurnaceBlockEntity.MODE_BATTERY -> {
                label = "Battery Powered";
                color = 0xFF55FF55;
            }
            default -> {
                label = "Not Powered";
                color = 0xFFFF6B6B;
            }
        }
        int statusX = this.imageWidth - this.titleLabelX - this.font.width(label);
        graphics.drawString(this.font, label, statusX, this.inventoryLabelY, color, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, TEXT_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && hasShiftDown()) {
            Slot slot = this.hoveredSlot;
            if (slot != null && slot.hasItem()) {
                this.slotClicked(slot, slot.index, button, ClickType.QUICK_MOVE);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}

