package com.xekek.pkprac.client;

import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.renderer.Notifications;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.xekek.pkprac.modules.PracticeMode.savedBB;

public class ResyncDetection {

    private static int lastHurtTime = 0;
    public static boolean needsResync = false;

    private static String lastWorldName = "";
    private static boolean firstTick = true;

    private static String[] lastBlockStates = new String[0];
    private static boolean blockStatesInitialized = false;


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().thePlayer != null) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

            String currentWorldName = "";
            if (Minecraft.getMinecraft().theWorld != null) {
                currentWorldName = Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName();
            }

            if (PracticeMode.isPracticeModeEnabled()) {
                WorldClient world = Minecraft.getMinecraft().theWorld;
                BlockPos playerPos = player.getPosition();

                int renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
                int anchorChunkX = (int) PracticeMode.savedX >> 4;
                int anchorChunkZ = (int) PracticeMode.savedZ >> 4;

                int minChunkX = anchorChunkX - renderDistance;
                int maxChunkX = anchorChunkX + renderDistance;
                int minChunkZ = anchorChunkZ - renderDistance;
                int maxChunkZ = anchorChunkZ + renderDistance;

                int playerChunkX = playerPos.getX() >> 4;
                int playerChunkZ = playerPos.getZ() >> 4;

                if (playerChunkX < minChunkX || playerChunkX > maxChunkX || playerChunkZ < minChunkZ || playerChunkZ > maxChunkZ) {
                    PracticeMode.teleportToAnchor();
                    Notifications.add("Exceeded render distance chunk limit! Teleporting back to anchor.", Notifications.NotificationType.WARNING);
                    return;
                }

                Chunk chunk = world.getChunkFromBlockCoords(playerPos);
                if (chunk == null || chunk.isEmpty()) {
                    PracticeMode.teleportToAnchor();
                    Notifications.add("Entered unloaded chunk! Teleporting back to anchor.", Notifications.NotificationType.WARNING);
                    return;
                }

                boolean worldChanged = !firstTick && !currentWorldName.equals(lastWorldName);
                AxisAlignedBB feetBB = new AxisAlignedBB(
                        savedBB.minX, savedBB.minY - 0.05, savedBB.minZ,
                        savedBB.maxX, savedBB.minY, savedBB.maxZ
                );

                boolean standingOnSomething = !Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(player, feetBB).isEmpty();
                boolean blockBroken = !standingOnSomething;

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
}
