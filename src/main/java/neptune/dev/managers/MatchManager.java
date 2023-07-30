package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.game.Match;
import neptune.dev.utils.render.Console;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {

    private static Map<UUID, Match> matches = new ConcurrentHashMap<>();

    public static void addMatch(Player player1, Player player2, Arena arenaName, String kitName) {
        UUID matchID = UUID.randomUUID();
        Match match = new Match(player1, player2, arenaName, kitName, matchID);
        matches.put(matchID, match);
        if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Match added between " + player1.getName() + " and " + player2.getName() + " on arena " + arenaName.getName() + " with kit " + kitName);
        }
    }

    public static void removeMatch(UUID matchID) {
        Match match = matches.remove(matchID);
        if (match != null) {
            if (Neptune.pluginConfig.getBoolean("general.enable-debug")) {
                Console.sendMessage("Match removed between " + match.getPlayer1().getName() + " and " + match.getPlayer2().getName() + " on arena " + match.getArenaName().getName() + " with kit " + match.getKitName());
            }
        }
    }

    public static UUID getMatchID(Player player) {
        for (Map.Entry<UUID, Match> entry : matches.entrySet()) {
            Match match = entry.getValue();
            if (match.getPlayer1().getUniqueId().equals(player.getUniqueId()) || match.getPlayer2().getUniqueId().equals(player.getUniqueId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Match getMatch(Player player) {
        for (Match match : matches.values()) {
            if (match.getPlayer1().getUniqueId().equals(player.getUniqueId()) || match.getPlayer2().getUniqueId().equals(player.getUniqueId())) {
                return match;
            }
        }
        return null;
    }

    public static String getOpponent(Player player) {
        for (Match match : matches.values()) {
            if (match.getPlayer1().getUniqueId().equals(player.getUniqueId())) {
                return match.getPlayer2().getName();
            } else if (match.getPlayer2().getUniqueId().equals(player.getUniqueId())) {
                return match.getPlayer1().getName();
            }
        }
        return null;
    }

    public static List<Player> getMatchPlayers(UUID matchID) {
        Match match = matches.get(matchID);
        if (match != null) {
            List<Player> players = new ArrayList<>();
            players.add(match.getPlayer1());
            players.add(match.getPlayer2());
            return players;
        }
        return null;
    }
}
