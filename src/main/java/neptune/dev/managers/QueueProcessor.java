package neptune.dev.managers;

import neptune.dev.game.StartGame;
import neptune.dev.utils.Console;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QueueProcessor {

    private static List<Player> queue = new ArrayList<>();
    private static List<String> playerKit = new ArrayList<>();

    public static void addPlayerToQueue(Player player, String kitName) {
        if (queue.contains(player)) {
            player.sendMessage("You are already in a queue! Use /leavequeue or use the item in your inventory to leave the queue.");
            return;
        } else {
            queue.add(player);
        }

        playerKit.add(player.getName() + ":" + kitName);

        if (queue.size() >= 2 && playerKit.size() >= 2) {
            if (playerKit.get(0).split(":")[1].equals(playerKit.get(1).split(":")[1])) {
                Console.sendMessage("Match found between " + queue.get(0).getName() + " and " + queue.get(1).getName());
                processQueueForKit(playerKit.get(0).split(":")[1], queue);
                Console.sendMessage("Removing " + queue.get(0).getName() + " and " + queue.get(1).getName() + " from the queue.");

                Player firstPlayer = queue.get(0);
                Player secondPlayer = queue.get(1);

                playerKit.remove(firstPlayer.getName() + ":" + getPlayerKitName(firstPlayer));
                playerKit.remove(secondPlayer.getName() + ":" + getPlayerKitName(secondPlayer));

                queue.remove(firstPlayer);
                queue.remove(secondPlayer);
                MatchManager.addMatch(firstPlayer, secondPlayer, "arena", getPlayerKitName(firstPlayer));
            }
        }

    }

    public static void removePlayerFromQueue(Player player) {
        if (queue.contains(player)) {
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
                    StartGame.StartGame(kitName, players);
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
