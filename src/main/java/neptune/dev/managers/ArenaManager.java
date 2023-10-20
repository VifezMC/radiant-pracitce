package neptune.dev.managers;

import neptune.dev.listeners.BlockListener;
import neptune.dev.types.Arena;
import neptune.dev.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private static List<Arena> arenas;

    public ArenaManager() {
        arenas = new ArrayList<>();
    }

    public void loadArenas() {
        if (ConfigManager.arenaConfig.get("arenas") == null) {
            return;
        }

        for (String arenaName : ConfigManager.arenaConfig.getConfigurationSection("arenas").getKeys(false)) {
            Location spawn1 = LocationUtil.toLoc(ConfigManager.arenaConfig.getString("arenas." + arenaName + ".spawn1"));
            Location spawn2 = LocationUtil.toLoc(ConfigManager.arenaConfig.getString("arenas." + arenaName + ".spawn2"));
            Location min = LocationUtil.toLoc(ConfigManager.arenaConfig.getString("arenas." + arenaName + ".min"));
            Location max = LocationUtil.toLoc(ConfigManager.arenaConfig.getString("arenas." + arenaName + ".max"));

            Arena arena = new Arena(arenaName, spawn1, spawn2, min, max);
            arenas.add(arena);
        }
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(name)) {
                return arena;
            }
        }
        return null;
    }

    public static void resetArena(Location lowEdge, Location highEdge, Player p, String kitName) {
        if (KitManager.getKit(kitName).getRules().contains("build")) {

            World world = lowEdge.getWorld();

            int minX = Math.min(lowEdge.getBlockX(), highEdge.getBlockX());
            int minY = Math.min(lowEdge.getBlockY(), highEdge.getBlockY());
            int minZ = Math.min(lowEdge.getBlockZ(), highEdge.getBlockZ());

            int maxX = Math.max(lowEdge.getBlockX(), highEdge.getBlockX());
            int maxY = Math.max(lowEdge.getBlockY(), highEdge.getBlockY());
            int maxZ = Math.max(lowEdge.getBlockZ(), highEdge.getBlockZ());

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.STATIONARY_LAVA) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    Location itemLocation = entity.getLocation();
                    if (isLocationWithinArena(itemLocation, lowEdge, highEdge)) {
                        entity.remove();
                    }
                }
            }

            BlockListener.removeBlocks(p);
        }
    }
    private static boolean isLocationWithinArena(Location location, Location lowEdge, Location highEdge) {
        return location.getBlockX() >= lowEdge.getBlockX() &&
                location.getBlockX() <= highEdge.getBlockX() &&
                location.getBlockY() >= lowEdge.getBlockY() &&
                location.getBlockY() <= highEdge.getBlockY() &&
                location.getBlockZ() >= lowEdge.getBlockZ() &&
                location.getBlockZ() <= highEdge.getBlockZ();
    }


    public static Arena selectRandomAvailableArena(List<String> arenas) {
        List<Arena> availableArenas = new ArrayList<>();
        for (String arenaName : arenas) {
            Arena arena = ArenaManager.getByName(arenaName);
            if (arena != null && arena.isAvailable()) {
                availableArenas.add(arena);
            }
        }
        if (availableArenas.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return availableArenas.get(random.nextInt(availableArenas.size()));
    }

    public static List<Arena> getArenas() {
        return arenas;
    }
}
