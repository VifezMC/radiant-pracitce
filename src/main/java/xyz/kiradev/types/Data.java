package xyz.kiradev.types;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.KitManager;

public class Data {

    private Player player;
    private int matches;
    private int wins;
    private int losses;
    private int elo;
    private String killeffect;


    public Data(Player player) {
        this.player = player;
        this.matches = ConfigManager.flatfileConfig.getInt(player.getUniqueId().toString() + ".matches");
        this.wins = ConfigManager.flatfileConfig.getInt(player.getUniqueId().toString() + ".wins");
        this.losses = ConfigManager.flatfileConfig.getInt(player.getUniqueId().toString() + ".losses");
        this.elo = ConfigManager.flatfileConfig.getInt(player.getUniqueId().toString() + ".elo");
        this.killeffect = ConfigManager.flatfileConfig.getString(player.getUniqueId().toString() + ".settings.kill-effect");
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

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getELO() {
        return elo;
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

    public String getKilleffect() {
        return killeffect;
    }

    public void setKilleffect(String effect) {
        this.killeffect = effect;
    }

    public ItemStack[] getKitItems(String kitName) {
        return KitManager.getKit(kitName).getItems();
    }
}