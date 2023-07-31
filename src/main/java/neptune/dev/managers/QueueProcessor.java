package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.game.StartGame;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueProcessor {

    private static final Map<Player, String> newQueue = new ConcurrentHashMap<>();
    public static int playing;

    public static void addPlayerToQueue(Player player, String kitName) {
        if (isPlayerInQueue(player)) {
            player.sendMessage(CC.translate(Neptune.messagesConfig.getString("queue.already-in-queue-message")));
            return;
        }

        newQueue.put(player, kitName);

        if (newQueue.size() >= 2) {
            List<Player> players = new ArrayList<>(newQueue.keySet());
            Set<String> kitNames = new HashSet<>(newQueue.values());

            if (kitNames.size() == 1) {
                startMatch(players);
            }
        }
    }

    private static void startMatch(List<Player> players) {
        Player firstPlayer = players.get(0);
        Player secondPlayer = players.get(1);
        String kitName = newQueue.get(firstPlayer);

        firstPlayer.getInventory().clear();
        secondPlayer.getInventory().setArmorContents(null);

        if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Match found between " + firstPlayer.getName() + " and " + secondPlayer.getName());
        }

        List<String> arenas = Neptune.kitsConfig.getStringList("kits." + kitName + ".arenas");

        if (arenas.isEmpty()) {
            firstPlayer.sendMessage(CC.translate("&cNo arenas found."));
            secondPlayer.sendMessage(CC.translate("&cNo arenas found."));
            newQueue.remove(firstPlayer);
            newQueue.remove(secondPlayer);
            return;
        }

        Collections.shuffle(arenas);
        String selectedArena = arenas.get(0);
        Arena a = ArenaManager.getByName(selectedArena);

        MatchManager.addMatch(firstPlayer, secondPlayer, a, kitName);

        firstPlayer.teleport(a.getSpawn1());
        secondPlayer.teleport(a.getSpawn2());

        PlayerUtils.removeState(firstPlayer, PlayerState.LOBBY);
        PlayerUtils.setState(firstPlayer, PlayerState.PLAYING);
        PlayerUtils.removeState(secondPlayer, PlayerState.LOBBY);
        PlayerUtils.setState(secondPlayer, PlayerState.PLAYING);

        processQueueForKit(kitName, players);

        if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Removing " + firstPlayer.getName() + " and " + secondPlayer.getName() + " from the queue.");
        }

        newQueue.remove(firstPlayer);
        newQueue.remove(secondPlayer);
        playing += 2;

        String opponentMessage = Neptune.messagesConfig.getString("match.match-found")
                .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(firstPlayer)));
        firstPlayer.sendMessage(CC.translate(opponentMessage));

        String opponentMessage2 = Neptune.messagesConfig.getString("match.match-found")
                .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(secondPlayer)));
        secondPlayer.sendMessage(CC.translate(opponentMessage2));
    }

    public static void removePlayerFromQueue(Player player) {
        if (isPlayerInQueue(player)) {
            newQueue.remove(player);
        } else {
            player.sendMessage("You are not in a queue!");
        }
    }

    public static boolean isPlayerInQueue(Player player) {
        return newQueue.containsKey(player);
    }

    private static void processQueueForKit(String kitName, List<Player> players) {
        players.forEach(player -> StartGame.startGame(kitName, players));
    }
}
