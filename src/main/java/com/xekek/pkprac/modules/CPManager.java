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
package com.xekek.pkprac.modules;

import com.xekek.pkprac.renderer.Notifications;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import java.util.ArrayList;
import java.util.List;

public class CPManager {
    public static int MaxCheckpoints = 10;

    public static boolean isCheckpointTeleporting = false;

    public static class Checkpoint {
        public double x, y, z;
        public double motionX, motionY, motionZ;
        public float yaw, pitch;

        public Checkpoint(EntityPlayer player) {
            this.x = player.posX;
            this.y = player.posY;
            this.z = player.posZ;
            this.motionX = player.motionX;
            this.motionY = player.motionY;
            this.motionZ = player.motionZ;
            this.yaw = player.rotationYaw;
            this.pitch = player.rotationPitch;
        }
    }

    public static List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
    public static int currentIndex = -1;

    public static void saveCheckpoint(EntityPlayer player) {
        checkpoints.add(new Checkpoint(player));
        Notifications.add("Checkpoint saved at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")", Notifications.NotificationType.INFO);
        currentIndex = checkpoints.size() - 1;
        if (checkpoints.size() > MaxCheckpoints) {
            Notifications.add("Checkpoint limit reached. Oldest checkpoint will be removed.", Notifications.NotificationType.WARNING);
            checkpoints.remove(0);
            currentIndex--;
        }
    }

    public static void loadCheckpoint(EntityPlayer player, int index) {
        if(!PracticeMode.isPracticeModeEnabled()){
            Notifications.add("Practice mode is not enabled.", Notifications.NotificationType.ERROR);
            return;
        }
        if (!hasCheckpoints()) {
            Notifications.add("No checkpoints available to load.", Notifications.NotificationType.ERROR);
            return;
        }
        if (index >= 0 && index < checkpoints.size()) {
            Checkpoint cp = checkpoints.get(index);
            Notifications.add("Loading checkpoint " + (index + 1), Notifications.NotificationType.INFO);

            isCheckpointTeleporting = true;

            player.setPositionAndRotation(cp.x, cp.y, cp.z, cp.yaw, cp.pitch);
            player.motionX = cp.motionX;
            player.motionY = cp.motionY;
            player.motionZ = cp.motionZ;
            player.fallDistance = 0;
            currentIndex = index;

            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                isCheckpointTeleporting = false;
            }).start();
        }
    }

    public static void resetCheckpoints() {
        checkpoints.clear();
        currentIndex = -1;
    }
    public static boolean hasCheckpoints() {
        return !checkpoints.isEmpty();
    }
}