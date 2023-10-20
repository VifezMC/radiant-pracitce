package neptune.dev.ui.ranked;

import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.InventoryManager;
import neptune.dev.utils.render.CC;
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

import java.util.ArrayList;
import java.util.List;

public class RankedModernUI implements Listener {
    public static void openMenu(Player player, ConfigurationSection kitsConfig) {
        Inventory menu = Bukkit.createInventory(null, 9 * ConfigManager.menusConfig.getInt("queue-gui-type.ranked.height"), CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.ranked.menu-name")));

        if (kitsConfig != null && kitsConfig.contains("kits")) {
            ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
            int counter = 0;
            int x = 1;
            int y = 1;
            String loreKey = "queue-gui-type.item-meta";
            List<String> lore = ConfigManager.menusConfig.getStringList(loreKey);
            List<String> translatedLore = new ArrayList<>();
            for (String loreLine : lore) {
                translatedLore.add(CC.translate(loreLine).replace("{queueing}", "0").replace("{playing}", "0"));
            }
            for (String kitName : kitsSection.getKeys(false)) {
                ConfigurationSection kitConfig = kitsSection.getConfigurationSection(kitName);
                if (kitConfig.contains("icon")) {
                    boolean isRanked = kitConfig.getBoolean("ranked", false); // Read the "ranked" value from the config with a default value of false
                    if (isRanked) { // Only proceed if the kit is marked as "ranked"
                        ItemStack iconItem = kitConfig.getItemStack("icon");
                        ItemMeta itemMeta = iconItem.getItemMeta();
                        itemMeta.addItemFlags(ItemFlag.values());
                        itemMeta.setDisplayName(CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.ranked.item-color") + kitName));
                        itemMeta.setLore(translatedLore);
                        iconItem.setItemMeta(itemMeta);

                        int slotIndex = y * 9 + x;
                        if (slotIndex == 17) {
                            slotIndex = 19;
                        }
                        menu.setItem(slotIndex, iconItem);
                        counter++;
                        x = counter % 9 == 0 ? 1 : counter % 9 + 1;
                        y = counter / 9 + 1;
                    }
                }
            }
        }

        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR) {
                Material material = Material.matchMaterial(ConfigManager.menusConfig.getString("queue-gui-type.ranked.surrounding-items"));
                short durability = (short) ConfigManager.menusConfig.getInt("queue-gui-type.ranked.durability");
                String itemName = ConfigManager.menusConfig.getString("queue-gui-type.ranked.surrounding-items-name");
                ItemStack blueGlassPane = new ItemStack(material, 1, durability);
                ItemMeta glassPaneMeta = blueGlassPane.getItemMeta();
                glassPaneMeta.setDisplayName(CC.translate(itemName));
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
        if (event.getInventory().getTitle().equals(CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.ranked.menu-name")))) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                ItemStack clickedItem = event.getCurrentItem();
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta != null && itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equals(ConfigManager.menusConfig.getString("queue-gui-type.unranked.surrounding-items"))) {
                    event.setCancelled(true);
                    return;
                }
                event.setCancelled(true);
                player.closeInventory();
                player.getInventory().clear();
                InventoryManager.createQueueItems(player);
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
