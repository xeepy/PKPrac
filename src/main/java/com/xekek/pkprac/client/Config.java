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
package com.xekek.pkprac.client;

import java.io.*;

import com.xekek.pkprac.Main;
import com.xekek.pkprac.renderer.ParkourSettings;

import net.minecraft.client.Minecraft;

public class Config {

    private static final File DIR = new File(Minecraft.getMinecraft().mcDataDir, "pkprac");
    private static final String CONFIG_FILE = new File(DIR, "parkourmod_settings.cfg").getAbsolutePath();

    static {
        if (!DIR.exists()) DIR.mkdirs();
    }

    private static boolean saveCheckpointOnActivation = ParkourSettings.saveCheckpointOnActivation;
    private static float gifScale = ParkourSettings.gifScale;
    private static int selectedGifStyle = 0;

    public static int getSelectedGifStyle() {
        return selectedGifStyle;
    }
    public static void setSelectedGifStyle(int idx) {
        selectedGifStyle = idx;
    }
    public static float getGifScale() {
        return gifScale;
    }
    public static void setGifScale(float scale) {
        gifScale = scale;
        ParkourSettings.gifScale = scale;
        saveSettings();
    }

    public static void saveSettings() {
        try {
            File file = new File(CONFIG_FILE);
            FileWriter writer = new FileWriter(file);
            writer.write("saveCheckpointOnActivation=" +  saveCheckpointOnActivation  + "\n");
            writer.write("gifScale=" + gifScale + "\n");
            writer.write("selectedGifStyle=" + selectedGifStyle + "\n");
            writer.write("toggleBeams=" + ParkourSettings.toggleBeams + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("[ParkourMod] Failed to save settings: " + e.getMessage());
        }
    }
    public static void loadSettings() {
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("saveCheckpointOnActivation=")) {
                    saveCheckpointOnActivation = Boolean.parseBoolean(line.split("=")[1]);
                } else if (line.startsWith("gifScale=")) {
                    try {
                        gifScale = Float.parseFloat(line.split("=")[1]);
                        Main.setGifScale(gifScale);
                    } catch (Exception ignored) {}
                } else if (line.startsWith("selectedGifStyle=")) {
                    try {
                        selectedGifStyle = Integer.parseInt(line.split("=")[1]);
                    } catch (Exception ignored) {}
                } else if (line.startsWith("toggleBeams=")) {
                    ParkourSettings.toggleBeams = Boolean.parseBoolean(line.split("=")[1]);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("[ParkourMod] Failed to load settings: " + e.getMessage());
        }
    }

}
