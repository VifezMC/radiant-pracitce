package neptune.dev.managers;

import neptune.dev.game.Match;
import neptune.dev.utils.Console;
import org.bukkit.entity.Player;

import java.util.*;

public class MatchManager {

    private static List<Match> matches = new ArrayList<>();

    public static void addMatch(Player player1, Player player2, String arenaName, String kitName) {
        matches.add(new Match(player1, player2, arenaName, kitName, UUID.randomUUID().toString()));
        Console.sendMessage("Match added between " + player1.getName() + " and " + player2.getName() + " on arena " + arenaName + " with kit " + kitName);
    }

    public static void removeMatch(UUID matchID) {
        Iterator<Match> iterator = matches.iterator();
        while (iterator.hasNext()) {
            Match match = iterator.next();
            if (match.getMatchID().equals(matchID.toString())) {
                iterator.remove();
                Console.sendMessage("Match removed between " + match.getPlayer1().getName() + " and " + match.getPlayer2().getName() + " on arena " + match.getArenaName() + " with kit " + match.getKitName());
            }
        }
    }



    public static UUID getMatchID(Player player) {
        for (Match match : matches) {
            if (match.getPlayer1().equals(player) || match.getPlayer2().equals(player)) {
                return UUID.fromString(match.getMatchID());
            }
        }
        return null;
    }

    public static Match getMatch(Player player) {
        for (Match match : matches) {
            if (match.getPlayer1().equals(player) || match.getPlayer2().equals(player)) {
                return match;
            }
        }
        return null;
    }
    public static String getOpponent(Player player) {
        for (Match match : matches) {
            if (match.getPlayer1().equals(player)) {
                return match.getPlayer2().getName();
            } else if (match.getPlayer2().equals(player)) {
                return match.getPlayer1().getName();
            }
        }
        return null;
    }

    public static List<Player> getMatchPlayers(UUID matchID) {
        for (Match match : matches) {
            if (match.getMatchID().equals(matchID.toString())) {
                List<Player> players = new ArrayList<>();
                players.add(match.getPlayer1());
                players.add(match.getPlayer2());
                return players;
            }
        }
        return null;
    }

}