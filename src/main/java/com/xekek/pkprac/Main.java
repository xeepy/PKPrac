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
package com.xekek.pkprac;

import com.xekek.pkprac.client.ResyncDetection;
import com.xekek.pkprac.client.KeyHandler;
import com.xekek.pkprac.client.Config;
import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.network.Packets;
import com.xekek.pkprac.renderer.*;
import com.xekek.pkprac.renderer.Beams;
import com.xekek.pkprac.renderer.gui.GifRenderer;
import com.xekek.pkprac.renderer.gui.GuiParkourSettings;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;


@Mod(modid = Main.MODID, version = Main.VERSION, name = "pkprac")
public class Main
{
    public static final String MODID = "pkprac";
    public static final String VERSION = "1.0.4";

    private static GifRenderer gifRenderer;
    private static Notifications notificationSystem;

    Beams beams = new Beams();

    public static float getGifScale() {
        if (gifRenderer != null) return gifRenderer.getScale();
        return ParkourSettings.gifScale;
    }

    public static void setGifScale(float scale) {
        ParkourSettings.gifScale = scale;
        if (gifRenderer != null) gifRenderer.setScale(scale);
        Config.saveSettings();
    }

    public static void reloadGifRenderer() {
        if (gifRenderer != null) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(gifRenderer);
        }
        gifRenderer = new GifRenderer(
            GuiParkourSettings.getSelectedGifPath()
        );
        gifRenderer.setScale(ParkourSettings.gifScale);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(gifRenderer);
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        notificationSystem = new Notifications();
        MinecraftForge.EVENT_BUS.register(notificationSystem);
        MinecraftForge.EVENT_BUS.register(new UpdateChecker());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Config.loadSettings();
        reloadGifRenderer();
        MinecraftForge.EVENT_BUS.register(new Beams());
        MinecraftForge.EVENT_BUS.register(new Packets());
        MinecraftForge.EVENT_BUS.register(new PracticeMode());
        MinecraftForge.EVENT_BUS.register(new ResyncDetection());
        beams = new Beams();
        new KeyHandler();
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
    }
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PracticeMode.shutdown();
        Config.saveSettings();
        if (gifRenderer != null) {
            MinecraftForge.EVENT_BUS.unregister(gifRenderer);
            gifRenderer = null;
        }
    }
}
