package xyz.kiradev.managers;

import xyz.kiradev.types.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitManager {

    private static List<Kit> kits;

    public KitManager() {
        kits = new ArrayList<>();
    }

    public void loadKits() {
        if (ConfigManager.kitsConfig.get("kits") == null) {
            return;
        }

        for (String kitName : ConfigManager.kitsConfig.getConfigurationSection("kits").getKeys(false)) {
            ItemStack[] items = ConfigManager.kitsConfig.getList("kits." + kitName + ".items").toArray(new ItemStack[0]);
            ItemStack[] armour = ConfigManager.kitsConfig.getList("kits." + kitName + ".armour").toArray(new ItemStack[0]);
            ItemStack icon = ConfigManager.kitsConfig.getItemStack("kits." + kitName + ".icon");
            List<String> arenas = ConfigManager.kitsConfig.getStringList("kits." + kitName + ".arenas");
            Boolean ranked = ConfigManager.kitsConfig.getBoolean("kits." + kitName + ".ranked");
            List<String> rules = ConfigManager.kitsConfig.getStringList("kits." + kitName + ".rules");
            String description = ConfigManager.kitsConfig.getString("kits." + kitName + ".description");
            Kit kit = new Kit(kitName, items, armour, icon, arenas, ranked, rules, description);
            kits.add(kit);
        }
    }

    public static Kit getKit(String Kit) {
        for (Kit kit : kits) {
            if (kit.getName().equals(Kit)) {
                return kit;
            }
        }
        return null;
    }
    public List<Kit> getKits() {
        return kits;
    }
}
