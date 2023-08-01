package neptune.dev.game;

import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.MatchManager;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class EndGame {

    public static void EndGame(Player winner, Player loser, Player p) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Neptune"), new Runnable() {
            @Override
            public void run() {
                PlayerUtils.endGame(winner);
                PlayerUtils.endGame(loser);

                Match match = MatchManager.getMatch(p);

                if (match != null) {
                    String arenaName = match.getArenaNameAsString();
                    Arena arena = ArenaManager.getByName(arenaName);
                    if (arena != null) {
                        arena.setAvailable(true);
                    }
                }

                MatchManager.removeMatch(MatchManager.getMatchID(p));
                QueueProcessor.playing = QueueProcessor.playing - 2;
            }
        }, 60L);
    }
}
