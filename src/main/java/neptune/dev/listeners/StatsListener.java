package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.types.Stat;
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
        Stat stat = PlayerDataListener.getStats(player);
        if (stat == null) return;
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".matches", stat.getMatches());
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".wins", stat.getWins());
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".losses", stat.getLosses());
        ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".elo", stat.getELO());
        try {
            ConfigManager.statsConfig.save(ConfigManager.stats);
            ConfigManager.statsConfig.load(ConfigManager.stats);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}