package xyz.kiradev.listeners;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.types.Data;
import xyz.kiradev.types.Kit;

import java.io.IOException;
import java.util.HashMap;

public class PlayerDataListener implements Listener {

    private static final HashMap<Player, Data> stats = new HashMap<>();

    public static Data getStats(OfflinePlayer player) {
        return stats.get(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();

        if (ConfigManager.databaseConfig.get(playerId) == null) {
            setDefaultPlayerData(playerId);
            saveAndLoadConfig();
        }

        stats.put(player, new Data(player));
    }

    private void setDefaultPlayerData(String playerId) {
        ConfigManager.databaseConfig.set(playerId + ".kills", 0);
        ConfigManager.databaseConfig.set(playerId + ".deaths", 0);
        ConfigManager.databaseConfig.set(playerId + ".matches", 0);
        ConfigManager.databaseConfig.set(playerId + ".wins", 0);
        ConfigManager.databaseConfig.set(playerId + ".losses", 0);
        ConfigManager.databaseConfig.set(playerId + ".elo", ConfigManager.pluginConfig.getInt("general.starting-elo"));
        ConfigManager.databaseConfig.set(playerId + ".settings.kill-effect", "none");
        for (Kit kit : ConfigManager.kitManager.getKits()) {
            ConfigManager.databaseConfig.set(playerId + ".kiteditor." + kit.getName(), "none");
        }
    }

    private void saveAndLoadConfig() {
        try {
            ConfigManager.databaseConfig.save(ConfigManager.database);
            ConfigManager.databaseConfig.load(ConfigManager.database);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}