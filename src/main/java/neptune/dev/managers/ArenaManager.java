package neptune.dev.managers;

import neptune.dev.types.Arena;
import neptune.dev.utils.LocationUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

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
            Arena arena = new Arena(arenaName, spawn1, spawn2);
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

    public static List<Arena> getArenas() {
        return arenas;
    }
}
