package neptune.dev.game;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Match {

    Player player1;
    Player player2;
    String arenaName;
    String kitName;
    String matchID;

    public Match(Player player1, Player player2, String arenaName, String kitName, String matchID) {
        this.player1 = player1;
        this.player2 = player2;
        this.arenaName = arenaName;
        this.kitName = kitName;
        this.matchID = UUID.randomUUID().toString();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public String getArenaName() {
        return arenaName;
    }

    public String getKitName() {
        return kitName;
    }

    public String getMatchID() {
        return matchID;
    }
}
