package neptune.dev.ui.unranked;

import neptune.dev.Neptune;
import neptune.dev.utils.render.CC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UnrankedInventoryLegacy {

    public static void openMenu(Player player, ConfigurationSection kitsConfig) {
        Inventory menu = Bukkit.createInventory(null, 9 * 3, CC.translate(Neptune.menusConfig.getString("queue-gui-type.unranked.menu-name")));

        if (kitsConfig != null && kitsConfig.contains("kits")) {
            ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
            for (String kitName : kitsSection.getKeys(false)) {
                ConfigurationSection kitConfig = kitsSection.getConfigurationSection(kitName);
                if (kitConfig.contains("icon")) {
                    ItemStack iconItem = kitConfig.getItemStack("icon");
                    ItemMeta itemMeta = iconItem.getItemMeta();
                    itemMeta.addItemFlags(ItemFlag.values());
                    itemMeta.setDisplayName(CC.translate(Neptune.menusConfig.getString("queue-gui-type.item-color") + kitName));
                    iconItem.setItemMeta(itemMeta);
                    menu.addItem(iconItem);
                }
            }
        }

        player.openInventory(menu);
    }
}