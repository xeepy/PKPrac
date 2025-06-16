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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Notifications {
    private static final List<Notification> NOTIFICATIONS = new ArrayList<Notification>();
    private static final int MAX_NOTIFICATIONS = 3;
    private static final int NOTIFICATION_LIFETIME = 5000;
    private static final int NOTIFICATION_FADE_TIME = 500;

    public static void add(String message) {
        add(message, NotificationType.INFO);
    }

    public static void add(String message, NotificationType type) {
        synchronized (NOTIFICATIONS) {
            if (NOTIFICATIONS.size() >= MAX_NOTIFICATIONS) {
                NOTIFICATIONS.remove(0);
            }
            NOTIFICATIONS.add(new Notification(message, type));
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        synchronized (NOTIFICATIONS) {
            Iterator<Notification> iterator = NOTIFICATIONS.iterator();
            while (iterator.hasNext()) {
                Notification notification = iterator.next();
                if (notification.isExpired()) {
                    iterator.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(Post event) {
        if (event.type != ElementType.TEXT) return;
        synchronized (NOTIFICATIONS) {
            if (NOTIFICATIONS.isEmpty()) return;
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            int screenWidth = res.getScaledWidth();
            int screenHeight = res.getScaledHeight();
            int y = screenHeight - 20;
            for (int i = NOTIFICATIONS.size() - 1; i >= 0; i--) {
                Notification notification = NOTIFICATIONS.get(i);
                int x = screenWidth - notification.getWidth() - 10;
                y -= notification.getHeight() + 5;
                notification.render(x, y);
            }
        }
    }

    public enum NotificationType {
        INFO(new Color(255, 255, 153)),
        SUCCESS(new Color(204, 255, 204)),
        WARNING(new Color(255, 204, 153)),
        ERROR(new Color(255, 153, 153)),
        DISABLED(new Color(230, 230, 230));

        private final Color color;

        NotificationType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private static class Notification {
        private static final int MAX_WIDTH = 300;
        private final List<String> lines;
        private final NotificationType type;
        private final long creationTime;
        private final int width;
        private final int height;

        public Notification(String message, NotificationType type) {
            this.type = type;
            this.creationTime = System.currentTimeMillis();
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            this.lines = wordWrap(message, fontRenderer, MAX_WIDTH - 20);
            int maxLineWidth = 0;
            for (String line : lines) {
                maxLineWidth = Math.max(maxLineWidth, fontRenderer.getStringWidth(line));
            }
            this.width = Math.max(120, Math.min(MAX_WIDTH, maxLineWidth + 24));
            this.height = 12 * lines.size() + 18;
        }

        private static List<String> wordWrap(String text, FontRenderer fontRenderer, int maxWidth) {
            List<String> result = new ArrayList<>();
            String[] words = text.split("\\s+");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                if (word.isEmpty()) continue;

                String testLine = line.length() == 0 ? word : line + " " + word;
                int testWidth = fontRenderer.getStringWidth(testLine);

                if (testWidth > maxWidth && line.length() > 0) {
                    result.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }

            if (line.length() > 0) {
                result.add(line.toString());
            }

            if (result.isEmpty()) {
                result.add(text.length() > 0 ? text : "");
            }

            return result;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - creationTime > NOTIFICATION_LIFETIME;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void render(int x, int y) {
            float alpha = 1.0F;
            long timeAlive = System.currentTimeMillis() - creationTime;

            if (timeAlive > NOTIFICATION_LIFETIME - NOTIFICATION_FADE_TIME) {
                alpha = 1.0F - (float) (timeAlive - (NOTIFICATION_LIFETIME - NOTIFICATION_FADE_TIME)) / NOTIFICATION_FADE_TIME;
            } else if (timeAlive < NOTIFICATION_FADE_TIME) {
                alpha = (float) timeAlive / NOTIFICATION_FADE_TIME;
            }

            if (alpha < 0.05F) {
                return;
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            Color noteColor = type.getColor();
            int bgAlpha = (int) (240 * alpha);

            Gui.drawRect(x, y, x + width, y + height,
                    new Color(noteColor.getRed(), noteColor.getGreen(), noteColor.getBlue(), bgAlpha).getRGB());

            Gui.drawRect(x + width, y + 2, x + width + 2, y + height + 2,
                    new Color(0, 0, 0, (int) (50 * alpha)).getRGB());
            Gui.drawRect(x + 2, y + height, x + width + 2, y + height + 2,
                    new Color(0, 0, 0, (int) (50 * alpha)).getRGB());

            int cornerSize = 12;
            Gui.drawRect(x + width - cornerSize, y, x + width, y + cornerSize,
                    new Color(0, 0, 0, (int) (40 * alpha)).getRGB());
            int darkerRed = Math.max(0, noteColor.getRed() - 40);
            int darkerGreen = Math.max(0, noteColor.getGreen() - 40);
            int darkerBlue = Math.max(0, noteColor.getBlue() - 40);
            Gui.drawRect(x + width - cornerSize + 2, y + 2, x + width - 2, y + cornerSize - 2,
                    new Color(darkerRed, darkerGreen, darkerBlue, bgAlpha).getRGB());

            if (lines.size() > 1) {
                int lineAlpha = (int) (20 * alpha);
                Color lineColor = new Color(noteColor.getRed() - 60, noteColor.getGreen() - 60, noteColor.getBlue() - 60, lineAlpha);
                for (int i = 1; i < lines.size(); i++) {
                    int lineY = y + 12 + (i * 12);
                    Gui.drawRect(x + 8, lineY + 9, x + width - 16, lineY + 10, lineColor.getRGB());
                }
            }

            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            int textY = y + 10;
            int textColor = new Color(50, 50, 50, (int) (255 * alpha)).getRGB();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                    int textX = x + 12 + (int)(Math.sin((textY + i) * 0.3) * 0.8);
                fontRenderer.drawString(line, textX, textY, textColor);
                textY += 12;
            }

            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
    }
}
