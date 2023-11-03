package xyz.kiradev.listeners;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
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

    public static HashMap<Player, Data> stats = new HashMap<>();


    public static Data getStats(OfflinePlayer player) {
        return stats.get(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (ConfigManager.flatfileConfig.get(player.getUniqueId().toString()) == null) {
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".kills", 0);
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".deaths", 0);
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".matches", 0);
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".wins", 0);
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".losses", 0);
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".elo", ConfigManager.pluginConfig.getInt("general.starting-elo"));
            ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".settings.kill-effect", "none");
            for (Kit kit : ConfigManager.kitManager.getKits()) {
                ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".kiteditor." + kit.getName(), "none");
            }

            try {
                ConfigManager.flatfileConfig.save(ConfigManager.database);
                ConfigManager.flatfileConfig.load(ConfigManager.database);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        stats.put(event.getPlayer(), new Data(player));
    }

}