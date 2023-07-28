package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.StartGame;
import neptune.dev.utils.CC;
import neptune.dev.utils.Console;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueProcessor {

    private static List<Player> queue = new ArrayList<>();
    private static Map<Player, String> playerKit = new HashMap<>();

    public static void addPlayerToQueue(Player player, String kitName) {
        if (isPlayerInQueue(player)) {
            player.sendMessage(CC.translate(Neptune.messagesConfig.getString("queue.already-in-queue-message")));
            return;
        }

        queue.add(player);
        playerKit.put(player, kitName);

        if (queue.size() >= 2) {
            processQueue();
        }
    }

    public static void removePlayerFromQueue(Player player) {
        if (isPlayerInQueue(player)) {
            queue.remove(player);
            playerKit.remove(player);
        } else {
            player.sendMessage("You are not in a queue!");
        }
    }

    public static boolean isPlayerInQueue(Player player) {
        return queue.contains(player);
    }

    private static void processQueue() {
        List<Player> matchedPlayers = new ArrayList<>();

        for (Player player : queue) {
            String kitName = playerKit.get(player);
            if (!matchedPlayers.contains(player)) {
                List<Player> matchingPlayers = new ArrayList<>();
                matchingPlayers.add(player);

                for (Player otherPlayer : queue) {
                    if (player != otherPlayer && playerKit.get(otherPlayer).equals(kitName)) {
                        matchingPlayers.add(otherPlayer);
                        matchedPlayers.add(otherPlayer);
                    }
                }

                if (matchingPlayers.size() >= 2) {
                    StartGame.StartGame(kitName, matchingPlayers);
                }
            }
        }

        matchedPlayers.forEach(playerKit::remove);
        queue.removeAll(matchedPlayers);
    }
}
