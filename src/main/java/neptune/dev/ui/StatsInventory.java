package neptune.dev.ui;

import neptune.dev.utils.CC;
import neptune.dev.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;



public class StatsInventory implements Listener {

    public static void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9 * 3, CC.translate("&8Stats of " + player.getName()));
        for (int x = 0; x < 27; x++) {
            menu.setItem(x, ItemBuilder.build(Material.STAINED_GLASS_PANE, 1, 15, "&0.", null));
        }
        ItemBuilder stat = new ItemBuilder(Material.SKULL_ITEM);
        stat.setDurability(3);
        stat.setName("&bYour Statistics");
        stat.addLoreLine("&7&m-----------------------");
        stat.addLoreLine("&bKills&7: &f" + "1");
        stat.addLoreLine("&bDeaths&7: &f" + "2");
        stat.addLoreLine("&bMatches played&7: &f" + "3");
        menu.setItem(13, stat.toItemStack());
        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(CC.translate("&8Stats of " + event.getWhoClicked().getName()))) {
            event.setCancelled(true);
        }
    }
}

