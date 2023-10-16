package neptune.dev;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import neptune.dev.listeners.PlayerDataListener;
import neptune.dev.managers.ConfigManager;
import neptune.dev.types.Stat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Placeholder extends PlaceholderExpansion {

    private final Neptune plugin;

    public Placeholder(Neptune plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "neptune";
    }

    @Override
    public String getAuthor() {
        return Constants.Autor;
    }

    @Override
    public String getVersion() {
        return Constants.Ver + "";
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled("Neptune");
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
        Stat stat = PlayerDataListener.getStats(p);
        return stat.getELO();
    }
    private String getPlayerDivision(OfflinePlayer p) {
        Stat stat = PlayerDataListener.getStats(p);
        return ConfigManager.divisionsManager.getPlayerDivision(stat.getELO());
    }
}