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

import com.xekek.pkprac.client.KeyHandler;
import com.xekek.pkprac.network.Packets;
import com.xekek.pkprac.renderer.Notifications;
import com.xekek.pkprac.renderer.ParkourSettings;
import com.xekek.pkprac.server.MarkerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import static com.xekek.pkprac.client.KeyHandler.getKeyName;

public class PracticeMode {

    public static boolean isEnabled = false;
    private static final Minecraft mc = Minecraft.getMinecraft();    public static double savedX, savedY, savedZ;
    private static float savedYaw, savedPitch;

    private static double preciseX, preciseY, preciseZ;
    private static float preciseYaw, precisePitch;

    public static boolean isFinishedResyncing = true;
    public static boolean justTeleported = false;

    private static int savedHotbarSlot;
    private static long lastToggleTime = 0;


    public static void togglePracticeMode() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime < 2000) {
            Notifications.add("You must wait 2 seconds before toggling practice mode again!", Notifications.NotificationType.WARNING);
            return;
        }

        double totalMotion = Math.abs(mc.thePlayer.motionX) + Math.abs(mc.thePlayer.motionZ);

        if (!mc.thePlayer.onGround) {
            Notifications.add("You must be on the ground to toggle practice mode!", Notifications.NotificationType.WARNING);
            return;
        }
        if (mc.thePlayer.isSneaking()) {
            Notifications.add("You must not be sneaking to toggle practice mode!", Notifications.NotificationType.WARNING);
            return;
        }
        if (MarkerHandler.isNoPracticeMarkerNearby(mc.thePlayer.getPosition())) {
            Notifications.add("You cannot toggle practice mode near a no-practice marker!", Notifications.NotificationType.WARNING);
            return;
        }

        if (totalMotion > 0.00) {
            Notifications.add("You have to be standing still to toggle practice mode.", Notifications.NotificationType.WARNING);
            return;
        }


        isEnabled = !isEnabled;
        if (isEnabled) {
            lastToggleTime = currentTime;
            Notifications.add("Practice mode Enabled!", Notifications.NotificationType.SUCCESS);
            setPracticeMode();
        } else {
            isFinishedResyncing = false;
            if (mc.thePlayer.capabilities.isCreativeMode) {
                mc.thePlayer.capabilities.allowFlying = true;
            }
            Notifications.add("Practice mode Disabled!", Notifications.NotificationType.DISABLED);
            setPracticeMode();
        }
    }    public static void setPracticeMode() {
        if (isEnabled) {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                savedHotbarSlot = player.inventory.currentItem;
                preciseX = savedX = player.posX;
                preciseY = savedY = player.posY;
                preciseZ = savedZ = player.posZ;
                preciseYaw = savedYaw = player.rotationYaw;
                precisePitch = savedPitch = player.rotationPitch;
            }
            if (ParkourSettings.saveCheckpointOnActivation) {
                CPManager.saveCheckpoint(player);
            }
            isFinishedResyncing = false;
            Packets.resetPracticeSync();

        } else {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                justTeleported = true;
                player.inventory.currentItem = savedHotbarSlot;
                player.setPosition(preciseX, preciseY, preciseZ);
                player.rotationPitch = precisePitch;
                player.rotationYaw = preciseYaw;
                player.prevRotationPitch = precisePitch;
                player.prevRotationYaw = preciseYaw;

                new Thread(() -> {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    justTeleported = false;
                }).start();
            }
        }
    }
    public static void shutdown() {
        isEnabled = false;
        isFinishedResyncing = true;
        savedX = savedY = savedZ = 0;

    }

    public static boolean isPracticeModeEnabled() {
        return isEnabled;
    }

    public static void handleServerTeleport(double x, double y, double z) {
        if (isPracticeModeEnabled()) {
            savedX = preciseX = x;
            savedY = preciseY = y;
            savedZ = preciseZ = z;

            isEnabled = false;
            if (Flight.isFlying){
                Flight.Fly();
            }

            savedYaw = savedPitch = 0f;
            preciseYaw = precisePitch = 0f;

            isFinishedResyncing = true;
        }
    }
}
