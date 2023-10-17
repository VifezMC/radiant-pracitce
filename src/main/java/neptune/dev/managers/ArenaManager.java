package neptune.dev.managers;

import neptune.dev.listeners.BlockListener;
import neptune.dev.types.Arena;
import neptune.dev.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static void resetArena(Location lowEdge, Location highEdge, Player p) {
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
        BlockListener.removeBlocks(p);
    }
    public static List<Arena> getArenas() {
        return arenas;
    }
}
