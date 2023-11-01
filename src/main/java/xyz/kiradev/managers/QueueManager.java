package xyz.kiradev.managers;

import org.bukkit.entity.Player;
import xyz.kiradev.utils.render.CC;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    public static Map<Player, String> Queue = new ConcurrentHashMap<>();
    public static int playing;

    public static void addPlayerToQueue(Player player, String kitName) {
        if (isPlayerInQueue(player)) {
            player.sendMessage(CC.translate(ConfigManager.messagesConfig.getString("queue.already-in-queue-message")));
            return;
        }

        Queue.put(player, kitName);
        KitManager.getKit(kitName).addQueue(1);

        if (Queue.size() >= 2) {
            List<Player> players = new ArrayList<>(Queue.keySet());
            Set<String> kitNames = new HashSet<>(Queue.values());

            if (kitNames.size() == 1) {
                GameManager.startGame(players);
                KitManager.getKit(kitName).removeQueue(2);
            }
        }
    }

    public static void removePlayerFromQueue(Player player) {
        if (isPlayerInQueue(player)) {
            Queue.remove(player);
        } else {
            player.sendMessage(CC.translate(ConfigManager.messagesConfig.getString("queue.not-in-queue")));
        }
    }

    public static boolean isPlayerInQueue(Player player) {
        return Queue.containsKey(player);
    }
}
