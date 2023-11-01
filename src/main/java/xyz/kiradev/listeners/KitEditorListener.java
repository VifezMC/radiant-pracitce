package xyz.kiradev.listeners;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.kiradev.commands.user.KitEditorCMD;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.InventoryManager;
import xyz.kiradev.player.PlayerState;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.Console;

import java.io.IOException;
import java.util.Arrays;

public class KitEditorListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if (PlayerUtils.hasPlayerState(p, PlayerState.KITEDITOR)) {
            p.getInventory();
            ConfigManager.databaseConfig.set(p.getUniqueId() + ".kiteditor." + KitEditorCMD.kiteditor.get(p).getName(), Arrays.asList(p.getInventory().getContents()));
            p.getInventory().clear();
            PlayerUtils.setState(p, PlayerState.LOBBY);
            InventoryManager.createSpawnItems(p);
            try {
                ConfigManager.databaseConfig.save(ConfigManager.database);
                ConfigManager.databaseConfig.load(ConfigManager.database);
            } catch (IOException | InvalidConfigurationException e) {
                Console.sendError("Error occurred while saving kit-editor");
            }
            p.sendMessage(CC.translate(ConfigManager.messagesConfig.getString("general.kit-saved")));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (PlayerUtils.hasPlayerState(p, PlayerState.KITEDITOR)) {
            if (event.getAction().toString().contains("RIGHT_CLICK")) {
                event.setCancelled(true);
            }
        }
    }
}
