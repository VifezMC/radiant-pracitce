package xyz.kiradev.utils;

import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.utils.render.Console;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordUtils {
    public static void sendMatchStart(String player1, String player2, String arena, String kit) {
        try {

            URL url = new URL(ConfigManager.pluginConfig.getString("discord.webhook"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonBody = "{"
                    + "\"content\": \"\","
                    + "\"embeds\": [{"
                    + "\"title\": \"" + player1 + " vs " + player2 + "\","
                    + "\"description\": \"" + kit + " Match\\nArena: " + arena + "\","
                    + "\"color\": 1752220"
                    + "}]"
                    + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (!(responseCode == 204)) {
                Console.sendError("Failed to send webhook");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Console.sendError("Failed to send webhook");
        }
    }

    public static void sendMatchEnd(String winner, String loser) {
        try {
            URL url = new URL(ConfigManager.pluginConfig.getString("discord.webhook"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonBody = "{"
                    + "\"content\": \"\","
                    + "\"embeds\": [{"
                    + "\"title\": \"Match Ended\","
                    + "\"description\": \"Winner: **" + winner + "**\\nLoser: **" + loser + "**\","
                    + "\"color\": 1752220"
                    + "}]"
                    + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (!(responseCode == 204)) {
                Console.sendError("Failed to send webhook");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Console.sendError("Failed to send webhook");
        }
    }
}