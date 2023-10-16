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
        if (ConfigManager.statsConfig.get(player.getUniqueId().toString()) == null) {
            ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".kills", 0);
            ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".deaths", 0);
            ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".matches", 0);
            ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".wins", 0);
            ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".losses", 0);
            ConfigManager.statsConfig.set(player.getUniqueId().toString() + ".elo", ConfigManager.pluginConfig.getInt("general.starting-elo"));

            try {
                ConfigManager.statsConfig.save(ConfigManager.stats);
                ConfigManager.statsConfig.load(ConfigManager.stats);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        stats.put(event.getPlayer(), new Stat(player));
    }

}