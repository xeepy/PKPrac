package com.xekek.pkprac.util;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class BlockStateManager {
    private static final Map<BlockPos, IBlockState> changedTrapdoors = new HashMap<>();

    public static void toggleTrapdoorClient(BlockPos pos) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockTrapDoor)) return;
        if (!changedTrapdoors.containsKey(pos)) {
            changedTrapdoors.put(pos, state);
        }

        boolean isOpen = state.getValue(BlockTrapDoor.OPEN);
        IBlockState newState = state.withProperty(BlockTrapDoor.OPEN, !isOpen);
        world.setBlockState(pos, newState, 2);
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public static boolean isTrapdoor(BlockPos pos) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockTrapDoor;
    }

    public static void restoreAll() {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        for (Map.Entry<BlockPos, IBlockState> entry : changedTrapdoors.entrySet()) {
            BlockPos pos = entry.getKey();
            IBlockState originalState = entry.getValue();
            world.setBlockState(pos, originalState, 2);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
        changedTrapdoors.clear();
    }

    public static void clear() {
        changedTrapdoors.clear();
    }
}
