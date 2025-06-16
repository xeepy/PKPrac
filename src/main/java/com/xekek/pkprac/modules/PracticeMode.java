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

import com.xekek.pkprac.Main;
import com.xekek.pkprac.client.KeyHandler;
import com.xekek.pkprac.events.Packets;
import com.xekek.pkprac.renderer.Notifications;
import com.xekek.pkprac.renderer.ParkourSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;

import static com.xekek.pkprac.client.KeyHandler.getKeyName;

public class PracticeMode {

    public static boolean isEnabled = false;
    private static final Minecraft mc = Minecraft.getMinecraft();    public static double savedX, savedY, savedZ;
    private static float savedYaw, savedPitch;

    private static double preciseX, preciseY, preciseZ;
    private static float preciseYaw, precisePitch;

    public static boolean isFinishedResyncing = true;
    public static boolean justTeleported = false;

    public static net.minecraft.item.ItemStack hotbarItem = null;



    public static void togglePracticeMode() {
        double totalMotion = Math.abs(mc.thePlayer.motionX) + Math.abs(mc.thePlayer.motionZ);

        if(!mc.thePlayer.onGround){
            Notifications.add("You must be on the ground to toggle practice mode!", Notifications.NotificationType.WARNING);
            return;
        }
        if(mc.thePlayer.isSneaking()){
            Notifications.add("You must not be sneaking to toggle practice mode!", Notifications.NotificationType.WARNING);
            return;
        }

        if (totalMotion > 0.01) {
            String practiceKeyName = getKeyName(KeyHandler.toggleKey);
            Notifications.add("You have to be standing still to toggle practice mode.", Notifications.NotificationType.WARNING);
            return;
        }


        isEnabled = !isEnabled;
        if (isEnabled) {
            Notifications.add("Practice mode Enabled!", Notifications.NotificationType.SUCCESS);
            setPracticeMode();
        } else {
            isFinishedResyncing = false;
            if(mc.thePlayer.capabilities.isCreativeMode){
                mc.thePlayer.capabilities.allowFlying = true;
            }
            Notifications.add("Practice mode Disabled!", Notifications.NotificationType.DISABLED);
            setPracticeMode();
        }
    }    public static void setPracticeMode() {
        if (isEnabled) {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                hotbarItem = player.getHeldItem();
                preciseX = savedX = player.posX;
                preciseY = savedY = player.posY;
                preciseZ = savedZ = player.posZ;
                preciseYaw = savedYaw = player.rotationYaw;
                precisePitch = savedPitch = player.rotationPitch;
            }
            if(ParkourSettings.saveCheckpointOnActivation) {
                CPManager.saveCheckpoint(player);
            }
            isFinishedResyncing = false;
            Packets.resetPracticeSync();

        } else {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                player.setPosition(preciseX, preciseY, preciseZ);
                player.rotationPitch = precisePitch;
                player.rotationYaw = preciseYaw;
                player.prevRotationPitch = precisePitch;
                player.prevRotationYaw = preciseYaw;
                justTeleported = true;
            }
            savedX = savedY = savedZ = 0;
            savedYaw = savedPitch = 0f;
            preciseX = preciseY = preciseZ = 0;
            preciseYaw = precisePitch = 0f;
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
}
