package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitManager {

    private static List<Kit> kits;

    public KitManager() {
        kits = new ArrayList<>();
    }

    public void loadKits() {
        if (Neptune.kitsConfig.get("kits") == null) {
            return;
        }

        for (String kitName : Neptune.kitsConfig.getConfigurationSection("kits").getKeys(false)) {
            ItemStack[] items = Neptune.kitsConfig.getList("kits." + kitName + ".items").toArray(new ItemStack[0]);
            ItemStack[] armour = Neptune.kitsConfig.getList("kits." + kitName + ".armour").toArray(new ItemStack[0]);
            ItemStack icon = Neptune.kitsConfig.getItemStack("kits." + kitName + ".icon");
            List<String> arenas = Neptune.kitsConfig.getStringList("kits." + kitName + ".arenas");
            Boolean ranked = Neptune.kitsConfig.getBoolean("kits." + kitName + ".ranked");
            List<String> rules = Neptune.kitsConfig.getStringList("kits." + kitName + ".rules");
            String description = Neptune.kitsConfig.getString("kits." + kitName + ".description");
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

    public static Kit getKitByKit(Kit Kit) {
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
