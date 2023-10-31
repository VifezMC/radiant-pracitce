package xyz.kiradev;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import xyz.kiradev.listeners.PlayerDataListener;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.types.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Placeholder extends PlaceholderExpansion {

    private final Stellar plugin;

    public Placeholder(Stellar plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "neptune";
    }

    @Override
    public String getAuthor() {
        return Constants.Author;
    }

    @Override
    public String getVersion() {
        return Constants.Ver + "";
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled("Stellar");
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) {
            return "";
        }

        switch (identifier) {
            case "elo":
                return getPlayerELO(player) + "";
            case "division":
                return getPlayerDivision(player);
            default:
                return null;
        }
    }

    private int getPlayerELO(OfflinePlayer p) {
        Data data = PlayerDataListener.getStats(p);
        return data.getELO();
    }
    private String getPlayerDivision(OfflinePlayer p) {
        Data data = PlayerDataListener.getStats(p);
        return ConfigManager.divisionsManager.getPlayerDivision(data.getELO());
    }
}