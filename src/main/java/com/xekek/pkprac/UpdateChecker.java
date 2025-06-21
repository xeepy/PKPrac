package com.xekek.pkprac;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/xeepy/PKPrac/tags";
    private static boolean checkedForUpdate = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !checkedForUpdate && Minecraft.getMinecraft().thePlayer != null) {
            checkedForUpdate = true;
            new Thread(this::checkForUpdates).start();
        }
    }

    private void checkForUpdates() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonResponse = new JsonParser().parse(response.toString()).getAsJsonArray().get(0).getAsJsonObject();
            String latestVersion = jsonResponse.get("name").getAsString().replace("v", "");

            String currentVersion = Main.VERSION;

            if (isVersionNewer(currentVersion, latestVersion)) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.YELLOW + "[PKPrac] " + EnumChatFormatting.RESET + "Update available: " +
                        EnumChatFormatting.GREEN + latestVersion + EnumChatFormatting.RESET + " (You have " +
                        EnumChatFormatting.RED + currentVersion + EnumChatFormatting.RESET + ")"
                ));
            }
        } catch (Exception e) {
            System.out.println("Failed to check for updates: " + e.getMessage());
        }
    }

    private boolean isVersionNewer(String current, String latest) {
        String[] currParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");
        for (int i = 0; i < Math.min(currParts.length, latestParts.length); i++) {
            int currNum = Integer.parseInt(currParts[i].replaceAll("[^0-9]", ""));
            int latestNum = Integer.parseInt(latestParts[i].replaceAll("[^0-9]", ""));
            if (latestNum > currNum) return true;
            if (latestNum < currNum) return false;
        }
        return latestParts.length > currParts.length;
    }
}
