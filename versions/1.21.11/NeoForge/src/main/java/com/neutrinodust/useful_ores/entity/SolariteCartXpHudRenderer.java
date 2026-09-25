package com.neutrinodust.useful_ores.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public class SolariteCartXpHudRenderer {

    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;

    private static final int BAR_GAP = 3;

    private static final int BG_COLOR = 0xFF1E1E1E;
    private static final int BORDER_COLOR = 0xFFFFFFFF;
    private static final int FILL_COLOR_NORMAL = 0xFF2ED8FF;
    private static final int FILL_COLOR_SOLAR = 0xFFFFD23F;
    private static final int FILL_COLOR_GRID = 0xFF6BFF6B;

    private static final int BATTERY_FULL_COLOR = 0xFF6BFF6B;
    private static final int BATTERY_MID_COLOR = 0xFFFFD23F;
    private static final int BATTERY_LOW_COLOR = 0xFFFF4C4C;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (!(player != null && player.getVehicle() instanceof SolariteBatteryMinecartEntity cart)) {
            return;
        }

        GuiGraphics gg = event.getGuiGraphics();
        int screenWidth = gg.guiWidth();
        int screenHeight = gg.guiHeight();

        int x = (screenWidth - BAR_WIDTH) / 2;
        int y = screenHeight - 49;

        boolean solar = cart.isRunningOnFreeSolarPower();
        boolean grid = cart.isRunningOnGridPower();
        boolean battery = cart.isRunningOnBatteryPower();

        float fraction = Math.min(1f,
                (float) (cart.getEffectiveDisplaySpeed() / SolariteBatteryMinecartEntity.SOLARITE_MAX_SPEED));
        int filledWidth = Math.round(BAR_WIDTH * fraction);
        int fillColor = solar ? FILL_COLOR_SOLAR : grid ? FILL_COLOR_GRID : FILL_COLOR_NORMAL;

        gg.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, BORDER_COLOR);
        gg.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BG_COLOR);
        if (filledWidth > 0) {
            gg.fill(x, y, x + filledWidth, y + BAR_HEIGHT, fillColor);
        }

        int speedBs = Math.round((float) cart.getEffectiveDisplaySpeed() * 20f);
        String speedLabel = speedBs + " b/s";
        int speedTextWidth = mc.font.width(speedLabel);
        gg.drawString(mc.font, speedLabel, screenWidth / 2 - speedTextWidth / 2, y - 10, fillColor, true);

        int batteryY = y + BAR_HEIGHT + BAR_GAP;
        float batteryPct = cart.getBatteryLevel();
        int batteryFilled = Math.round(BAR_WIDTH * batteryPct);
        int batteryColor = batteryPct > 0.5f ? BATTERY_FULL_COLOR
                : batteryPct > 0.2f ? BATTERY_MID_COLOR
                : BATTERY_LOW_COLOR;

        gg.fill(x - 1, batteryY - 1, x + BAR_WIDTH + 1, batteryY + BAR_HEIGHT + 1, BORDER_COLOR);
        gg.fill(x, batteryY, x + BAR_WIDTH, batteryY + BAR_HEIGHT, BG_COLOR);
        if (batteryFilled > 0) {
            gg.fill(x, batteryY, x + batteryFilled, batteryY + BAR_HEIGHT, batteryColor);
        }

        String modeLabel = solar ? "Solar" : grid ? "Grid" : battery ? "Battery" : "Battery";
        String batteryLabel = modeLabel + " - " + Math.round(batteryPct * 100f) + "%";
        int batteryTextWidth = mc.font.width(batteryLabel);
        gg.drawString(mc.font, batteryLabel, screenWidth / 2 - batteryTextWidth / 2,
                batteryY + BAR_HEIGHT + 2, batteryColor, true);
    }
}

