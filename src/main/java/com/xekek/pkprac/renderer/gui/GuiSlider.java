/*
 * PKPrac - A parkour practice mod
 * Copyright (C) 2025 xeepy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.xekek.pkprac.renderer.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiSlider extends GuiButton {
    public float sliderPosition;
    public boolean dragging;
    private float minValue;
    private float maxValue;
    public int value;
    private java.util.function.Supplier<String> displayStringSupplier;
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    public GuiSlider(int id, int x, int y, int width, int height, float min, float max, float current) {
        super(id, x, y, width, height, "");
        this.minValue = min;
        this.maxValue = max;
        this.sliderPosition = (current - min) / (max - min);
        this.value = (int) current;
        this.displayStringSupplier = () -> String.format("Max Checkpoints: %d", this.value);
    }

    public void setDisplayStringSupplier(java.util.function.Supplier<String> supplier) {
        this.displayStringSupplier = supplier;
    }

    private String getDisplayString() {
        if (displayStringSupplier != null) {
            return displayStringSupplier.get();
        }
        return String.format("Value: %d", this.value);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;
        mc.getTextureManager().bindTexture(WIDGETS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int barY = yPosition + (height - 20) / 2;
        int barX = xPosition;
        int barWidth = width;
        int barHeight = 20;
        drawTexturedModalRect(barX, barY, 0, 46, barWidth / 2, barHeight);
        drawTexturedModalRect(barX + barWidth / 2, barY, 200 - barWidth / 2, 46, barWidth / 2, barHeight);
        int knobX = xPosition + (int)(this.sliderPosition * (width - 8));
        int knobY = barY;
        int knobTexY = 66;
        boolean hovered = mouseX >= knobX && mouseY >= knobY && mouseX < knobX + 8 && mouseY < knobY + 20;
        int knobTexX = hovered ? 0 : 0;
        drawTexturedModalRect(knobX, knobY, 0, knobTexY, 4, 20);
        drawTexturedModalRect(knobX + 4, knobY, 196, knobTexY, 4, 20);
        int textY = yPosition + (height - 8) / 2;
        this.displayString = getDisplayString();
        this.drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, textY, 0xFFFFFF);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            setValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            setValueFromMouse(mouseX);
        }
    }

    private void setValueFromMouse(int mouseX) {
        float percent = (float)(mouseX - (xPosition + 4)) / (float)(width - 8);
        percent = Math.max(0.0F, Math.min(1.0F, percent));
        this.sliderPosition = percent;
        value = (int) (minValue + sliderPosition * (maxValue - minValue));
        updateDisplayString();
    }

    private void updateDisplayString() {
        this.displayString = getDisplayString();
    }
}