package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.StartGame;
import neptune.dev.utils.CC;
import neptune.dev.utils.Console;
import org.bukkit.entity.Player;

import java.util.*;

public class QueueProcessor {

    private static List<Player> queue = new LinkedList<>();
    private static Set<String> playerKit = new HashSet<>();
    public static int playing;

    public static void addPlayerToQueue(Player player, String kitName) {
        if (isPlayerInQueue(player)) {
            player.sendMessage(CC.translate(Neptune.messagesConfig.getString("queue.already-in-queue-message")));
            return;
        }

        queue.add(player);
        playerKit.add(player.getName() + ":" + kitName);

        if (queue.size() >= 2 && playerKit.size() >= 2) {
            String firstKit = getPlayerKitName(queue.get(0));
            String secondKit = getPlayerKitName(queue.get(1));
            if (firstKit != null && firstKit.equals(secondKit)) {
                Player firstPlayer = queue.get(0);
                Player secondPlayer = queue.get(1);
                firstPlayer.getInventory().clear();
                secondPlayer.getInventory().setArmorContents(null);
                if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
                    Console.sendMessage("Match found between " + firstPlayer.getName() + " and " + secondPlayer.getName());

                }
                MatchManager.addMatch(firstPlayer, secondPlayer, "test", firstKit);
                processQueueForKit(firstKit, queue);
                if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
                    Console.sendMessage("Removing " + firstPlayer.getName() + " and " + secondPlayer.getName() + " from the queue.");
                }
                playerKit.remove(firstPlayer.getName() + ":" + firstKit);
                playerKit.remove(secondPlayer.getName() + ":" + secondKit);
                queue.remove(firstPlayer);
                queue.remove(secondPlayer);
                QueueProcessor.playing = QueueProcessor.playing + 2;


                String formattingString = Neptune.messagesConfig.getString("match.match-found");
                String formattedMessage = formattingString.replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(firstPlayer)));
                firstPlayer.sendMessage(CC.translate(formattedMessage));
                String formattingString2 = Neptune.messagesConfig.getString("match.match-found");
                String formattedMessage2 = formattingString2.replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(secondPlayer)));
                secondPlayer.sendMessage(CC.translate(formattedMessage2));

            }
        }
    }

    public static void removePlayerFromQueue(Player player) {
        if (isPlayerInQueue(player)) {
            queue.remove(player);
            String playerKitString = player.getName() + ":" + getPlayerKitName(player);
            playerKit.remove(playerKitString);
        } else {
            player.sendMessage("You are not in a queue!");
        }
    }

    public static boolean isPlayerInQueue(Player player) {
        return queue.contains(player);
    }

    private static void processQueueForKit(String kitName, List<Player> players) {
        for (Player player : players) {
            for (Player otherPlayer : players) {
                if (player != otherPlayer) {
                    StartGame.startGame(kitName, players);
                }
            }
        }
    }

    private static String getPlayerKitName(Player player) {
        for (String kitInfo : playerKit) {
            String[] parts = kitInfo.split(":");
            if (parts.length == 2 && parts[0].equals(player.getName())) {
                return parts[1];
            }
        }
        return null;
    }
}