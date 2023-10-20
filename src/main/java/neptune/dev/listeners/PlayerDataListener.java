package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.types.Stat;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.HashMap;

public class PlayerDataListener implements Listener {

    public static HashMap<Player, Stat> stats = new HashMap<>();


    public static Stat getStats(OfflinePlayer player) {
        return stats.get(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (ConfigManager.databaseConfig.get(player.getUniqueId().toString()) == null) {
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".kills", 0);
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".deaths", 0);
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".matches", 0);
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".wins", 0);
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".losses", 0);
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".elo", ConfigManager.pluginConfig.getInt("general.starting-elo"));
            ConfigManager.databaseConfig.set(player.getUniqueId().toString() + ".settings.kill-effect", "none");

            try {
                ConfigManager.databaseConfig.save(ConfigManager.database);
                ConfigManager.databaseConfig.load(ConfigManager.database);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        stats.put(event.getPlayer(), new Stat(player));
    }

}