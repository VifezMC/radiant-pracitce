package neptune.dev.managers;


import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.utils.LocationUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private List<Arena> arenas;

    public ArenaManager() {
        this.arenas = new ArrayList<>();
    }

    public void loadArenas() {
        if (Neptune.arenaConfig.get("arenas") == null) return;
        for (String r : Neptune.arenaConfig.getConfigurationSection("arenas").getKeys(false)) {
            Location spawn1 = LocationUtil.toLoc(Neptune.arenaConfig.getString("arenas." + r + ".spawn1"));
            Location spawn2 = LocationUtil.toLoc(Neptune.arenaConfig.getString("arenas." + r + ".spawn2"));
            Arena arena = new Arena(r, spawn1, spawn2);
            arenas.add(arena);
        }
    }

    public static Arena getRandomArena() {
        return Neptune.getArenaManager().getArenas().get(new Random().nextInt(Neptune.getArenaManager().getArenas().size()));
    }

    public Arena getByName(String name) {
        for (Arena arena : Neptune.getArenaManager().getArenas()) {
            if (arena.getName().equals(name)) {
                return arena;
            }
        }
        return null;
    }

    public List<Arena> getArenas() {
        return arenas;
    }
}
