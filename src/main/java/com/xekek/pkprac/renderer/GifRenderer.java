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

import com.xekek.pkprac.modules.PracticeMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GifRenderer extends Gui
{
    private List<BufferedImage> originalFrames = new ArrayList<BufferedImage>();
    private List<DynamicTexture> frames = new ArrayList<DynamicTexture>();
    private List<ResourceLocation> frameLocations = new ArrayList<ResourceLocation>();
    private List<Integer> delays = new ArrayList<Integer>();
    private List<Integer> frameWidths = new ArrayList<Integer>();
    private List<Integer> frameHeights = new ArrayList<Integer>();
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private boolean isLoaded = false;

    private float scale = 0.5f;
    private int originalWidth = 240;
    private int originalHeight = 184;

    public GifRenderer(String gifPath)
    {
        loadGif(gifPath);
    }    private void loadGif(String gifPath)
    {
        try
        {
            ResourceLocation rl = new ResourceLocation(gifPath);
            InputStream gifStream = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            if (gifStream == null)
            {
                System.out.println("GIF resource not found: " + gifPath);
                return;
            }
            int available = gifStream.available();
            if (available == 0) {
                System.out.println("GIF resource is empty: " + gifPath);
                gifStream.close();
                return;
            } else {
                System.out.println("Loaded GIF resource " + gifPath + " with size: " + available + " bytes");
            }
            ImageInputStream stream = ImageIO.createImageInputStream(gifStream);
            ImageReader reader = null;
            try {
                reader = ImageIO.getImageReadersByFormatName("gif").next();
            } catch (Exception e) {
                System.out.println("No GIF reader found for: " + gifPath);
                stream.close();
                gifStream.close();
                return;
            }
            reader.setInput(stream);
            int frameCount = 0;
            try {
                frameCount = reader.getNumImages(true);
            } catch (Exception e) {
                System.out.println("Error reading GIF frame count: " + gifPath);
                reader.dispose();
                stream.close();
                gifStream.close();
                return;
            }
            for (int i = 0; i < frameCount; i++)
            {
                BufferedImage frame = null;
                try {
                    frame = reader.read(i);
                } catch (Exception e) {
                    System.out.println("Error reading GIF frame " + i + " of " + gifPath + ": " + e.getMessage());
                    continue;
                }
                originalFrames.add(frame);
                if (i == 0) {
                    originalWidth = frame.getWidth();
                    originalHeight = frame.getHeight();
                }
                int delay = 100;
                try
                {
                    String delayStr = reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0")
                            .getChildNodes().item(0).getAttributes().getNamedItem("delayTime").getNodeValue();
                    delay = Integer.parseInt(delayStr) * 10;
                }
                catch (Exception e)
                {
                }
                delays.add(delay);
            }
            reader.dispose();
            stream.close();
            gifStream.close();
            generateScaledTextures();
            isLoaded = true;
        }
        catch (IOException e)
        {
            System.out.println("Error loading GIF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BufferedImage scaleImage(BufferedImage original, int newWidth, int newHeight)
    {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    private void generateScaledTextures()
    {
        frames.clear();
        frameLocations.clear();
        frameWidths.clear();
        frameHeights.clear();

        int scaledWidth = (int)(originalWidth * scale);
        int scaledHeight = (int)(originalHeight * scale);

        for (int i = 0; i < originalFrames.size(); i++)
        {
            BufferedImage scaledFrame = scaleImage(originalFrames.get(i), scaledWidth, scaledHeight);
            DynamicTexture texture = new DynamicTexture(scaledFrame);
            frames.add(texture);

            frameWidths.add(scaledWidth);
            frameHeights.add(scaledHeight);
            ResourceLocation location = new ResourceLocation("parkourmod", "gif_frame_" + i + "_" + Math.round(scale * 100));
            Minecraft.getMinecraft().getTextureManager().loadTexture(location, texture);
            frameLocations.add(location);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && isLoaded && !frames.isEmpty() && PracticeMode.isPracticeModeEnabled())        {
            int screenWidth = event.resolution.getScaledWidth();

            int scaledWidth = frameWidths.get(0);
            int scaledHeight = frameHeights.get(0);

            int x = screenWidth - scaledWidth - 10;
            int y = 10;

            renderGif(x, y, scaledWidth, scaledHeight);
        }
    }
    public void setScale(float newScale)
    {
        this.scale = Math.max(0.1f, Math.min(2.0f, newScale));

        if (isLoaded && !originalFrames.isEmpty())
        {
            generateScaledTextures();
        }
    }

    public float getScale()
    {
        return this.scale;
    }

    public void renderGif(int x, int y, int width, int height)
    {
        if (!isLoaded || frames.isEmpty()) return;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFrameTime >= delays.get(currentFrame))
        {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(frameLocations.get(currentFrame));

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int frameWidth = frameWidths.get(currentFrame);
        int frameHeight = frameHeights.get(currentFrame);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, frameWidth, frameHeight);

        GlStateManager.disableBlend();
    }
}

