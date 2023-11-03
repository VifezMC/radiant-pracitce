package xyz.kiradev.listeners;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.types.Data;

import java.io.IOException;

public class StatsListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Data data = PlayerDataListener.getStats(player);
        if (data == null) return;
        ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".matches", data.getMatches());
        ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".wins", data.getWins());
        ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".losses", data.getLosses());
        ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".elo", data.getELO());
        ConfigManager.flatfileConfig.set(player.getUniqueId().toString() + ".settings.kill-effect", data.getKilleffect());

        try {
            ConfigManager.flatfileConfig.save(ConfigManager.database);
            ConfigManager.flatfileConfig.load(ConfigManager.database);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}