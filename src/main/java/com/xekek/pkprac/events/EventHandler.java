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
package com.xekek.pkprac.events;

import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.renderer.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandler {    private static final Minecraft mc = Minecraft.getMinecraft();
    private static long lastNotificationTime = 0;
    private static final long NOTIFICATION_COOLDOWN = 1000;

    // Track player state to prevent bypass attempts
    private static boolean wasLookingAtBlock = false;
    private static boolean leftClickHeld = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!PracticeMode.isPracticeModeEnabled()) return;

        if (mc.thePlayer != null && mc.thePlayer.worldObj != null &&
            mc.thePlayer.worldObj.getWorldInfo().getGameType() == WorldSettings.GameType.ADVENTURE) {
            return;
        }

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK ||
            event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {

            event.setCanceled(true);
            showCooldownNotification("Cannot interact with blocks in practice mode", Notifications.NotificationType.WARNING);
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(MouseEvent event) {
        if (!PracticeMode.isPracticeModeEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (event.button == 0) {
            leftClickHeld = event.buttonstate;

            if (event.buttonstate && mc.objectMouseOver != null &&
                mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                event.setCanceled(true);
                leftClickHeld = false;
                showCooldownNotification("Cannot break blocks in practice mode", Notifications.NotificationType.WARNING);
                return;
            }
        }

        if ((event.button == 0 || event.button == 1) && event.buttonstate) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                event.setCanceled(true);

                String message = event.button == 0 ?
                    "Cannot break blocks in practice mode" :
                    "Cannot place/interact with blocks in practice mode";

                showCooldownNotification(message, Notifications.NotificationType.WARNING);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!PracticeMode.isPracticeModeEnabled()) return;
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        boolean currentlyLookingAtBlock = mc.objectMouseOver != null &&
            mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
        if (leftClickHeld && currentlyLookingAtBlock) {
            if (mc.playerController != null) {
                mc.playerController.resetBlockRemoving();
                try {
                    java.lang.reflect.Field blockHitDelayField = mc.playerController.getClass().getDeclaredField("blockHitDelay");
                    blockHitDelayField.setAccessible(true);
                    blockHitDelayField.setInt(mc.playerController, 5);
                } catch (Exception e) {
                }
            }


            try {
                java.lang.reflect.Field pressedField = mc.gameSettings.keyBindAttack.getClass().getDeclaredField("pressed");
                pressedField.setAccessible(true);
                pressedField.setBoolean(mc.gameSettings.keyBindAttack, false);

                java.lang.reflect.Field pressTimeField = mc.gameSettings.keyBindAttack.getClass().getDeclaredField("pressTime");
                pressTimeField.setAccessible(true);
                pressTimeField.setInt(mc.gameSettings.keyBindAttack, 0);
            } catch (Exception e) {
                try {
                    java.lang.reflect.Method unpressMethod = mc.gameSettings.keyBindAttack.getClass().getDeclaredMethod("unpressKey");
                    unpressMethod.setAccessible(true);
                    unpressMethod.invoke(mc.gameSettings.keyBindAttack);
                } catch (Exception e2) {
                    leftClickHeld = false;
                }
            }

            showCooldownNotification("Cannot break blocks in practice mode", Notifications.NotificationType.WARNING);
        }

        wasLookingAtBlock = currentlyLookingAtBlock;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!PracticeMode.isPracticeModeEnabled()) return;

        if (event.getPlayer() != null && event.getPlayer() == mc.thePlayer) {
            event.setCanceled(true);
            showCooldownNotification("Cannot break blocks in practice mode", Notifications.NotificationType.WARNING);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (!PracticeMode.isPracticeModeEnabled()) return;

        if (event.player != null && event.player == mc.thePlayer) {
            event.setCanceled(true);
            showCooldownNotification("Cannot place blocks in practice mode", Notifications.NotificationType.WARNING);
        }
    }
    private static void showCooldownNotification(String message, Notifications.NotificationType type) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNotificationTime > NOTIFICATION_COOLDOWN) {
            Notifications.add(message, type);
            lastNotificationTime = currentTime;
        }
    }
}