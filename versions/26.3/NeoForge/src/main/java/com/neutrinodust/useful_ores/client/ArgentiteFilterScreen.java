package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.filter.ArgentiteFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ArgentiteFilterScreen extends AbstractContainerScreen<ArgentiteFilterMenu> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("useful_ores", "textures/gui/container/argentite_filter.png");

    public ArgentiteFilterScreen(ArgentiteFilterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 133);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}

