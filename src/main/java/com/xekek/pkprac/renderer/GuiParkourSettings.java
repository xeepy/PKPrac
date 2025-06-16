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
package com.xekek.pkprac.renderer;

import com.xekek.pkprac.Main;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;


public class GuiParkourSettings extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton openCheckpointEditorButton;
    private GuiButton toggleCheckpointButton;
    private GuiButton gifScaleDownButton;
    private GuiButton gifScaleUpButton;
    private GuiButton gifScaleText;
    private GuiButton toggleBeamsButton;
    private GuiButton doneButton;
    private float gifScale = 1.0f;

    private static final ResourceLocation LOGO_TEXTURE = new ResourceLocation("parkourmod", "practhumby.png");

    public GuiParkourSettings(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        this.buttonList.clear();
        openCheckpointEditorButton = new GuiButton(0, centerX - 100, centerY - 44, 200, 20, "Open Checkpoint Editor");
        toggleBeamsButton = new GuiButton(6, centerX - 100, centerY - 22, 200, 20, getToggleBeamsText());
        toggleCheckpointButton = new GuiButton(1, centerX - 100, centerY, 200, 20, getCheckpointButtonText());
        gifScaleText = new GuiButton(5, centerX - 56, centerY + 28, 116, 20, getGifScaleText());
        gifScale = Main.getGifScale();
        gifScaleDownButton = new GuiButton(3, centerX - 100, centerY + 28, 40, 20, "-");
        gifScaleUpButton = new GuiButton(4, centerX + 60, centerY + 28, 40, 20, "+");
        doneButton = new GuiButton(2, centerX - 100, centerY + 54, 200, 20, "Done");

        this.buttonList.add(openCheckpointEditorButton);
        this.buttonList.add(toggleBeamsButton);
        this.buttonList.add(toggleCheckpointButton);
        this.buttonList.add(gifScaleDownButton);
        this.buttonList.add(gifScaleText);
        this.buttonList.add(gifScaleUpButton);
        this.buttonList.add(doneButton);
    }


    private String getToggleBeamsText() {
        boolean enabled = ParkourSettings.toggleBeams;
        return "Toggle Beams: " + (enabled ? "ON" : "OFF");
    }
    private String getCheckpointButtonText() {
        boolean enabled = ParkourSettings.saveCheckpointOnActivation;
        return "Auto Checkpoint: " + (enabled ? "ON" : "OFF");
    }

    private String getGifScaleText() {
        return "GIF Scale: " + Math.round(gifScale * 100) + "%";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiCheckpointEditor(this));
        } else if (button.id == 1) {
            ParkourSettings.toggleSaveCheckpointOnActivation();
            toggleCheckpointButton.displayString = getCheckpointButtonText();
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 3) {
            gifScale = Math.max(0.1f, gifScale - 0.1f);
            Main.setGifScale(gifScale);
            this.initGui();
        } else if (button.id == 4) {
            gifScale = Math.min(1.5f, gifScale + 0.1f);
            Main.setGifScale(gifScale);
            this.initGui();
        } else if (button.id == 6) {
            ParkourSettings.toggleBeams = !ParkourSettings.toggleBeams;
            toggleBeamsButton.displayString = getToggleBeamsText();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        float logoScale = 0.25f;
        int logoWidth = (int) (this.width * logoScale);
        int logoHeight = (int) (logoWidth * 360f / 600f);

        int centerX = this.width / 2;
        int firstButtonY = this.height / 2 - 44;
        int titleY = firstButtonY - 28;
        int logoY = titleY - logoHeight - 3;
        int logoX = centerX - logoWidth / 2;

        mc.getTextureManager().bindTexture(LOGO_TEXTURE);
        GlStateManager.enableBlend();
        drawModalRectWithCustomSizedTexture(logoX, logoY, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);
        GlStateManager.disableBlend();

        drawCenteredString(this.fontRendererObj, "Parkour Mod Settings", centerX, titleY, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}