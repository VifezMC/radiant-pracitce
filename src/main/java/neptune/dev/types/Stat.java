package neptune.dev.types;

import neptune.dev.managers.ConfigManager;
import org.bukkit.entity.Player;

public class Stat {

    private Player player;
    private int matches;
    private int wins;
    private int losses;
    private int elo;


    public Stat(Player player) {
        this.player = player;
        this.matches = ConfigManager.statsConfig.getInt(player.getUniqueId().toString() + ".matches");
        this.wins = ConfigManager.statsConfig.getInt(player.getUniqueId().toString() + ".wins");
        this.losses = ConfigManager.statsConfig.getInt(player.getUniqueId().toString() + ".losses");
        this.elo = ConfigManager.statsConfig.getInt(player.getUniqueId().toString() + ".elo");
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getWins() {
        return wins;
    }

    public int getELO() {
        return elo;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void addMatches() {
        this.matches += 1;
    }

    public void addWins() {
        this.wins += 1;
    }

    public void addLosses() {
        this.losses += 1;
    }

    public void addELO(byte elo) {
        this.elo += elo;
    }

    public void removeELO(byte elo) {
        this.elo -= elo;
    }
}