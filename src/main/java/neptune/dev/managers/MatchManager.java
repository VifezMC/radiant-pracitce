package neptune.dev.managers;

import neptune.dev.types.Arena;
import neptune.dev.types.Match;
import neptune.dev.utils.render.Console;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {

    private static Map<UUID, Match> matches = new ConcurrentHashMap<>();
    private static Map<UUID, Arena> matchArenas = new ConcurrentHashMap<>();

    public static void addMatch(Player player1, Player player2, Arena arenaName, String kitName) {
        UUID matchID = UUID.randomUUID();
        Match match = new Match(null, null, player1, player2, arenaName, kitName, matchID);
        matches.put(matchID, match);
        matchArenas.put(matchID, arenaName);
        if (ConfigManager.pluginConfig.getBoolean("general.enable-debug")) {
            Console.sendMessage("Match added between " + player1.getName() + " and " + player2.getName() + " on arena " + arenaName.getName() + " with kit " + kitName);
        }
    }

    public static void removeMatch(UUID matchID) {
        Match match = matches.remove(matchID);
        if (match != null) {
            if (ConfigManager.pluginConfig.getBoolean("general.enable-debug")) {
                Console.sendMessage("Match removed between " + match.getPlayer1().getName() + " and " + match.getPlayer2().getName() + " on arena " + match.getArena().getName() + " with kit " + match.getKitName());
            }
        }
        matchArenas.remove(matchID);
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

    public static Arena getArena(UUID matchID) {
        return matchArenas.get(matchID);
    }

    public static Player getOpponent(Player player) {
        for (Match match : matches.values()) {
            if (match.getPlayer1().getUniqueId().equals(player.getUniqueId())) {
                return match.getPlayer2();
            } else if (match.getPlayer2().getUniqueId().equals(player.getUniqueId())) {
                return match.getPlayer1();
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
