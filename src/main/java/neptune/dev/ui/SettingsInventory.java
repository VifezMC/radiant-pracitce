package neptune.dev.ui;

import neptune.dev.managers.ConfigManager;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SettingsInventory {

    public static void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9 * 3, CC.translate(ConfigManager.menusConfig.getString("settings.item-color") + "Settings"));
        for (int x = 0; x < 27; x++) {
            menu.setItem(x, ItemBuilder.build(Material.STAINED_GLASS_PANE, 1, 15, "&0.", null));
        }
        ItemBuilder stat = new ItemBuilder(Material.FIREBALL);
        stat.setName(CC.translate(ConfigManager.menusConfig.getString("settings.item-color") + "Kill Effects"));
        stat.addLoreLine("&7&m-----------------------");
        stat.addLoreLine((CC.translate("&7Click here to select your preferred ")));
        stat.addLoreLine((CC.translate("&7kill effect.")));
        stat.addLoreLine("&7&m-----------------------");
        menu.setItem(10, stat.toItemStack());
        player.openInventory(menu);
    }

}