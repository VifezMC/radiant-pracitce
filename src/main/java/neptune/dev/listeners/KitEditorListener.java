package neptune.dev.listeners;

import neptune.dev.commands.user.KitEditorCMD;
import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.InventoryManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;

public class KitEditorListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if(PlayerUtils.hasPlayerState(p, PlayerState.KITEDITOR)){
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
