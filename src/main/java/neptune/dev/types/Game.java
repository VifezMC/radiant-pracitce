package neptune.dev.types;

import neptune.dev.Neptune;
import neptune.dev.listeners.PlayerDataListener;
import neptune.dev.managers.*;
import neptune.dev.player.GameState;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Game {
    private static Map<Player, Byte> countdowns = new ConcurrentHashMap<>();

    public static void endGame(Player p) {
        PlayerUtils.setState(p, PlayerState.LOBBY);
        p.teleport(PlayerUtils.getLobbyLocation());
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);
        p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        PlayerUtils.createSpawnItems(p);
        p.updateInventory();
    }

    public static void startGame(String kitName, List<Player> players) {
        for (Player p : players) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getInventory().setContents(KitManager.getKit(kitName).getItems());
            p.getInventory().setArmorContents(KitManager.getKit(kitName).getArmour());
            p.updateInventory();
            p.closeInventory();
            p.setFoodLevel(20);
            p.setExhaustion(20);
            p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
            startCountdown(p);
        }
    }

    public static void EndGame(Player winner, Player loser) {
        PlayerUtils.setState(winner, PlayerState.ENDED);
        PlayerUtils.setState(loser, PlayerState.ENDED);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Neptune.instance, () -> {
            endGame(winner);
            endGame(loser);

            Match match = MatchManager.getMatch(loser);

            if (match != null) {
                String arenaName = match.getArenaNameAsString();
                Arena arena = ArenaManager.getByName(arenaName);
                if (arena != null) {
                    arena.setAvailable(true);
                }
            }
            KitManager.getKit(MatchManager.getMatch(loser).getKitName()).removePlaying(2);
            MatchManager.removeMatch(MatchManager.getMatchID(loser));
            QueueManager.playing -= 2;
        }, 60L);
    }

    private static void startCountdown(Player p) {

        if (p != null && !countdowns.containsKey(p)) {
            countdowns.put(p, (byte) 5);
            if(KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("sumo")){
                PlayerUtils.setGState(p, GameState.SUMO);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    int countdown = countdowns.getOrDefault(p, (byte) 0);

                    if (countdown > 0) {
                        p.sendMessage(CC.translate(ConfigManager.messagesConfig.getString("match.starting-countdown").replace("{countdown}", countdown + "")));
                        p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1.0f, 1.0f);
                        countdowns.put(p, (byte) (countdown - 1));
                    } else {
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        p.sendMessage(CC.translate(ConfigManager.messagesConfig.getString("match.match-started")));
                        countdowns.remove(p);
                        cancel();
                        if(KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("sumo")){
                            PlayerUtils.setGState(p, GameState.DEFAULT);
                        }
                    }
                }
            }.runTaskTimer(Neptune.instance, 0, 20);
        }
    }
}