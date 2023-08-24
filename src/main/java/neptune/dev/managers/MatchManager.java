package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.game.Match;
import neptune.dev.utils.render.Console;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {

    private static final boolean DEBUG_ENABLED = Neptune.pluginConfig.getBoolean("general.enable-debug");
    private static final Map<UUID, Match> matches = new ConcurrentHashMap<>();
    private static final Map<UUID, Arena> matchArenas = new ConcurrentHashMap<>();

    private static boolean isCreative(Player player) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    public static void addMatch(Player player1, Player player2, Arena arenaName, String kitName) {
        UUID matchID = UUID.randomUUID();
        Match match = new Match(player1, player2, arenaName, kitName, matchID);
        matches.put(matchID, match);
        matchArenas.put(matchID, arenaName);
        if (DEBUG_ENABLED) {
            Console.sendMessage("Match added between " + player1.getName() + " and " + player2.getName() + " on arena " + arenaName.getName() + " with kit " + kitName);
        }
    }

    public static void removeMatch(UUID matchID) {
        Match match = matches.remove(matchID);
        if (match != null && DEBUG_ENABLED) {
            Console.sendMessage("Match removed between " + match.getPlayer1().getName() + " and " + match.getPlayer2().getName() + " on arena " + match.getArenaName().getName() + " with kit " + match.getKitName());
        }
        matchArenas.remove(matchID);
    }

    public static UUID getMatchID(Player player) {
        for (Map.Entry<UUID, Match> entry : matches.entrySet()) {
            Match match = entry.getValue();
            if (match.involvesPlayer(player)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Match getMatch(Player player) {
        for (Match match : matches.values()) {
            if (match.involvesPlayer(player)) {
                return match;
            }
        }
        return null;
    }

    public static Arena getArena(UUID matchID) {
        return matchArenas.get(matchID);
    }

    public static String getOpponent(Player player) {
        for (Match match : matches.values()) {
            if (match.involvesPlayer(player)) {
                return match.getOtherPlayer(player).getName();
            }
        }
        return null;
    }

    public static List<Player> getMatchPlayers(UUID matchID) {
        Match match = matches.get(matchID);
        if (match != null) {
            return Arrays.asList(match.getPlayer1(), match.getPlayer2());
        }
        return null;
    }

    public static Player getWinner(UUID matchID) {
        Match match = matches.get(matchID);
        if (match != null) {
            Player player1 = match.getPlayer1();
            Player player2 = match.getPlayer2();

            if (isCreative(player1) && !isCreative(player2)) {
                return player2;
            }

            if (!isCreative(player1) && isCreative(player2)) {
                return player1;
            }
        }
        return null;
    }

    public static Player getLoser(UUID matchID) {
        Match match = matches.get(matchID);
        if (match != null) {
            Player player1 = match.getPlayer1();
            Player player2 = match.getPlayer2();

            if (isCreative(player1) && !isCreative(player2)) {
                return player1;
            }

            if (!isCreative(player1) && isCreative(player2)) {
                return player2;
            }
        }
        return null;
    }
}