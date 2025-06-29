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
package com.xekek.pkprac.mixins;

import com.xekek.pkprac.modules.PracticeMode;
import com.xekek.pkprac.util.BlockStateManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {


    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    private void onClickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        if (PracticeMode.isPracticeModeEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = {"onPlayerRightClick"}, at = @At("HEAD"), cancellable = true)
    private void onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos pos, EnumFacing side, Vec3 vec, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (PracticeMode.isPracticeModeEnabled()) {
                if (pos != null && worldIn != null) {
                    if (BlockStateManager.isTrapdoor(pos)) {
                        BlockStateManager.toggleTrapdoorClient(pos);
                        cir.setReturnValue(true);
                        cir.cancel();
                        return;
                    }
                }
                cir.setReturnValue(false);
                cir.cancel();
            }
        } catch (Exception e) {
            System.err.println("[PKPrac] Error: " + e.getMessage());
        }
    }


    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        if (PracticeMode.isPracticeModeEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
