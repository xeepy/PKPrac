package com.xekek.pkprac.client;

import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.renderer.Notifications;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BlockDetection {

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

                boolean standingOnSomething = false;
                for (BlockPos pos : positions) {
                    BlockPos checkPos = pos.add(0, 0, 0);
                    IBlockState state = Minecraft.getMinecraft().theWorld.getBlockState(checkPos);
                    Block block = state.getBlock();

                    System.out.println("Checking block at " + checkPos + ": " + block.getUnlocalizedName());

                    if(!block.isAir(Minecraft.getMinecraft().theWorld, checkPos) && state.getBlock().isBlockSolid(Minecraft.getMinecraft().theWorld, checkPos, EnumFacing.UP)) {
                        standingOnSomething = true;
                        break;
                    }
                    AxisAlignedBB collisionBox = block.getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, checkPos, state);
                    if (collisionBox != null) {
                        standingOnSomething = true;
                        break;
                    }
                    if (standingOnSomething) {
                        break;
                    }
                }

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

                System.out.println("Block states captured: " + currentBlockStates.length);
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
