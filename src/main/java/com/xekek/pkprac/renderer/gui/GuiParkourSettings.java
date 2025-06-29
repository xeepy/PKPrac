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

import com.xekek.pkprac.Main;
import com.xekek.pkprac.client.Config;
import com.xekek.pkprac.renderer.ParkourSettings;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;


public class GuiParkourSettings extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton openCheckpointEditorButton;
    private GuiButton toggleCheckpointButton;
    private GuiSlider gifScaleSlider;
    private GuiButton toggleBeamsButton;
    private GuiButton gifStyleButton;
    private GuiButton doneButton;

    private static final ResourceLocation LOGO_TEXTURE = new ResourceLocation("parkourmod", "thumbnail.png");

    private static final String[] gifStyleNames = {"Miku", "Earth", "Nyan", "Spooky", "Winter"};
    private static final String[] gifStylePaths = {
            "parkourmod:Miku.gif",
            "parkourmod:Earth.gif",
            "parkourmod:Nyan.gif",
            "parkourmod:Spooky.gif",
            "parkourmod:Winter.gif"
    };

    public GuiParkourSettings(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2 + 20;
        this.buttonList.clear();
        openCheckpointEditorButton = new GuiButton(0, centerX - 100, centerY - 66, 200, 20, "Open Checkpoint Editor");
        toggleBeamsButton = new GuiButton(6, centerX - 100, centerY - 44, 200, 20, getToggleBeamsText());
        toggleCheckpointButton = new GuiButton(1, centerX - 100, centerY - 22, 200, 20, getCheckpointButtonText());
        gifScaleSlider = new GuiSlider(3, centerX - 100, centerY, 200, 20, 0.1f, 1.5f, "GIF Scale: ", Config.getGifScale());
        gifScaleSlider.setDisplayStringSupplier(() -> "GIF Scale: " + Math.round(gifScaleSlider.sliderPosition * 140 + 10) + "%");
        gifStyleButton = new GuiButton(7, centerX - 100, centerY + 22, 200, 20, getGifStyleText());
        doneButton = new GuiButton(2, centerX - 100, centerY + 44, 200, 20, "Done");

        this.buttonList.add(openCheckpointEditorButton);
        this.buttonList.add(toggleBeamsButton);
        this.buttonList.add(toggleCheckpointButton);
        this.buttonList.add(gifScaleSlider);
        this.buttonList.add(gifStyleButton);
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
    private String getGifStyleText() {
        int idx = Config.getSelectedGifStyle();
        String style = gifStyleNames[idx];
        return "GIF Style: " + style;
    }

    public static String getSelectedGifPath() {
        int idx = Config.getSelectedGifStyle();
        return gifStylePaths[idx];
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiCheckpointEditor(this));
        } else if (button.id == 1) {
            ParkourSettings.toggleSaveCheckpointOnActivation();
            toggleCheckpointButton.displayString = getCheckpointButtonText();
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 6) {
            ParkourSettings.toggleBeams = !ParkourSettings.toggleBeams;
            toggleBeamsButton.displayString = getToggleBeamsText();
        } else if (button.id == 7) {
            int idx = (Config.getSelectedGifStyle() + 1) % gifStyleNames.length;
            Config.setSelectedGifStyle(idx);
            gifStyleButton.displayString = getGifStyleText();
            Config.saveSettings();
            Main.reloadGifRenderer();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        float newGifScale = gifScaleSlider.sliderPosition * 1.4f + 0.1f;
        if (newGifScale != Config.getGifScale()) {
            Config.setGifScale(newGifScale);
            Main.setGifScale(newGifScale);
        }

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