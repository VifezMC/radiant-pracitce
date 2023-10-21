package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.types.Data;
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
        Data data = PlayerDataListener.getStats(player);
        if (data == null) return;
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".matches", data.getMatches());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".wins", data.getWins());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".losses", data.getLosses());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".elo", data.getELO());
        ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".settings.kill-effect", data.getKilleffect());

        try {
            ConfigManager.databaseConfig.save(ConfigManager.database);
            ConfigManager.databaseConfig.load(ConfigManager.database);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}