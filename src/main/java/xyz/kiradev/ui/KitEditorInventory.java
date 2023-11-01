package xyz.kiradev.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.types.Kit;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.ItemBuilder;

public class KitEditorInventory {
    public static Inventory menu;

    static {
        menu = Bukkit.createInventory(null, 9 * ConfigManager.menusConfig.getInt("kit-editor.height"),
                CC.translate(ConfigManager.menusConfig.getString("kit-editor.menu-name")));

        int counter = 0;
        int x = 1;
        int y = 1;
        for (Kit kit : ConfigManager.kitManager.getKits()) {
            ItemBuilder item = new ItemBuilder(kit.getIcon());
            item.setName(CC.translate(ConfigManager.menusConfig.getString("kit-editor.item-color") + kit.getName()));
            item.setLore(CC.translate("&7&m--------------------"));
            item.addLoreLine((CC.translate("&7Click here to edit your")));
            item.addLoreLine((CC.translate("&7preferred kit")));
            item.addLoreLine(CC.translate("&7&m--------------------"));

            int slotIndex = y * 9 + x;
            if (slotIndex == 17) {
                slotIndex = 19;
            }

            menu.setItem(slotIndex, item.toItemStack());

            counter++;
            x = counter % 9 == 0 ? 1 : counter % 9 + 1;
            y = counter / 9 + 1;
        }
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR) {
                Material material = Material.matchMaterial(ConfigManager.menusConfig.getString("kit-editor.surrounding-items"));
                short durability = (short) ConfigManager.menusConfig.getInt("kit-editor.durability");
                String itemName = ConfigManager.menusConfig.getString("kit-editor.surrounding-items-name");
                ItemStack blueGlassPane = new ItemStack(material, 1, durability);
                ItemMeta glassPaneMeta = blueGlassPane.getItemMeta();
                glassPaneMeta.setDisplayName(CC.translate(itemName));
                glassPaneMeta.addItemFlags(ItemFlag.values());
                blueGlassPane.setItemMeta(glassPaneMeta);
                menu.setItem(i, blueGlassPane);
            }
        }
    }
}
