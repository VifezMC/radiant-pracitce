package neptune.dev.utils;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class PlayerUtils {

    private static Map<Player, PlayerState> playerStates = new HashMap<>();
    private static Map<String, ItemStack> spawnItemsCache = new HashMap<>();
    private static Map<String, ItemStack> queueItemsCache = new HashMap<>();
    private static World lobbyWorld;
    private static final Logger logger = getLogger();

    public static void resetPlayer(Player p) {
        setState(p, PlayerState.LOBBY);
        p.teleport(getLobbyLocation());
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);

        if (Neptune.pluginConfig.getBoolean("general.enable-join-message")) {
            for (String msg : Neptune.messagesConfig.getStringList("general.join-message")) {
                p.sendMessage(CC.translate(msg));
            }
        }

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        createSpawnItems(p);
        p.updateInventory();
    }

    public static void endGame(Player p) {
        removeState(p, PlayerState.PLAYING);
        setState(p, PlayerState.LOBBY);
        p.teleport(getLobbyLocation());
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        createSpawnItems(p);
        p.updateInventory();
    }

    private static World getLobbyWorld() {
        if (lobbyWorld == null) {
            lobbyWorld = Neptune.instance.getServer().getWorld(Neptune.arenaConfig.getString("lobby").split(":")[0]);
        }
        return lobbyWorld;
    }

    private static Location getLobbyLocation() {
        String[] data = Neptune.arenaConfig.getString("lobby").split(":");
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        Location loc = new Location(getLobbyWorld(), x, y, z);
        loc.setYaw(Float.parseFloat(data[4]));
        loc.setPitch(Float.parseFloat(data[5]));
        return loc;
    }

    public static void setState(Player player, PlayerState state) {
        playerStates.put(player, state);
    }

    public static void removeState(Player player, PlayerState state) {
        playerStates.remove(player, state);
    }

    public static PlayerState getState(Player player) {
        return playerStates.getOrDefault(player, PlayerState.LOBBY);
    }

    public static boolean hasPlayerState(Player player, PlayerState state) {
        PlayerState currentPlayerState = getState(player);
        return currentPlayerState == state;
    }

    public static String toString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
    }

    public static String getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping + "";
    }

    private static void createItems(Player player, String configSection, Map<String, ItemStack> itemCache) {
        ConfigurationSection itemsSection = Neptune.spawnItemsConfig.getConfigurationSection(configSection);
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
