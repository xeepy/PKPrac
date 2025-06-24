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
package com.xekek.pkprac.renderer;

import com.xekek.pkprac.modules.CPManager;
import com.xekek.pkprac.modules.PracticeMode;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static com.xekek.pkprac.modules.PracticeMode.*;

public class Beams {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!PracticeMode.isPracticeModeEnabled()) return;
        if (!ParkourSettings.toggleBeams) return;
        renderBeam(savedX, savedY, savedZ, 0.2f, 0.8f, 1.0f, 0.7f, event);

        if (CPManager.hasCheckpoints() && CPManager.checkpoints.get(CPManager.currentIndex) != null) {
            CPManager.Checkpoint cp = CPManager.checkpoints.get(CPManager.currentIndex);
            renderBeam(cp.x, cp.y, cp.z, 0.6f, 1.0f, 0.6f, 0.7f, event);
        }
    }

    private static void setupBeamRendering(float r, float g, float b, float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, alpha);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDepthMask(true);
        GL11.glLineWidth(6.0f);
    }

    private static void cleanupBeamRendering() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public static void renderBeam(double x, double y, double z, float r, float g, float b, float alpha, RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        double rx = x - mc.getRenderManager().viewerPosX;
        double ry = y - mc.getRenderManager().viewerPosY;
        double rz = z - mc.getRenderManager().viewerPosZ;

        setupBeamRendering(r, g, b, alpha);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(rx, ry, rz);
        GL11.glVertex3d(rx, ry + 10, rz);
        GL11.glEnd();
        cleanupBeamRendering();
    }
}