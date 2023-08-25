package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.game.StartGame;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
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

        Arena selectedArena = selectRandomAvailableArena(arenas);
        if (selectedArena == null) {
            firstPlayer.sendMessage(CC.translate("&cNo available arenas found."));
            secondPlayer.sendMessage(CC.translate("&cNo available arenas found."));
            newQueue.remove(firstPlayer);
            newQueue.remove(secondPlayer);
            return;
        }

        selectedArena.setAvailable(false);
        newQueue.remove(firstPlayer);
        newQueue.remove(secondPlayer);
        MatchManager.addMatch(firstPlayer, secondPlayer, selectedArena, kitName);

        firstPlayer.teleport(selectedArena.getSpawn1());
        secondPlayer.teleport(selectedArena.getSpawn2());

        PlayerUtils.removeState(firstPlayer, PlayerState.LOBBY);
        PlayerUtils.setState(firstPlayer, PlayerState.PLAYING);
        PlayerUtils.removeState(secondPlayer, PlayerState.LOBBY);
        PlayerUtils.setState(secondPlayer, PlayerState.PLAYING);

        processQueueForKit(kitName, players);

        if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Removing " + firstPlayer.getName() + " and " + secondPlayer.getName() + " from the queue.");
        }

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

    private static Arena selectRandomAvailableArena(List<String> arenas) {
        List<Arena> availableArenas = new ArrayList<>();
        for (String arenaName : arenas) {
            Arena arena = ArenaManager.getByName(arenaName);
            if (arena != null && arena.isAvailable()) {
                availableArenas.add(arena);
            }
        }
        if (availableArenas.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return availableArenas.get(random.nextInt(availableArenas.size()));
    }
}
