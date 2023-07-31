package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.game.Arena;
import neptune.dev.utils.LocationUtil;
import neptune.dev.utils.render.CC;
import org.bukkit.Location;
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

    public static Arena getRandomArena(Player p1, Player p2) {
        List<Arena> allArenas = Neptune.getArenaManager().getArenas();

        if (allArenas.isEmpty()) {
            p1.sendMessage(CC.translate("&cNo Arenas available."));
            p2.sendMessage(CC.translate("&cNo Arenas available."));
            return null;
        }

        return allArenas.get(new Random().nextInt(allArenas.size()));
    }

    public static String getRandomString(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("The ArrayList is empty or null.");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static Arena getByName(String name) {
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

}
