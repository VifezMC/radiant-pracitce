package xyz.kiradev.managers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.Console;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager {
    public static Map<String, ItemStack> spawnItemsCache = new ConcurrentHashMap<>();
    private static final Map<String, ItemStack> queueItemsCache = new ConcurrentHashMap<>();
    private static final Map<String, ItemStack> partyItemsCache = new ConcurrentHashMap<>();


    public static void createSpawnItems(Player player) {
        createItems(player, "spawn-items", spawnItemsCache);
    }

    public static void createQueueItems(Player player) {
        createItems(player, "queue-items", queueItemsCache);
    }

    public static void createPartyItems(Player player) {
        createItems(player, "party-items", partyItemsCache);
    }


    private static void createItems(Player p, String configSection, Map<String, ItemStack> itemCache) {
        p.getInventory().clear();
        ConfigurationSection itemsSection = ConfigManager.spawnItemsConfig.getConfigurationSection(configSection);
        if (itemsSection == null) {
            Console.sendError("Invalid configuration for " + configSection + ". Please check 'config.yml'");
            return;
        }

        for (String itemName : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
            if (itemSection == null) {
                Console.sendError("Invalid item type in configuration for " + configSection + " item '" + itemName + "'. Please check 'items.yml'");
                continue;
            }

            ItemStack cachedItem = itemCache.get(itemName);
            if (cachedItem == null) {
                Material material = Material.matchMaterial(itemSection.getString("type", "DIAMOND_SWORD"));
                if (material == null) {
                    Console.sendError("Invalid item type in configuration for " + configSection + " item '" + itemName + "'. Please check 'items.yml'");
                    continue;
                }

                String displayName = itemSection.getString("display-name");
                int slot = itemSection.getInt("slot", 1);

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(CC.translate(displayName));
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
                cachedItem = item;
                itemCache.put(itemName, item);
            }

            p.getInventory().setItem(itemSection.getInt("slot", 1) - 1, cachedItem);
        }
    }
}
