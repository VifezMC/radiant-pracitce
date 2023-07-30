package neptune.dev.listeners;

import neptune.dev.Neptune;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.render.CC;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static neptune.dev.utils.PlayerUtils.hasPlayerState;
import static org.bukkit.Bukkit.getLogger;

public class SpawnListeners implements Listener {

    @EventHandler // DISABLING DAMAGE IN LOBBY
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (hasPlayerState(player, PlayerState.LOBBY)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler // DISABLING FOOD DROP IN LOBBY
    public void onFoodLevel(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (hasPlayerState(player, PlayerState.LOBBY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler // DISABLE PLAYER ITEM DROPS
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (hasPlayerState(player, PlayerState.LOBBY) && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler // REMOVE PLAYER FROM QUEUE IF THEY LEAVE
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        if (QueueProcessor.isPlayerInQueue(event.getPlayer())) {
            QueueProcessor.removePlayerFromQueue(event.getPlayer());
        }
    }

    @EventHandler // SPAWN ITEMS
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT_CLICK") && item != null) {
            for (String itemName : Neptune.spawnItemsConfig.getConfigurationSection("spawn-items").getKeys(false)) {
                ConfigurationSection itemSection = Neptune.spawnItemsConfig.getConfigurationSection("spawn-items." + itemName);
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
            for (String itemName : Neptune.spawnItemsConfig.getConfigurationSection("queue-items").getKeys(false)) {
                ConfigurationSection itemSection = Neptune.spawnItemsConfig.getConfigurationSection("queue-items." + itemName);
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