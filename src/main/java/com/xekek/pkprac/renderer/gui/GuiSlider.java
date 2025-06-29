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
    private String labelText;
    private java.util.function.Supplier<String> displayStringSupplier;
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    public GuiSlider(int id, int x, int y, int width, int height, float min, float max, String text, float current) {
        super(id, x, y, width, height, "");
        this.minValue = min;
        this.maxValue = max;
        this.labelText = text;
        this.sliderPosition = (current - min) / (max - min);
        this.value = (int) current;
        this.displayStringSupplier = () -> String.format("%s %d", this.labelText, this.value);
        this.displayString = getDisplayString();
    }

    public void setDisplayStringSupplier(java.util.function.Supplier<String> supplier) {
        this.displayStringSupplier = supplier;
        this.displayString = getDisplayString();
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

        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        
        mc.getTextureManager().bindTexture(WIDGETS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46, this.width / 2, this.height);
        this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46, this.width / 2, this.height);
        
        this.mouseDragged(mc, mouseX, mouseY);
        
        int knobX = this.xPosition + (int)(this.sliderPosition * (float)(this.width - 8));
        this.drawTexturedModalRect(knobX, this.yPosition, 0, 66, 4, 20);
        this.drawTexturedModalRect(knobX + 4, this.yPosition, 196, 66, 4, 20);

        int textColor = 14737632;
        if (!this.enabled) {
            textColor = 10526880;
        } else if (this.hovered || this.dragging) {
            textColor = 16777120;
        }
        
        this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderPosition = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            if (this.sliderPosition < 0.0F) {
                this.sliderPosition = 0.0F;
            }
            if (this.sliderPosition > 1.0F) {
                this.sliderPosition = 1.0F;
            }
            this.value = (int) (minValue + sliderPosition * (maxValue - minValue));
            this.displayString = getDisplayString();
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            this.sliderPosition = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            if (this.sliderPosition < 0.0F) {
                this.sliderPosition = 0.0F;
            }
            if (this.sliderPosition > 1.0F) {
                this.sliderPosition = 1.0F;
            }
            this.value = (int) (minValue + sliderPosition * (maxValue - minValue));
            this.displayString = getDisplayString();
        }
    }
}