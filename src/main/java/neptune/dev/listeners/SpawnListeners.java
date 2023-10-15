package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.QueueManager;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static neptune.dev.player.PlayerUtils.hasPlayerState;
import static org.bukkit.Bukkit.getLogger;

public class SpawnListeners implements Listener {

    @EventHandler // DISABLING DAMAGE IN LOBBY
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (hasPlayerState(player, PlayerState.LOBBY) || hasPlayerState(player, PlayerState.INQUEUE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler // DISABLE ALL COMMAND WHILE IN QUEUE OR INGAME
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        Player p = event.getPlayer();
        if(hasPlayerState(p, PlayerState.INQUEUE) || hasPlayerState(p, PlayerState.PLAYING) && !command.equals("/leavequeue")){
            event.setCancelled(true);
            p.sendMessage(CC.translate("&cYou can't use commands while in-types or in queue."));
        }
    }

    @EventHandler // DISABLING FOOD DROP IN LOBBY
    public void onFoodLevel(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (hasPlayerState(player, PlayerState.LOBBY) || hasPlayerState(player, PlayerState.INQUEUE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler // DISABLE PLAYER ITEM DROPS
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (hasPlayerState(player, PlayerState.LOBBY) && player.getGameMode() != GameMode.CREATIVE || hasPlayerState(player, PlayerState.INQUEUE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler // REMOVE PLAYER FROM QUEUE IF THEY LEAVE
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        if (QueueManager.isPlayerInQueue(event.getPlayer())) {
            QueueManager.removePlayerFromQueue(event.getPlayer());
        }
        PlayerUtils.playerStates.remove(event.getPlayer());
        PlayerUtils.gameStates.remove(event.getPlayer());
    }

    @EventHandler // SPAWN ITEMS
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT_CLICK") && item != null) {
            for (String itemName : ConfigManager.spawnItemsConfig.getConfigurationSection("spawn-items").getKeys(false)) {
                ConfigurationSection itemSection = ConfigManager.spawnItemsConfig.getConfigurationSection("spawn-items." + itemName);
                if (itemSection == null) {
                    getLogger().warning("Invalid item type in configuration for spawn item '" + itemName + "'. Please check 'spawn-items.yml'");
                    continue;
                }

                Material material = Material.matchMaterial(itemSection.getString("type"));
                if (material == null) {
                    getLogger().warning("Invalid item type in configuration for spawn item '" + itemName + "'. Please check 'spawn-items.yml'");
                    continue;
                }

                if (item.getType() == material && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    String displayName = itemSection.getString("display-name");

                    if (meta != null && meta.getDisplayName() != null && meta.getDisplayName().equals(CC.translate(displayName))) {
                        String command = itemSection.getString("command", "");
                        if (!command.isEmpty()) {
                            player.performCommand(command);
                            event.setCancelled(true);
                        }
                        break;
                    }
                }
            }
        }
    }

    @EventHandler // Queue Items
    public void onPlayerInteract2(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT_CLICK") && item != null) {
            for (String itemName : ConfigManager.spawnItemsConfig.getConfigurationSection("queue-items").getKeys(false)) {
                ConfigurationSection itemSection = ConfigManager.spawnItemsConfig.getConfigurationSection("queue-items." + itemName);
                if (itemSection == null) {
                    getLogger().warning("Invalid item type in configuration for spawn item '" + itemName + "'. Please check 'spawn-items.yml'");
                    continue;
                }

                Material material = Material.matchMaterial(itemSection.getString("type"));
                if (material == null) {
                    getLogger().warning("Invalid item type in configuration for spawn item '" + itemName + "'. Please check 'spawn-items.yml'");
                    continue;
                }

                if (item.getType() == material && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    String displayName = itemSection.getString("display-name");

                    if (meta != null && meta.getDisplayName() != null && meta.getDisplayName().equals(CC.translate(displayName))) {
                        String command = itemSection.getString("command");
                        if (!command.isEmpty()) {
                            player.performCommand(command);
                            event.setCancelled(true);
                        }
                        break;
                    }
                }
            }
        }
    }
}