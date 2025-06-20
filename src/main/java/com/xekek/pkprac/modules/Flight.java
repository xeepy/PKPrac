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
import net.minecraft.client.Minecraft;

public class Flight {

    public static boolean isFlying = false;

    static Minecraft mc = Minecraft.getMinecraft();

    public static void Fly() {
        if (isFlying) {
            isFlying = false;
            mc.thePlayer.capabilities.isFlying = false;
            Notifications.add("Flight deactivated!", Notifications.NotificationType.ERROR);
        } else {
            isFlying = true;
            mc.thePlayer.capabilities.isFlying = true;
            mc.thePlayer.motionY = 0.1;
            mc.thePlayer.onGround = false;
            Notifications.add("Flight activated!", Notifications.NotificationType.SUCCESS);
        }
    }

}