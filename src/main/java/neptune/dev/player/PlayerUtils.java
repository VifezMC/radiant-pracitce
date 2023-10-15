package neptune.dev.player;

import neptune.dev.Neptune;
import neptune.dev.managers.ConfigManager;
import neptune.dev.utils.render.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class PlayerUtils {

    public static Map<Player, PlayerState> playerStates = new ConcurrentHashMap<>();
    public static Map<Player, GameState> gameStates = new ConcurrentHashMap<>();
    public static Map<String, ItemStack> spawnItemsCache = new ConcurrentHashMap<>();
    private static Map<String, ItemStack> queueItemsCache = new ConcurrentHashMap<>();
    private static World lobbyWorld;
    private static final Logger logger = getLogger();

    private static World getLobbyWorld() {
        if (lobbyWorld == null) {
            lobbyWorld = Neptune.instance.getServer().getWorld(ConfigManager.arenaConfig.getString("lobby").split(":")[0]);
        }
        return lobbyWorld;
    }

    public static Location getLobbyLocation() {
        String[] data = ConfigManager.arenaConfig.getString("lobby").split(":");
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        Location loc = new Location(getLobbyWorld(), x, y, z);
        loc.setYaw(Float.parseFloat(data[4]));
        loc.setPitch(Float.parseFloat(data[5]));
        return loc;
    }

    public static void setState(Player player, PlayerState state) {
        playerStates.remove(player);
        playerStates.put(player, state);
    }
    public static void setGState(Player player, GameState state) {
        gameStates.remove(player);
        gameStates.put(player, state);
    }

    public static PlayerState getState(Player player) {
        return playerStates.getOrDefault(player, PlayerState.LOBBY);
    }
    public static GameState getGState(Player player) {
        return gameStates.getOrDefault(player, GameState.DEFAULT);
    }
    public static boolean hasPlayerState(Player player, PlayerState state) {
        PlayerState currentPlayerState = getState(player);
        return currentPlayerState == state;
    }
    public static boolean hasGPlayerState(Player player, GameState state) {
        GameState currentPlayerState = getGState(player);
        return currentPlayerState == state;
    }


    public static String toString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
    }

    public static int getPing(Player player) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle", new Class[0]).invoke(player);
            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception e) {
            return -1;
        }
    }
    private static void createItems(Player player, String configSection, Map<String, ItemStack> itemCache) {
        ConfigurationSection itemsSection = ConfigManager.spawnItemsConfig.getConfigurationSection(configSection);
        if (itemsSection == null) {
            logger.warning("Invalid configuration for " + configSection + ". Please check 'config.yml'");
            return;
        }

        for (String itemName : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
            if (itemSection == null) {
                logger.warning("Invalid item type in configuration for " + configSection + " item '" + itemName + "'. Please check 'spawn-items.yml'");
                continue;
            }

            ItemStack cachedItem = itemCache.get(itemName);
            if (cachedItem == null) {
                Material material = Material.matchMaterial(itemSection.getString("type", "DIAMOND_SWORD"));
                if (material == null) {
                    logger.warning("Invalid item type in configuration for " + configSection + " item '" + itemName + "'. Please check 'spawn-items.yml'");
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

            player.getInventory().setItem(itemSection.getInt("slot", 1) - 1, cachedItem);
        }
    }

    public static void createSpawnItems(Player player) {
        createItems(player, "spawn-items", spawnItemsCache);
    }

    public static void createQueueItems(Player player) {
        createItems(player, "queue-items", queueItemsCache);
    }
}
