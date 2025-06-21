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

import com.xekek.pkprac.client.KeyHandler;
import com.xekek.pkprac.events.EventHandler;
import com.xekek.pkprac.client.Config;
import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.network.Packets;
import com.xekek.pkprac.renderer.*;
import com.xekek.pkprac.renderer.Beams;
import com.xekek.pkprac.renderer.gui.GifRenderer;
import com.xekek.pkprac.renderer.gui.GuiParkourSettings;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;


@Mod(modid = Main.MODID, version = Main.VERSION, name = "PKPrac")
public class Main
{
    public static final String MODID = "PKPrac";
    public static final String VERSION = "1.0.0";

    private static int lastHurtTime = 0;
    public static boolean needsResync = false;

    private static String lastWorldName = "";
    private static boolean firstTick = true;

    private static String[] lastBlockStates = new String[0];
    private static boolean blockStatesInitialized = false;

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
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(gifRenderer);
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        notificationSystem = new Notifications();
        MinecraftForge.EVENT_BUS.register(notificationSystem);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Config.loadSettings();
        reloadGifRenderer();
        MinecraftForge.EVENT_BUS.register(new Beams());
        MinecraftForge.EVENT_BUS.register(new Packets());
        MinecraftForge.EVENT_BUS.register(new PracticeMode());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
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

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().thePlayer != null) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

            String currentWorldName = "";
            if (Minecraft.getMinecraft().theWorld != null) {
                currentWorldName = Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName();
            }

            if (PracticeMode.isPracticeModeEnabled()) {
                boolean worldChanged = !firstTick && !currentWorldName.equals(lastWorldName);

                double minX = PracticeMode.savedX - 0.3;
                double maxX = PracticeMode.savedX + 0.3;
                double minZ = PracticeMode.savedZ - 0.3;
                double maxZ = PracticeMode.savedZ + 0.3;
                double y = Math.floor(PracticeMode.savedY - 0.03);

                BlockPos[] positions = new BlockPos[] {
                        new BlockPos(minX, y, minZ),
                        new BlockPos(maxX, y, minZ),
                        new BlockPos(minX, y, maxZ),
                        new BlockPos(maxX, y, maxZ)
                };

                boolean allAir = true;
                boolean standingOnSomething = false;

                for (BlockPos pos : positions) {
                    boolean foundSolid = false;
                    for (int i = -1; i <= 2; i++) {
                        BlockPos checkPos = new BlockPos(pos.getX(), pos.getY() + i, pos.getZ());
                        Block block = Minecraft.getMinecraft().theWorld.getBlockState(checkPos).getBlock();
                        String name = block.getUnlocalizedName();

                        if (!name.contains("air")) {
                            if (hasCollisionAtPlayerPosition(checkPos, PracticeMode.savedX, PracticeMode.savedZ)) {
                                foundSolid = true;
                                break;
                            }
                        }
                    }

                    if (foundSolid) {
                        allAir = false;
                        standingOnSomething = true;
                    }
                }

                boolean blockBroken = allAir && !standingOnSomething;
                boolean wasHit = player.hurtTime > 0 && player.hurtTime != lastHurtTime;
                boolean isDead = player.isDead || player.getHealth() <= 0;

                boolean blockStateChanged = false;
                String[] currentBlockStates = captureBlockStates(PracticeMode.savedX, PracticeMode.savedY, PracticeMode.savedZ);

                if (blockStatesInitialized && currentBlockStates.length == lastBlockStates.length) {
                    for (int i = 0; i < currentBlockStates.length; i++) {
                        if (!currentBlockStates[i].equals(lastBlockStates[i])) {
                            blockStateChanged = true;
                            break;
                        }
                    }
                } else if (!blockStatesInitialized) {
                    blockStatesInitialized = true;
                }
                lastBlockStates = currentBlockStates;

                if (!needsResync && (blockBroken || wasHit || isDead || worldChanged || blockStateChanged)) {
                    PracticeMode.isEnabled = false;
                    PracticeMode.setPracticeMode();

                    String reason = "Something's gone wrong! Resyncing.";
                    if (worldChanged) reason = "World change detected! Resyncing.";
                    if (blockStateChanged) reason = "Block state changed! Resyncing.";

                    Notifications.add(reason, Notifications.NotificationType.WARNING);
                    needsResync = true;
                }
                lastHurtTime = player.hurtTime;
            } else {
                needsResync = false;
                blockStatesInitialized = false;
            }

            lastWorldName = currentWorldName;
            firstTick = false;

            if (PracticeMode.justTeleported) {
                PracticeMode.justTeleported = false;
            }
        }
    }
    public static Notifications getNotificationSystem() {
        return notificationSystem;
    }

    private static String[] captureBlockStates(double centerX, double centerY, double centerZ) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return new String[0];
        }

        java.util.List<String> states = new java.util.ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = new BlockPos(centerX + x, centerY + y, centerZ + z);

                    try {
                        IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(pos);
                        Block block = blockState.getBlock();
                        String blockName = block.getUnlocalizedName();

                        if (blockName.contains("trapdoor") || blockName.contains("door") ||
                            blockName.contains("button") || blockName.contains("lever") ||
                            blockName.contains("pressure") || blockName.contains("tripwire") ||
                            blockName.contains("redstone") || blockName.contains("piston") ||
                            blockName.contains("fence_gate") || blockName.contains("torch")) {

                            String stateString = pos.toString() + ":" + blockState.toString();
                            states.add(stateString);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        return states.toArray(new String[0]);
    }

    private static boolean hasCollisionAtPlayerPosition(BlockPos blockPos, double playerX, double playerZ) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return true;
        }

        try {
            IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(blockPos);
            Block block = blockState.getBlock();

            AxisAlignedBB collisionBox = block.getCollisionBoundingBox(
                Minecraft.getMinecraft().theWorld, blockPos, blockState);

            if (collisionBox == null) {
                return false;
            }

            double relativeX = playerX - blockPos.getX();
            double relativeZ = playerZ - blockPos.getZ();

            double boxMinX = collisionBox.minX - blockPos.getX();
            double boxMaxX = collisionBox.maxX - blockPos.getX();
            double boxMinZ = collisionBox.minZ - blockPos.getZ();
            double boxMaxZ = collisionBox.maxZ - blockPos.getZ();

            boolean xInBounds = relativeX >= boxMinX && relativeX <= boxMaxX;
            boolean zInBounds = relativeZ >= boxMinZ && relativeZ <= boxMaxZ;

            double playerRadius = 0.3;

            boolean xOverlaps = (relativeX + playerRadius >= boxMinX) && (relativeX - playerRadius <= boxMaxX);
            boolean zOverlaps = (relativeZ + playerRadius >= boxMinZ) && (relativeZ - playerRadius <= boxMaxZ);

            return xOverlaps && zOverlaps;

        } catch (Exception e) {
            return true;
        }
    }
}
