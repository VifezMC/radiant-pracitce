package xyz.kiradev.types;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Match {

    private final Player player1;
    private final Player player2;
    private final Arena arenaName;
    private final String kitName;
    private final UUID matchID;
    private Player winner;
    private Player loser;


    public Match(Player winner, Player loser, Player player1, Player player2, Arena arenaName, String kitName, UUID matchID) {
        this.winner = winner;
        this.loser = loser;
        this.player1 = player1;
        this.player2 = player2;
        this.arenaName = arenaName;
        this.kitName = kitName;
        this.matchID = matchID;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public void setLoser(Player player) {
        if (involvesPlayer(player)) {
            this.loser = player;
            this.winner = getOtherPlayer(player);
        }
    }

    public Arena getArena() {
        return arenaName;
    }

    public String getKitName() {
        return kitName;
    }

    public UUID getMatchID() {
        return matchID;
    }

    public String getArenaNameAsString() {
        return arenaName.getName();
    }

    public boolean involvesPlayer(Player player) {
        return player1.getUniqueId().equals(player.getUniqueId()) || player2.getUniqueId().equals(player.getUniqueId());
    }

    public Player getOtherPlayer(Player player) {
        if (player1.getUniqueId().equals(player.getUniqueId())) {
            return player2;
        } else if (player2.getUniqueId().equals(player.getUniqueId())) {
            return player1;
        }
        return null;
    }

}
