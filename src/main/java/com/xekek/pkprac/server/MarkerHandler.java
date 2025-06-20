package com.xekek.pkprac.server;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import java.util.List;

public class MarkerHandler {
    public static boolean isNoPracticeMarkerNearby(double x, double y, double z) {
        if (Minecraft.getMinecraft().theWorld == null) return false;
        String playerUUID = Minecraft.getMinecraft().thePlayer.getUniqueID().toString();
        int chunkX = (int) x >> 4;
        int chunkZ = (int) z >> 4;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int currentChunkX = chunkX + dx;
                int currentChunkZ = chunkZ + dz;

                double minX = currentChunkX * 16;
                double minZ = currentChunkZ * 16;
                double maxX = minX + 16;
                double maxZ = minZ + 16;

                AxisAlignedBB chunkBox = new AxisAlignedBB(minX, 0, minZ, maxX, 256, maxZ);
                List<Entity> entities = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(Entity.class, chunkBox);

                for (Entity entity : entities) {
                    if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
                        String name = entity.getCustomNameTag();
                        if (name.equals("NoPractice") || name.equals("NoPractice[" + playerUUID + "]")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isNoPracticeMarkerNearby(BlockPos pos) {
        return isNoPracticeMarkerNearby(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

}
