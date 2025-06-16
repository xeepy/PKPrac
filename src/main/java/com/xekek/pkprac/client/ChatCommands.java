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

import com.xekek.pkprac.modules.CPManager;
import com.xekek.pkprac.modules.PracticeMode;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class ChatCommands {


    public static boolean handleChatCommand(String message)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if (player == null) return false;
        if (message.startsWith(".setmaxcheckpoints") || message.startsWith(".setmaxcp"))
        {
            if (!PracticeMode.isPracticeModeEnabled())
            {
                String practiceKeyName = getKeyName(KeyHandler.toggleKey);
                player.addChatMessage(new ChatComponentText("Set max checkpoints only works in Practice Mode! Press " + practiceKeyName + " first."));
                return true;
            }

            String[] parts = message.split(" ");
            if (parts.length < 2)
            {
                player.addChatMessage(new ChatComponentText("Usage: .setmaxcheckpoints <number>"));
                return true;
            }

            try
            {
                int maxCheckpoints = Integer.parseInt(parts[1]);
                if (maxCheckpoints <= 0)
                {
                    player.addChatMessage(new ChatComponentText("Max checkpoints must be a positive number!"));
                    return true;
                }
                CPManager.MaxCheckpoints = maxCheckpoints;
                player.addChatMessage(new ChatComponentText("Max checkpoints set to " + maxCheckpoints));
            }
            catch (NumberFormatException e)
            {
                player.addChatMessage(new ChatComponentText("Invalid number format! Use a valid integer."));
            }
            return true;
        }
        if (message.startsWith(".clearcp") || message.startsWith(".cpclear"))
        {
            if (!PracticeMode.isPracticeModeEnabled())
            {
                String practiceKeyName = getKeyName(KeyHandler.toggleKey);
                player.addChatMessage(new ChatComponentText("Clear checkpoints only works in Practice Mode! Press " + practiceKeyName + " first."));
                return true;
            }

            CPManager.resetCheckpoints();
            player.addChatMessage(new ChatComponentText("All checkpoints cleared!"));
            return true;
        }
        if (message.startsWith(".teleport") || message.startsWith(".tp"))
        {
            if (!PracticeMode.isPracticeModeEnabled())
            {
                String practiceKeyName = getKeyName(KeyHandler.toggleKey);
                player.addChatMessage(new ChatComponentText("Teleport commands only work in Practice Mode! Press " + practiceKeyName + " first."));
                return true;
            }

            handleTeleportCommand(message, player);
            return true;
        }
        else if (message.startsWith(".face") || message.startsWith(".angle"))
        {
            if (!PracticeMode.isPracticeModeEnabled())
            {
                String practiceKeyName = getKeyName(KeyHandler.toggleKey);
                player.addChatMessage(new ChatComponentText("Angle commands only work in Practice Mode! Press " + practiceKeyName + " first."));
                return true;
            }

            handleFaceCommand(message, player);
            return true;
        }
        return false;
    }

    private static String getKeyName(KeyBinding key) {
        if (key == null) return "?";
        try {
            return Keyboard.getKeyName(key.getKeyCode());
        } catch (Exception e) {
            return key.getKeyDescription();
        }
    }

    private static void handleTeleportCommand(String message, EntityPlayer player)
    {
        String[] parts = message.split(" ");

        if (parts.length < 4)
        {
            player.addChatMessage(new ChatComponentText("Usage: .teleport <x> <y> <z> or .tp <x> <y> <z>"));
            player.addChatMessage(new ChatComponentText("Example: .tp 100.5 64 -50.2"));
            return;
        }

        try
        {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            player.setPositionAndRotation(x, y, z, player.rotationYaw, player.rotationPitch);
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
            player.fallDistance = 0;

            player.addChatMessage(new ChatComponentText("Teleported to (" + x + ", " + y + ", " + z + ")"));
        }
        catch (NumberFormatException e)
        {
            player.addChatMessage(new ChatComponentText("Invalid coordinates! Use numbers only."));
            player.addChatMessage(new ChatComponentText("Example: .tp 100.5 64 -50.2"));
        }
    }

    private static void handleFaceCommand(String message, EntityPlayer player)
    {
        String[] parts = message.split(" ");

        if (parts.length < 3)
        {
            player.addChatMessage(new ChatComponentText("Usage: .face <yaw> <pitch> or .angle <yaw> <pitch>"));
            player.addChatMessage(new ChatComponentText("Example: .face 90 0 (facing east, level)"));
            player.addChatMessage(new ChatComponentText("Yaw: 0=south, 90=west, 180=north, 270=east"));
            player.addChatMessage(new ChatComponentText("Pitch: -90=up, 0=level, 90=down"));
            return;
        }

        try
        {
            float yaw = Float.parseFloat(parts[1]);
            float pitch = Float.parseFloat(parts[2]);

            while (yaw > 180) yaw -= 360;
            while (yaw <= -180) yaw += 360;

            if (pitch > 90) pitch = 90;
            if (pitch < -90) pitch = -90;

            player.setPositionAndRotation(player.posX, player.posY, player.posZ, yaw, pitch);

            player.addChatMessage(new ChatComponentText("Rotation set to yaw=" + Math.round(yaw * 10) / 10.0 +
                    "°, pitch=" + Math.round(pitch * 10) / 10.0 + "°"));
        }
        catch (NumberFormatException e)
        {
            player.addChatMessage(new ChatComponentText("Invalid angles! Use numbers only."));
            player.addChatMessage(new ChatComponentText("Example: .face 90 0"));
        }
    }




}