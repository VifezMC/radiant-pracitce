package neptune.dev.ui;

import neptune.dev.Neptune;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UnrankedInventory implements Listener {

    public static void openMenu(Player player, ConfigurationSection kitsConfig) {
        Inventory menu = Bukkit.createInventory(null, 9 * 1, CC.translate("&8Unranked Queue"));

        if (kitsConfig != null && kitsConfig.contains("kits")) {
            ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
            for (String kitName : kitsSection.getKeys(false)) {
                ConfigurationSection kitConfig = kitsSection.getConfigurationSection(kitName);
                if (kitConfig.contains("icon")) {
                    ItemStack iconItem = kitConfig.getItemStack("icon");
                    ItemMeta itemMeta = iconItem.getItemMeta();
                    itemMeta.addItemFlags(ItemFlag.values());
                    itemMeta.setDisplayName(CC.translate(Neptune.pluginConfig.getString("menus.queue.item-color") + kitName));
                    iconItem.setItemMeta(itemMeta);
                    menu.addItem(iconItem);
                }
            }
        }

        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getTitle().equals(CC.translate("&8Unranked Queue"))) {
            event.setCancelled(true);
            player.closeInventory();
            player.getInventory().clear();
            PlayerUtils.createQueueItems(player);
            player.updateInventory();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                String itemName = clickedItem.getItemMeta().getDisplayName();
                if (itemName != null && !itemName.isEmpty()) {
                    itemName = itemName.replaceAll("§.", "");
                    String command = "queue " + itemName;
                    player.performCommand(command);
                }
            }
        }
    }
}
