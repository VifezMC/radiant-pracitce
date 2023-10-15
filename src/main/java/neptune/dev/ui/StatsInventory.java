package neptune.dev.ui;

import neptune.dev.listeners.PlayerDataListener;
import neptune.dev.managers.ConfigManager;
import neptune.dev.types.Stats;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StatsInventory {

    public static void openStats(Player player) {
        Stats stats = PlayerDataListener.getStats(player);
        if (stats == null) return;
        Inventory menu = Bukkit.createInventory(null, 9 * 3, CC.translate(ConfigManager.menusConfig.getString("stats.menu-name")));
        for (int x = 0; x < 27; x++) {
            menu.setItem(x, ItemBuilder.build(Material.STAINED_GLASS_PANE, 1, 15, "&0.", null));
        }
        ItemBuilder stat = new ItemBuilder(Material.SKULL_ITEM);
        stat.setDurability(3);
        stat.setName(CC.translate(ConfigManager.menusConfig.getString("stats.item-name")));

        String loreKey = "stats.item-meta";
        List<String> lore = ConfigManager.menusConfig.getStringList(loreKey);
        List<String> translatedLore = new ArrayList<>();
        for (String loreLine : lore) {
            translatedLore.add(CC.translate(loreLine)
                    .replace("{kdr}", toKDR(stats.getWins(), stats.getLosses()) + "")
                    .replace("{matches_played}", stats.getMatches() + "")
                    .replace("{matches-won}", stats.getWins() + "")
                    .replace("{matches-lost}", stats.getLosses() + "")
                    .replace("{elo}", stats.getELO() + ""));
        }
        stat.setLore(translatedLore);
        menu.setItem(13, stat.toItemStack());
        player.openInventory(menu);
    }

    public static double toKDR(int kills, int deaths) {
        double kd = kills;
        if (deaths > 0) {
            kd = (double) kills / deaths;
            BigDecimal bd = new BigDecimal(kd);
            BigDecimal bd2 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
            kd = bd2.doubleValue();
        }
        return kd;
    }
}