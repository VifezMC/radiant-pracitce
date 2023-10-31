package xyz.kiradev.managers;

import xyz.kiradev.listeners.BlockListener;
import xyz.kiradev.types.Arena;
import xyz.kiradev.types.Match;
import xyz.kiradev.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

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

    public static void resetArena(Match match) {
        World world = match.getArena().getSpawn1().getWorld();

        int minX = Math.min(match.getArena().getMin().getBlockX(), match.getArena().getMax().getBlockX());
        int minY = Math.min(match.getArena().getMin().getBlockY(), match.getArena().getMax().getBlockY());
        int minZ = Math.min(match.getArena().getMin().getBlockZ(), match.getArena().getMax().getBlockZ());

        int maxX = Math.max(match.getArena().getMin().getBlockX(), match.getArena().getMax().getBlockX());
        int maxY = Math.max(match.getArena().getMin().getBlockY(), match.getArena().getMax().getBlockY());
        int maxZ = Math.max(match.getArena().getMin().getBlockZ(), match.getArena().getMax().getBlockZ());

        if (KitManager.getKit(match.getKitName()).getRules().contains("build")) {
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
            BlockListener.removeBlocks(match);
        }

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                Location entityLocation = entity.getLocation();
                if (entityLocation.getBlockX() >= minX && entityLocation.getBlockX() <= maxX
                        && entityLocation.getBlockY() >= minY && entityLocation.getBlockY() <= maxY
                        && entityLocation.getBlockZ() >= minZ && entityLocation.getBlockZ() <= maxZ) {
                    entity.remove();
                }
            }
        }
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
