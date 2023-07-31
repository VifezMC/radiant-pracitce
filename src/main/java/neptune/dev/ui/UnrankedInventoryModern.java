package neptune.dev.ui;

import neptune.dev.Neptune;
import neptune.dev.utils.render.CC;
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

public class UnrankedInventoryModern implements Listener {

    public static void openMenu(Player player, ConfigurationSection kitsConfig) {
        Inventory menu = Bukkit.createInventory(null, 9 * 3, CC.translate("&8Unranked Queue"));

        if (kitsConfig != null && kitsConfig.contains("kits")) {
            ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
            int x = 1;
            int y = 1;
            for (String kitName : kitsSection.getKeys(false)) {
                ConfigurationSection kitConfig = kitsSection.getConfigurationSection(kitName);
                if (kitConfig.contains("icon")) {
                    ItemStack iconItem = kitConfig.getItemStack("icon");
                    ItemMeta itemMeta = iconItem.getItemMeta();
                    itemMeta.addItemFlags(ItemFlag.values());
                    itemMeta.setDisplayName(CC.translate(Neptune.menusConfig.getString("queue-gui-type.item-color") + kitName));
                    iconItem.setItemMeta(itemMeta);
                    menu.setItem(y * 9 + x, iconItem);
                    x++;
                    if (x > 8) {
                        x = 1;
                        y++;
                    }
                }
            }
        }

        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR) {
                ItemStack blueGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
                ItemMeta glassPaneMeta = blueGlassPane.getItemMeta();
                glassPaneMeta.setDisplayName(CC.translate("&bEmpty"));
                glassPaneMeta.addItemFlags(ItemFlag.values());
                blueGlassPane.setItemMeta(glassPaneMeta);
                menu.setItem(i, blueGlassPane);
            }
        }
        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getTitle().equals(CC.translate("&8Unranked Queue"))) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                ItemStack clickedItem = event.getCurrentItem();
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta != null && itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equals(CC.translate("&bEmpty"))) {
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
