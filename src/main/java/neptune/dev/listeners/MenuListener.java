package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.player.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuListener implements Listener {

    @EventHandler
    public void statsMenu(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(CC.translate(ConfigManager.menusConfig.getString("stats.menu-name")))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void unrankedMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.getTitle().equals(CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.unranked.menu-name")))) {
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                ItemMeta itemMeta = clickedItem.getItemMeta();

                if (itemMeta != null && itemMeta.getDisplayName() != null &&
                        itemMeta.getDisplayName().equals(CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.unranked.surrounding-items-name")))) {
                    // Prevent interaction with surrounding items
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(true);
                player.closeInventory();
                player.getInventory().clear();
                PlayerUtils.createQueueItems(player);
                player.updateInventory();

                if (itemMeta != null && itemMeta.hasDisplayName()) {
                    String itemName = itemMeta.getDisplayName();
                    itemName = itemName.replaceAll("§.", "");
                    String command = "queue " + itemName;
                    player.performCommand(command);
                }
            }
        }
    }
}
