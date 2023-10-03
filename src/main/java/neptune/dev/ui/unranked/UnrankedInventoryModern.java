package neptune.dev.ui.unranked;

import neptune.dev.Neptune;
import neptune.dev.game.Kit;
import neptune.dev.managers.KitManager;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.utils.render.CC;
import neptune.dev.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class UnrankedInventoryModern implements Listener {
    private static KitManager kitManager;

    public UnrankedInventoryModern(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    public static void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9 * Neptune.menusConfig.getInt("queue-gui-type.unranked.height"), CC.translate(Neptune.menusConfig.getString("queue-gui-type.unranked.menu-name")));


        int counter = 0;
        int x = 1;
        int y = 1;
        String loreKey = "queue-gui-type.item-meta";
        List<String> lore = Neptune.menusConfig.getStringList(loreKey);
        List<String> translatedLore = new ArrayList<>();

        for (Kit kit : kitManager.getKits()) {
            ItemStack iconItem = kit.getIcon();
            ItemMeta itemMeta = iconItem.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName(CC.translate(Neptune.menusConfig.getString("queue-gui-type.unranked.item-color") + kit.getName()));

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
                    loreWithDescription.add(loreLine.replace("{playing}", Integer.toString(kitManager.getKit(kit.getName()).getPlaying())));
                } else if (loreLine.contains("{queueing}")) {
                    loreWithDescription.add(loreLine.replace("{queueing}", Integer.toString(kitManager.getKit(kit.getName()).getQueue())));
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

        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR) {
                Material material = Material.matchMaterial(Neptune.menusConfig.getString("queue-gui-type.unranked.surrounding-items"));
                short durability = (short) Neptune.menusConfig.getInt("queue-gui-type.unranked.durability");
                String itemName = Neptune.menusConfig.getString("queue-gui-type.unranked.surrounding-items-name");
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
        if (event.getInventory().getTitle().equals(CC.translate(Neptune.menusConfig.getString("queue-gui-type.unranked.menu-name")))) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                ItemStack clickedItem = event.getCurrentItem();
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta != null && itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equals(CC.translate(Neptune.menusConfig.getString("queue-gui-type.unranked.surrounding-items-name")))) {
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
