package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.listeners.PlayerDataListener;
import neptune.dev.player.GameState;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.types.Arena;
import neptune.dev.types.Match;
import neptune.dev.utils.DiscordUtils;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import org.bukkit.*;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.github.paperspigot.Title;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static neptune.dev.player.PlayerUtils.getLobbyLocation;

public class GameManager {
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
        InventoryManager.createSpawnItems(p);
        p.updateInventory();
    }

    public static void startGame(List<Player> players) {
            Player firstPlayer = players.get(0);
            Player secondPlayer = players.get(1);
            String kitName = QueueManager.Queue.get(firstPlayer);

            firstPlayer.getInventory().clear();
            secondPlayer.getInventory().setArmorContents(null);

            if (ConfigManager.pluginConfig.getBoolean("general.enable-debug")) {
                Console.sendMessage("Match found between " + firstPlayer.getName() + " and " + secondPlayer.getName());
            }


            if (KitManager.getKit(kitName).getArenas().isEmpty()) {
                firstPlayer.sendMessage(CC.translate("&cNo arenas found."));
                secondPlayer.sendMessage(CC.translate("&cNo arenas found."));
                firstPlayer.teleport(getLobbyLocation());
                secondPlayer.teleport(getLobbyLocation());
                QueueManager.Queue.remove(firstPlayer);
                QueueManager.Queue.remove(secondPlayer);
                firstPlayer.teleport(getLobbyLocation());
                firstPlayer.getInventory().clear();
                InventoryManager.createSpawnItems(firstPlayer);
                firstPlayer.updateInventory();

                secondPlayer.getInventory().clear();
                InventoryManager.createSpawnItems(secondPlayer);
                secondPlayer.updateInventory();
                secondPlayer.teleport(getLobbyLocation());
                return;
            }

            Arena selectedArena = ArenaManager.selectRandomAvailableArena(KitManager.getKit(kitName).getArenas());
            if (selectedArena == null) {
                firstPlayer.sendMessage(CC.translate("&cNo available arenas found."));
                secondPlayer.sendMessage(CC.translate("&cNo available arenas found."));

                QueueManager.Queue.remove(firstPlayer);
                QueueManager.Queue.remove(secondPlayer);

                firstPlayer.getInventory().clear();
                InventoryManager.createSpawnItems(firstPlayer);
                firstPlayer.updateInventory();
                firstPlayer.teleport(getLobbyLocation());

                secondPlayer.getInventory().clear();
                InventoryManager.createSpawnItems(secondPlayer);
                secondPlayer.updateInventory();
                secondPlayer.teleport(getLobbyLocation());
                return;
            }

            selectedArena.setAvailable(false);
            QueueManager.Queue.remove(firstPlayer);
            QueueManager.Queue.remove(secondPlayer);
            PlayerDataListener.getStats(firstPlayer).addMatches();
            PlayerDataListener.getStats(secondPlayer).addMatches();

            MatchManager.addMatch(firstPlayer, secondPlayer, selectedArena, kitName);
        for (String msg : ConfigManager.messagesConfig.getStringList("match.match-found")) {
            firstPlayer.sendMessage(CC.translate(msg)
                    .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(firstPlayer).getName()))
                    .replace("{opponent-ping}", PlayerUtils.getPing(MatchManager.getOpponent(firstPlayer)) + "")
                    .replace("{kit}", MatchManager.getMatch(firstPlayer).getKitName())
                    .replace("{arena}", MatchManager.getArena(MatchManager.getMatchID(firstPlayer)).getName()));
            secondPlayer.sendMessage(CC.translate(msg)
                    .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(firstPlayer).getName()))
                    .replace("{opponent-ping}", PlayerUtils.getPing(MatchManager.getOpponent(firstPlayer)) + "")
                    .replace("{kit}", MatchManager.getMatch(firstPlayer).getKitName())
                    .replace("{arena}", MatchManager.getArena(MatchManager.getMatchID(firstPlayer)).getName()));
        }
            firstPlayer.teleport(selectedArena.getSpawn1());
            secondPlayer.teleport(selectedArena.getSpawn2());

            PlayerUtils.setState(firstPlayer, PlayerState.PLAYING);
            PlayerUtils.setState(secondPlayer, PlayerState.PLAYING);

            if(ConfigManager.pluginConfig.getBoolean("discord.enable-webhook")){
                DiscordUtils.sendMatchStart(firstPlayer.getName(), secondPlayer.getName(), selectedArena.getName(), kitName);
            }
            if (ConfigManager.pluginConfig.getBoolean("general.enable-debug")) {
                Console.sendMessage("Removing " + firstPlayer.getName() + " and " + secondPlayer.getName() + " from the queue.");
            }

            QueueManager.playing += 2;
            KitManager.getKit(kitName).addPlaying(2);
        for (Player p : players) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getInventory().setContents(KitManager.getKit(kitName).getItems());
            p.getInventory().setArmorContents(KitManager.getKit(kitName).getArmour());
            p.updateInventory();
            p.closeInventory();
            p.setFoodLevel(20);
            p.setExhaustion(20);
            p.setSaturation(20);
            p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
            startCountdown(p);
        }
    }

    public static void EndGame(Player winner, Player loser, String kitName) {
        Title winnerTitle = new Title(CC.translate("&a&lYOU WON!"), CC.translate("&fYou beat &b" + loser.getName() + "&7!"));
        winner.sendTitle(winnerTitle);
        Title loserTitle = new Title(CC.translate("&c&lYOU LOST!"), CC.translate("&fYou lost to &b" + winner.getName() + "&7!"));
        loser.sendTitle(loserTitle);
        loser.setGameMode(GameMode.CREATIVE);

        Location location = loser.getLocation();
        double x = location.getX();
        double y = location.getY() + 6.0;
        double z = location.getZ();
        World world = location.getWorld();
        Location lightningLocation = new Location(world, x, y, z);
        LightningStrike lightning = world.strikeLightning(lightningLocation);
        Location newLocation = new Location(world, x, y + 1.0, z, location.getYaw(), location.getPitch());

        loser.teleport(newLocation);
        loser.setHealth(loser.getMaxHealth());
        loser.setFireTicks(0);

        for (String msg : ConfigManager.messagesConfig.getStringList("match.kill-message")) {
            loser.sendMessage(CC.translate(msg)
                    .replace("{winner}", winner.getName())
                    .replace("{loser}", loser.getName()));
            winner.sendMessage(CC.translate(msg)
                    .replace("{winner}", winner.getName())
                    .replace("{loser}", loser.getName()));
        }
            PlayerUtils.setState(winner, PlayerState.ENDED);
            PlayerUtils.setState(loser, PlayerState.ENDED);
            PlayerDataListener.getStats(loser).addLosses();
            PlayerDataListener.getStats(winner).addWins();
            Random random = new Random();
            byte randomNumber = (byte) (random.nextInt(11) + 10);

            PlayerDataListener.getStats(winner).addELO(randomNumber);
            PlayerDataListener.getStats(loser).removeELO(randomNumber);
        ArenaManager.resetArena(MatchManager.getMatch(winner).getArenaName().getMin(), MatchManager.getMatch(winner).getArenaName().getMax(), winner, kitName);
        ArenaManager.resetArena(MatchManager.getMatch(winner).getArenaName().getMin(), MatchManager.getMatch(winner).getArenaName().getMax(), loser, kitName);
        PlayerUtils.setState(winner, PlayerState.ENDED);
        PlayerUtils.setState(loser, PlayerState.ENDED);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Neptune.instance, () -> {

            if(ConfigManager.pluginConfig.getBoolean("discord.enable-webhook")){
                DiscordUtils.sendMatchEnd(winner.getName(), loser.getName());
            }

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
                        PlayerUtils.setGState(p, GameState.DEFAULT);
                    }
                }
            }.runTaskTimer(Neptune.instance, 0, 20);
        }
    }
}