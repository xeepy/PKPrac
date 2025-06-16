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
import java.io.IOException;

public class GuiCheckpointEditor extends GuiScreen {
    private GuiScreen parentScreen;
    private int selectedIdx = -1;
    private int page = 0;
    private final int perPage = 5;
    private int listStartY;
    private int listEntryHeight = 24;

    public GuiCheckpointEditor(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        this.buttonList.clear();
        listStartY = centerY - 70;

        int start = page * perPage;
        int end = Math.min(CPManager.checkpoints.size(), start + perPage);
        for (int i = start; i < end; i++) {
            CPManager.Checkpoint cp = CPManager.checkpoints.get(i);
            String label = String.format("%d: (%.2f, %.2f, %.2f) Yaw: %.1f Pitch: %.1f", i + 1, cp.x, cp.y, cp.z, cp.yaw, cp.pitch);
            GuiButton btn = new GuiButton(1000 + i, centerX - 120, listStartY + (i - start) * listEntryHeight, 240, listEntryHeight - 4, label);
            if (i == selectedIdx) btn.packedFGColour = 0xFFAA00;
            this.buttonList.add(btn);
        }

        if (page > 0)
            this.buttonList.add(new GuiButton(200, centerX - 120, centerY + 60, 60, 20, "< Prev"));
        if ((page + 1) * perPage < CPManager.checkpoints.size())
            this.buttonList.add(new GuiButton(201, centerX + 60, centerY + 60, 60, 20, "Next >"));

        this.buttonList.add(new GuiButton(1, centerX - 120, centerY + 90, 60, 20, "Delete"));
        this.buttonList.add(new GuiButton(2, centerX - 50, centerY + 90, 60, 20, "Teleport"));
        this.buttonList.add(new GuiButton(3, centerX + 20, centerY + 90, 40, 20, "Up"));
        this.buttonList.add(new GuiButton(4, centerX + 65, centerY + 90, 40, 20, "Down"));
        this.buttonList.add(new GuiButton(100, centerX - 120, centerY + 150, 115, 20, "Export"));
        this.buttonList.add(new GuiButton(101, centerX + 5, centerY + 150, 115, 20, "Import"));
        this.buttonList.add(new GuiButton(102, centerX - 120, centerY + 120, 240, 20, "Done"));
        this.buttonList.add(new GuiButton(106, centerX - 120, centerY + 180, 240, 20, "Max Checkpoints: " + CPManager.MaxCheckpoints));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int centerX = this.width / 2;
        int start = page * perPage;
        int end = Math.min(CPManager.checkpoints.size(), start + perPage);

        if (button.id == 102) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 200 && page > 0) {
            page--;
            this.selectedIdx = -1;
            this.initGui();
        } else if (button.id == 201 && (page + 1) * perPage < CPManager.checkpoints.size()) {
            page++;
            this.selectedIdx = -1;
            this.initGui();
        } else if (button.id >= 1000 && button.id < 2000) {
            this.selectedIdx = button.id - 1000;
            this.initGui();
        } else if (button.id == 100) {
            exportCheckpoints();
        } else if (button.id == 101) {
            this.mc.displayGuiScreen(new GuiFilePicker(this));
        } else if (selectedIdx >= 0 && selectedIdx < CPManager.checkpoints.size()) {
            if (button.id == 1) {
                CPManager.checkpoints.remove(selectedIdx);
                if (CPManager.currentIndex >= CPManager.checkpoints.size())
                    CPManager.currentIndex = CPManager.checkpoints.size() - 1;
                if (selectedIdx >= CPManager.checkpoints.size()) selectedIdx = CPManager.checkpoints.size() - 1;
                this.initGui();
            } else if (button.id == 2) {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                CPManager.loadCheckpoint(player, selectedIdx);
            } else if (button.id == 3 && selectedIdx > 0) {
                CPManager.checkpoints.add(selectedIdx - 1, CPManager.checkpoints.remove(selectedIdx));
                selectedIdx--;
                this.initGui();
            } else if (button.id == 4 && selectedIdx < CPManager.checkpoints.size() - 1) {
                CPManager.checkpoints.add(selectedIdx + 1, CPManager.checkpoints.remove(selectedIdx));
                selectedIdx++;
                this.initGui();
            }
        }
    }

    private void exportCheckpoints() {
        try {
            java.io.File dir = new java.io.File(Minecraft.getMinecraft().mcDataDir, "pkprac");
            if (!dir.exists()) dir.mkdirs();
            int idx = 1;
            java.io.File file;
            do {
                file = new java.io.File(dir, "checkpoints" + idx + ".txt");
                idx++;
            } while (file.exists());
            java.io.PrintWriter writer = new java.io.PrintWriter(file);
            writer.println(CPManager.MaxCheckpoints);
            for (CPManager.Checkpoint cp : CPManager.checkpoints) {
                writer.printf("%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n", cp.x, cp.y, cp.z, cp.motionX, cp.motionY, cp.motionZ, cp.yaw, cp.pitch);
            }
            writer.close();
            try {
                java.awt.Desktop.getDesktop().open(dir);
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int centerX = this.width / 2;
        drawCenteredString(this.fontRendererObj, "Checkpoint Editor", centerX, listStartY - 24, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
