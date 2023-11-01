package xyz.kiradev.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.ItemBuilder;

public class SettingsKillEffectsInventory {
    public static Inventory menu;

    static {
        menu = Bukkit.createInventory(null, 9 * 3, CC.translate(ConfigManager.menusConfig.getString("settings.item-color") + "Kill Effects"));
        for (int x = 0; x < 27; x++) {
            menu.setItem(x, ItemBuilder.build(Material.STAINED_GLASS_PANE, 1, 15, "&0.", null));
        }
        ItemBuilder stat = new ItemBuilder(Material.FIREWORK);
        stat.setName(CC.translate(ConfigManager.menusConfig.getString("settings.item-color") + "Fireworks"));
        stat.addLoreLine("&7&m-----------------------");
        stat.addLoreLine((CC.translate("&7Click here to select")));
        stat.addLoreLine((CC.translate("&7kill effect.")));
        stat.addLoreLine("&7&m-----------------------");
        menu.setItem(10, stat.toItemStack());

        ItemBuilder lightning = new ItemBuilder(Material.PRISMARINE);
        lightning.setName(CC.translate(ConfigManager.menusConfig.getString("settings.item-color") + "Lightning"));
        lightning.addLoreLine("&7&m-----------------------");
        lightning.addLoreLine((CC.translate("&7Click here to select")));
        lightning.addLoreLine((CC.translate("&7kill effect.")));
        lightning.addLoreLine("&7&m-----------------------");
        menu.setItem(12, lightning.toItemStack());
    }
}