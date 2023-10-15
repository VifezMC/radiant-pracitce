package neptune.dev;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import neptune.dev.listeners.PlayerDataListener;
import neptune.dev.types.Stats;
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

        if (identifier.equals("elo")) {
            return getPlayerELO(player) + "";
        }
        return null;
    }

    private int getPlayerELO(OfflinePlayer p) {
        Stats stats = PlayerDataListener.getStats(p);
        return stats.getELO();
    }
}