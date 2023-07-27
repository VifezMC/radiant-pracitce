package neptune.dev.game;

import neptune.dev.managers.MatchManager;
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
                MatchManager.removeMatch(MatchManager.getMatchID(p));

            }
        }, 60L);

    }
}
