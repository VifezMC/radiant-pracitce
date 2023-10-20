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
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".matches", stat.getMatches());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".wins", stat.getWins());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".losses", stat.getLosses());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".elo", stat.getELO());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".settings.kill-effect", stat.getKilleffect());

        try {
            ConfigManager.databaseConfig.save(ConfigManager.database);
            ConfigManager.databaseConfig.load(ConfigManager.database);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}