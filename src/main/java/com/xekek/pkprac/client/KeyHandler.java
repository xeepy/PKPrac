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

import com.xekek.pkprac.Main;
import com.xekek.pkprac.modules.CPManager;
import com.xekek.pkprac.modules.Flight;
import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.renderer.GuiParkourSettings;
import com.xekek.pkprac.renderer.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyHandler {

    private Minecraft mc = Minecraft.getMinecraft();

    public static KeyBinding toggleKey = new KeyBinding("Toggle", Keyboard.KEY_G, "PKPrac");
    public static KeyBinding openSettingGui = new KeyBinding("Open Settings GUI", Keyboard.KEY_P, "PKPrac");

    public static KeyBinding loadCheckpoint = new KeyBinding("Load Checkpoint", Keyboard.KEY_R, "PKPrac");
    public static KeyBinding saveCheckpoint = new KeyBinding("Save Checkpoint", Keyboard.KEY_Z, "PKPrac");

    public static KeyBinding nextCheckpointKey = new KeyBinding("Next Checkpoint", Keyboard.KEY_F9, "PKPrac");
    public static KeyBinding prevCheckpointKey = new KeyBinding("Previous Checkpoint", Keyboard.KEY_F10, "PKPrac");

    public static KeyBinding flight = new KeyBinding("Practice Flight", Keyboard.KEY_F, "PKPrac");

    public KeyHandler() {
        ClientRegistry.registerKeyBinding(toggleKey);
        ClientRegistry.registerKeyBinding(openSettingGui);
        ClientRegistry.registerKeyBinding(loadCheckpoint);
        ClientRegistry.registerKeyBinding(saveCheckpoint);
        ClientRegistry.registerKeyBinding(nextCheckpointKey);
        ClientRegistry.registerKeyBinding(prevCheckpointKey);
        ClientRegistry.registerKeyBinding(flight);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static String getKeyName(KeyBinding key) {
        if (key == null) return "?";
        try {
            return Keyboard.getKeyName(key.getKeyCode());
        } catch (Exception e) {
            return key.getKeyDescription();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleKey.isPressed()) {
            if (mc.thePlayer != null) {
                PracticeMode.togglePracticeMode();
            }
        }

        boolean isPracticeModeEnabled = PracticeMode.isPracticeModeEnabled() && mc.thePlayer != null;

            if (flight.isPressed() && isPracticeModeEnabled) {
                Flight.Fly();
            }
            if (loadCheckpoint.isPressed() && isPracticeModeEnabled) {
                CPManager.loadCheckpoint(mc.thePlayer, CPManager.currentIndex);
            }
            if (saveCheckpoint.isPressed() && isPracticeModeEnabled) {
                CPManager.saveCheckpoint(mc.thePlayer);
            }

            if(nextCheckpointKey.isPressed() && isPracticeModeEnabled) {
                CPManager.currentIndex++;
                if (CPManager.currentIndex >= CPManager.checkpoints.size()) {
                    CPManager.currentIndex = 0;
                }
                Notifications.add("Switched to next checkpoint: " + (CPManager.currentIndex + 1), Notifications.NotificationType.INFO);
            }

            if(prevCheckpointKey.isPressed() && isPracticeModeEnabled) {
                CPManager.currentIndex--;
                if (CPManager.currentIndex < 0) {
                    CPManager.currentIndex = CPManager.checkpoints.size() - 1;
                }
                Notifications.add("Switched to previous checkpoint: " + (CPManager.currentIndex + 1), Notifications.NotificationType.INFO);
            }

        if (openSettingGui.isPressed()) {
            if (mc.thePlayer != null) {
                mc.displayGuiScreen(new GuiParkourSettings(mc.currentScreen));
            }
        }


    }
}
