package xyz.kiradev.ui.unranked;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.KitManager;
import xyz.kiradev.types.Kit;
import xyz.kiradev.utils.render.CC;

import java.util.ArrayList;
import java.util.List;

public class UnrankedInventoryModern  {

    public static void openMenu(Player player) {
        // Create the inventory
        Inventory menu = Bukkit.createInventory(null, 9 * ConfigManager.menusConfig.getInt("queue-gui-type.unranked.height"),
                CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.unranked.menu-name")));

        int counter = 0;
        int x = 1;
        int y = 1;
        String loreKey = "queue-gui-type.item-meta";
        List<String> lore = ConfigManager.menusConfig.getStringList(loreKey);
        List<String> translatedLore = new ArrayList<>();

        for (Kit kit : ConfigManager.kitManager.getKits()) {
            ItemStack iconItem = kit.getIcon();
            ItemMeta itemMeta = iconItem.getItemMeta();

            // Add item flags
            itemMeta.addItemFlags(ItemFlag.values());

            // Set display name with color
            itemMeta.setDisplayName(CC.translate(ConfigManager.menusConfig.getString("queue-gui-type.unranked.item-color") + kit.getName()));

            // Populate the translatedLore list with lore lines from your configuration
            translatedLore.clear();
            for (String loreLine : lore) {
                translatedLore.add(CC.translate(loreLine));
            }

            String description = kit.getDescription();
            List<String> loreWithDescription = new ArrayList<>();
            boolean descriptionAdded = false;

            for (String loreLine : translatedLore) {
                if (loreLine.contains("{playing}")) {
                    loreWithDescription.add(loreLine.replace("{playing}", Integer.toString(KitManager.getKit(kit.getName()).getPlaying())));
                } else if (loreLine.contains("{queueing}")) {
                    loreWithDescription.add(loreLine.replace("{queueing}", Integer.toString(KitManager.getKit(kit.getName()).getQueue())));
                } else if (loreLine.contains("{description}")) {
                    if (description != null && !description.isEmpty()) {
                        loreWithDescription.add(loreLine.replace("{description}", CC.translate(description)));
                        descriptionAdded = true;
                    }
                } else {
                    loreWithDescription.add(loreLine);
                }
            }

            if (!descriptionAdded && description != null && !description.isEmpty()) {
                loreWithDescription.add("");
            }

            itemMeta.setLore(loreWithDescription);
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

        // Fill empty slots with surrounding items
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR) {
                Material material = Material.matchMaterial(ConfigManager.menusConfig.getString("queue-gui-type.unranked.surrounding-items"));
                short durability = (short) ConfigManager.menusConfig.getInt("queue-gui-type.unranked.durability");
                String itemName = ConfigManager.menusConfig.getString("queue-gui-type.unranked.surrounding-items-name");
                ItemStack blueGlassPane = new ItemStack(material, 1, durability);
                ItemMeta glassPaneMeta = blueGlassPane.getItemMeta();
                glassPaneMeta.setDisplayName(CC.translate(itemName));
                glassPaneMeta.addItemFlags(ItemFlag.values());
                blueGlassPane.setItemMeta(glassPaneMeta);
                menu.setItem(i, blueGlassPane);
            }
        }

        // Open the inventory for the player
        player.openInventory(menu);
    }
}