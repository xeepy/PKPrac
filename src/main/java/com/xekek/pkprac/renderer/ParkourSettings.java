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

import com.xekek.pkprac.client.Config;

public class ParkourSettings {
    public static boolean saveCheckpointOnActivation = true;
    public static boolean toggleBeams = true;

    public static float gifScale = 1.0f;

    public static void toggleSaveCheckpointOnActivation() {
        saveCheckpointOnActivation = !saveCheckpointOnActivation;
        Config.saveSettings();
    }
}