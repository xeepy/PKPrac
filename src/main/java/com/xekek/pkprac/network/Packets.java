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
package com.xekek.pkprac.network;

import com.xekek.pkprac.client.ChatCommands;
import com.xekek.pkprac.modules.CPManager;
import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.renderer.Notifications;
import io.netty.channel.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;


import static com.xekek.pkprac.modules.PracticeMode.*;

public class Packets {

    public static int tickCounter = 0;
    private int lastTick = -1;
    private static int practiceTickCounter = 0;
    private static boolean needsPositionSync = false;
    private static boolean wasInPracticeMode = false;
    private static boolean needsDisableSync = false;

    private static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        event.manager.channel().pipeline().addBefore("packet_handler", "outbound",
                new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

                        EntityPlayerSP player = mc.thePlayer;
                        int currentTick = player != null ? player.ticksExisted : -1;

                        if (msg instanceof C03PacketPlayer && lastTick != currentTick) {
                            tickCounter++;
                            if (tickCounter >= 21) {
                                tickCounter = 0;
                            }
                            lastTick = currentTick;

                            boolean currentlyInPractice = PracticeMode.isPracticeModeEnabled();

                            if (wasInPracticeMode && !currentlyInPractice) {
                                needsDisableSync = true;
                                
                                practiceTickCounter = 0;
                            }

                            if (currentlyInPractice) {
                                practiceTickCounter++;
                                if (practiceTickCounter > 20) {
                                    practiceTickCounter = 0;
                                    needsPositionSync = true;
                                }
                            }

                            wasInPracticeMode = currentlyInPractice;
                        }

                        if (msg instanceof C01PacketChatMessage) {
                            String message = ((C01PacketChatMessage) msg).getMessage();
                            if (ChatCommands.handleChatCommand(message)) {
                                return;
                            }
                        }

                        if (PracticeMode.isPracticeModeEnabled()) {
                            if (player == null) {
                                super.write(ctx, msg, promise);
                                return;
                            }

                            if (msg instanceof C03PacketPlayer) {
                                if (needsPositionSync) {
                                    super.write(ctx, new C03PacketPlayer.C04PacketPlayerPosition(
                                            savedX, savedY, savedZ, true), promise);
                                    needsPositionSync = false;
                                } else {
                                    super.write(ctx, new C03PacketPlayer(true), promise);
                                }
                                return;
                            }

                            if (msg instanceof C0BPacketEntityAction || msg instanceof C13PacketPlayerAbilities ||
                                    msg instanceof C09PacketHeldItemChange || msg instanceof C0APacketAnimation ||
                                    msg instanceof C07PacketPlayerDigging || msg instanceof C08PacketPlayerBlockPlacement ||
                                    msg instanceof S08PacketPlayerPosLook || msg instanceof C02PacketUseEntity) {
                                return;
                            }
                        } else {
                            if (needsDisableSync && msg instanceof C03PacketPlayer && player != null) {
                                super.write(ctx, new C03PacketPlayer.C04PacketPlayerPosition(
                                        player.posX, player.posY, player.posZ, player.onGround), promise);
                                needsDisableSync = false;
                                return;
                            }
                        }

                        super.write(ctx, msg, promise);
                    }
                });

        event.manager.channel().pipeline().addBefore("packet_handler", "inbound",
                new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                        if (msg instanceof S08PacketPlayerPosLook && PracticeMode.isPracticeModeEnabled()) {
                            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) msg;

                            if (CPManager.isCheckpointTeleporting || PracticeMode.justTeleported) {
                                super.channelRead(ctx, msg);
                                return;
                            }

                            double distance = Math.sqrt(
                                    Math.pow(packet.getX() - savedX, 2) +
                                            Math.pow(packet.getY() - savedY, 2) +
                                            Math.pow(packet.getZ() - savedZ, 2)
                            );

                            if (distance > 1.0) {
                                Notifications.add("Something's gone wrong! Resyncing", Notifications.NotificationType.WARNING);
                                PracticeMode.handleServerTeleport(packet.getX(), packet.getY(), packet.getZ());
                            }
                        }

                        super.channelRead(ctx, msg);
                    }
                });
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        tickCounter = 0;
        lastTick = -1;
        practiceTickCounter = 0;
        needsPositionSync = false;
        wasInPracticeMode = false;
        needsDisableSync = false;
    }

    public static void resetPracticeSync() {
        practiceTickCounter = 0;
        needsPositionSync = true;
    }
}
