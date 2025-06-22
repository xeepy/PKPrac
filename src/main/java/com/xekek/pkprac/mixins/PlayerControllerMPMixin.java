package com.xekek.pkprac.mixins;

import com.xekek.pkprac.modules.PracticeMode;
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

    @Inject(method = "onPlayerRightClick", at = @At("HEAD"), cancellable = true)
    private void onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos pos, EnumFacing side, Vec3 vec, CallbackInfoReturnable<Boolean> cir) {
        if (PracticeMode.isPracticeModeEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
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
