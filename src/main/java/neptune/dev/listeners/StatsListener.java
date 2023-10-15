package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.types.Stats;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class StatsListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Stats stats = PlayerDataListener.getStats(player);
        if (stats == null) return;
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".matches", stats.getMatches());
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".wins", stats.getWins());
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".losses", stats.getLosses());
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".elo", stats.getELO());
        try {
            ConfigManager.statsConfig.save(ConfigManager.stats);
            ConfigManager.statsConfig.load(ConfigManager.stats);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}