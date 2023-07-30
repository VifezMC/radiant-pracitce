package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.utils.LocationUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ArenaManager {

    private List<Arena> arenas;

    public ArenaManager() {
        this.arenas = new ArrayList<>();
    }

    public void loadArenas() {
        if (Neptune.arenaConfig.get("arenas") == null) {
            return;
        }
        for (String arenaName : Neptune.arenaConfig.getConfigurationSection("arenas").getKeys(false)) {
            Location spawn1 = LocationUtil.toLoc(Neptune.arenaConfig.getString("arenas." + arenaName + ".spawn1"));
            Location spawn2 = LocationUtil.toLoc(Neptune.arenaConfig.getString("arenas." + arenaName + ".spawn2"));
            Arena arena = new Arena(arenaName, spawn1, spawn2);
            arenas.add(arena);
        }
    }

    public static Arena getRandomArena() {
        List<Arena> allArenas = Neptune.getArenaManager().getArenas();
        return allArenas.get(new Random().nextInt(allArenas.size()));
    }

    public Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(name)) {
                return arena;
            }
        }
        return null;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public static List<Arena> getArenas(List<String> arenaNames) {
        return Neptune.getArenaManager().getArenas().stream()
                .filter(a -> arenaNames.contains(a.getName()))
                .collect(Collectors.toList());
    }
}
