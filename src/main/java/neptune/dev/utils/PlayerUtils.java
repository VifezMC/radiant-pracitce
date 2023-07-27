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

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class PlayerUtils {

    private static Map<Player, PlayerState> playerStates = new HashMap<>();

    public static void resetPlayer(Player p) {
        setState(p, PlayerState.LOBBY);
        p.teleport(toLoc(Neptune.arenaConfig.getString("Lobby")));
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);

        // JOIN MESSAGE
        if (Neptune.pluginConfig.getBoolean("enable-join-message")) {
            for (String msg : Neptune.messagesConfig.getStringList("general.join-message")) {
                p.sendMessage(CC.translate(msg));
            }
        }

        // SPAWN ITEMS
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        createSpawnItems(p);
        p.updateInventory();
    }

    public static void endGame(Player p) {
        removeState(p, PlayerState.PLAYING);
        setState(p, PlayerState.LOBBY);
        p.teleport(toLoc(Neptune.arenaConfig.getString("Lobby")));
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);

        // SPAWN ITEMS
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        createSpawnItems(p);
        p.updateInventory();
    }

    public static Location toLoc(String location) {
        String[] data = location.split(":");
        World w = Neptune.instance.getServer().getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        Location loc = new Location(w, x, y, z);
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

    private static void createSpawnItems(Player player) {
        ConfigurationSection itemsSection = Neptune.spawnItemsConfig.getConfigurationSection("spawn-items");
        if (itemsSection == null) {
            getLogger().warning("Invalid configuration for spawn items. Please check 'config.yml'");
            return;
        }

        for (String itemName : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
            if (itemSection == null) {
                getLogger().warning("Invalid configuration for spawn item '" + itemName + "'. Please check 'config.yml'");
                continue;
            }

            Material material = Material.matchMaterial(itemSection.getString("type", "DIAMOND_SWORD"));
            if (material == null) {
                getLogger().warning("Invalid item type in configuration for spawn item '" + itemName + "'. Please check 'config.yml'");
                continue;
            }

            String displayName = itemSection.getString("display-name", "&6Ranked Queue &7(Right Click)");
            int slot = itemSection.getInt("slot", 1);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(CC.translate(displayName));
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
            player.getInventory().setItem(slot - 1, item);
        }
    }
}