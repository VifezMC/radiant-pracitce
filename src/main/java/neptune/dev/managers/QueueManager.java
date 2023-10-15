package neptune.dev.managers;

import neptune.dev.listeners.PlayerDataListener;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.types.Arena;
import neptune.dev.types.Game;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static neptune.dev.player.PlayerUtils.createSpawnItems;
import static neptune.dev.player.PlayerUtils.getLobbyLocation;

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
                startMatch(players);
                KitManager.getKit(kitName).removeQueue(2);
            }
        }
    }

    private static void startMatch(List<Player> players) {
        Player firstPlayer = players.get(0);
        Player secondPlayer = players.get(1);
        String kitName = Queue.get(firstPlayer);

        firstPlayer.getInventory().clear();
        secondPlayer.getInventory().setArmorContents(null);

        if (ConfigManager.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Match found between " + firstPlayer.getName() + " and " + secondPlayer.getName());
        }


        if (KitManager.getKit(kitName).getArenas().isEmpty()) {
            firstPlayer.sendMessage(CC.translate("&cNo arenas found."));
            secondPlayer.sendMessage(CC.translate("&cNo arenas found."));
            firstPlayer.teleport(getLobbyLocation());
            secondPlayer.teleport(getLobbyLocation());
            Queue.remove(firstPlayer);
            Queue.remove(secondPlayer);
            firstPlayer.teleport(getLobbyLocation());
            firstPlayer.getInventory().clear();
            createSpawnItems(firstPlayer);
            firstPlayer.updateInventory();

            secondPlayer.getInventory().clear();
            createSpawnItems(secondPlayer);
            secondPlayer.updateInventory();
            secondPlayer.teleport(getLobbyLocation());
            return;
        }

        Arena selectedArena = selectRandomAvailableArena(KitManager.getKit(kitName).getArenas());
        if (selectedArena == null) {
            firstPlayer.sendMessage(CC.translate("&cNo available arenas found."));
            secondPlayer.sendMessage(CC.translate("&cNo available arenas found."));

            Queue.remove(firstPlayer);
            Queue.remove(secondPlayer);

            firstPlayer.getInventory().clear();
            createSpawnItems(firstPlayer);
            firstPlayer.updateInventory();
            firstPlayer.teleport(getLobbyLocation());

            secondPlayer.getInventory().clear();
            createSpawnItems(secondPlayer);
            secondPlayer.updateInventory();
            secondPlayer.teleport(getLobbyLocation());
            return;
        }

        selectedArena.setAvailable(false);
        Queue.remove(firstPlayer);
        Queue.remove(secondPlayer);
        PlayerDataListener.getStats(firstPlayer).addMatches();
        PlayerDataListener.getStats(secondPlayer).addMatches();

        MatchManager.addMatch(firstPlayer, secondPlayer, selectedArena, kitName);

        firstPlayer.teleport(selectedArena.getSpawn1());
        secondPlayer.teleport(selectedArena.getSpawn2());

        PlayerUtils.setState(firstPlayer, PlayerState.PLAYING);
        PlayerUtils.setState(secondPlayer, PlayerState.PLAYING);

        processQueueForKit(kitName, players);

        if (ConfigManager.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Removing " + firstPlayer.getName() + " and " + secondPlayer.getName() + " from the queue.");
        }

        playing += 2;
        KitManager.getKit(kitName).addPlaying(2);
        String opponentMessage = ConfigManager.messagesConfig.getString("match.match-found")
                .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(firstPlayer).getName()))
                .replace("{opponent-ping}", PlayerUtils.getPing(MatchManager.getOpponent(firstPlayer)) + "")
                .replace("{kit}", MatchManager.getMatch(firstPlayer).getKitName())
                .replace("{arena}", MatchManager.getArena(MatchManager.getMatchID(firstPlayer)).getName());
        firstPlayer.sendMessage(CC.translate(opponentMessage));

        String opponentMessage2 = ConfigManager.messagesConfig.getString("match.match-found")
                .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(secondPlayer).getName()))
                .replace("{opponent-ping}", PlayerUtils.getPing(MatchManager.getOpponent(secondPlayer)) + "")
                .replace("{kit}", MatchManager.getMatch(firstPlayer).getKitName())
                .replace("{arena}", MatchManager.getArena(MatchManager.getMatchID(secondPlayer)).getName());
        secondPlayer.sendMessage(CC.translate(opponentMessage2));
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

    private static void processQueueForKit(String kitName, List<Player> players) {
        players.forEach(player -> Game.startGame(kitName, players));
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
