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

import com.xekek.pkprac.modules.CPManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiFilePicker extends GuiScreen {
    private GuiScreen parentScreen;
    private List<File> files = new ArrayList<>();
    private int selectedIdx = -1;
    private int page = 0;
    private final int perPage = 5;
    private int listStartY;
    private int listEntryHeight = 24;

    public GuiFilePicker(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        this.buttonList.clear();
        listStartY = centerY - 70;

        File dir = new File(Minecraft.getMinecraft().mcDataDir, "pkprac");
        if (!dir.exists()) dir.mkdirs();
        File[] fileArray = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (fileArray != null) {
            files.clear();
            for (File file : fileArray) {
                files.add(file);
            }
        }

        int start = page * perPage;
        int end = Math.min(files.size(), start + perPage);
        for (int i = start; i < end; i++) {
            File file = files.get(i);
            String label = file.getName();
            GuiButton btn = new GuiButton(1000 + i, centerX - 120, listStartY + (i - start) * listEntryHeight, 240, listEntryHeight - 4, label);
            if (i == selectedIdx) btn.packedFGColour = 0xFFAA00; // highlight selected
            this.buttonList.add(btn);
        }

        if (page > 0)
            this.buttonList.add(new GuiButton(200, centerX - 120, centerY + 60, 60, 20, "< Prev"));
        if ((page + 1) * perPage < files.size())
            this.buttonList.add(new GuiButton(201, centerX + 60, centerY + 60, 60, 20, "Next >"));

        this.buttonList.add(new GuiButton(102, centerX - 120, centerY + 120, 240, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int centerX = this.width / 2;
        int start = page * perPage;
        int end = Math.min(files.size(), start + perPage);

        if (button.id == 102) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 200 && page > 0) {
            page--;
            this.selectedIdx = -1;
            this.initGui();
        } else if (button.id == 201 && (page + 1) * perPage < files.size()) {
            page++;
            this.selectedIdx = -1;
            this.initGui();
        } else if (button.id >= 1000 && button.id < 2000) {
            selectedIdx = button.id - 1000;
            importCheckpoints(files.get(selectedIdx));
            this.mc.displayGuiScreen(new GuiCheckpointEditor(this));
        }
    }

    private void importCheckpoints(File file) {
        try {
            List<CPManager.Checkpoint> imported = new ArrayList<>();
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            java.util.Scanner scanner = new java.util.Scanner(file);
            if (scanner.hasNextLine()) {
                String firstLine = scanner.nextLine();
                try {
                    CPManager.MaxCheckpoints = Integer.parseInt(firstLine.trim());
                } catch (NumberFormatException e) {
                    CPManager.MaxCheckpoints = 25;
                }
            }
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 8) {
                    CPManager.Checkpoint cp = new CPManager.Checkpoint(player);
                    cp.x = Double.parseDouble(parts[0]);
                    cp.y = Double.parseDouble(parts[1]);
                    cp.z = Double.parseDouble(parts[2]);
                    cp.motionX = Double.parseDouble(parts[3]);
                    cp.motionY = Double.parseDouble(parts[4]);
                    cp.motionZ = Double.parseDouble(parts[5]);
                    cp.yaw = Float.parseFloat(parts[6]);
                    cp.pitch = Float.parseFloat(parts[7]);
                    imported.add(cp);
                }
            }
            scanner.close();
            CPManager.checkpoints = imported;
            CPManager.currentIndex = imported.size() - 1;
            this.selectedIdx = -1;
            this.initGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int centerX = this.width / 2;
        drawCenteredString(this.fontRendererObj, "Select Checkpoints File", centerX, listStartY - 24, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}