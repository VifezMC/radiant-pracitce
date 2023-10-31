package xyz.kiradev.managers;

import xyz.kiradev.Stellar;
import xyz.kiradev.listeners.PlayerDataListener;
import xyz.kiradev.player.GameState;
import xyz.kiradev.player.PlayerState;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.types.Arena;
import xyz.kiradev.types.Match;
import xyz.kiradev.utils.DiscordUtils;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.Console;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private static Map<Player, Byte> countdowns = new ConcurrentHashMap<>();

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
            QueueManager.Queue.remove(firstPlayer);
            QueueManager.Queue.remove(secondPlayer);

            firstPlayer.getInventory().clear();
            InventoryManager.createSpawnItems(firstPlayer);
            firstPlayer.updateInventory();
            firstPlayer.teleport(PlayerUtils.getLobbyLocation());

            secondPlayer.getInventory().clear();
            InventoryManager.createSpawnItems(secondPlayer);
            secondPlayer.updateInventory();
            secondPlayer.teleport(PlayerUtils.getLobbyLocation());
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
            firstPlayer.teleport(PlayerUtils.getLobbyLocation());

            secondPlayer.getInventory().clear();
            InventoryManager.createSpawnItems(secondPlayer);
            secondPlayer.updateInventory();
            secondPlayer.teleport(PlayerUtils.getLobbyLocation());
            return;
        }
        firstPlayer.getInventory().setHeldItemSlot(0);
        secondPlayer.getInventory().setHeldItemSlot(0);
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
                    .replace("{arena}", MatchManager.getArena(MatchManager.getMatchID(firstPlayer)).getName())
                    .replace("{division}", CC.translate(ConfigManager.divisionsManager.getPlayerDivision(PlayerDataListener.getStats(secondPlayer).getELO()))));
            secondPlayer.sendMessage(CC.translate(msg)
                    .replace("{opponent}", Objects.requireNonNull(MatchManager.getOpponent(firstPlayer).getName()))
                    .replace("{opponent-ping}", PlayerUtils.getPing(MatchManager.getOpponent(firstPlayer)) + "")
                    .replace("{kit}", MatchManager.getMatch(firstPlayer).getKitName())
                    .replace("{arena}", MatchManager.getArena(MatchManager.getMatchID(firstPlayer)).getName())
                    .replace("{division}", CC.translate(ConfigManager.divisionsManager.getPlayerDivision(PlayerDataListener.getStats(firstPlayer).getELO()))));
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


            if (!(ConfigManager.databaseConfig.get(p.getUniqueId() + ".kiteditor." + kitName).equals("none"))) {
                p.getInventory().setContents(PlayerDataListener.getStats(p).getKitItems(kitName));
            }else{
                p.getInventory().setContents(KitManager.getKit(kitName).getItems());
            }
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

    public static void EndGame(Match match) {
        Player winner = match.getWinner();
        Player loser = match.getLoser();

        PlayerUtils.animateDeath(loser);
        MatchManager.getMatch(loser).getWinner().hidePlayer(MatchManager.getMatch(loser).getLoser());

        switch(PlayerDataListener.getStats(winner).getKilleffect()) {
            case "Lightning":
                Location location = loser.getLocation();
                double x = location.getX();
                double y = location.getY() + 2.0;
                double z = location.getZ();
                World world = location.getWorld();
                Location lightningLocation = new Location(world, x, y, z);
                world.strikeLightning(lightningLocation);
                break;
            case "Fireworks":
                Location location2 = loser.getLocation();
                World world2 = location2.getWorld();
                Firework firework = world2.spawn(location2, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                FireworkEffect.Builder builder = FireworkEffect.builder()
                        .withColor(Color.RED)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .trail(true)
                        .flicker(false);
                meta.addEffect(builder.build());
                meta.setPower(1);
                firework.setFireworkMeta(meta);
                Bukkit.getScheduler().runTaskLater(Stellar.instance, () -> {
                    firework.detonate();
                }, 5L);
                break;
        }

        loser.setHealth(loser.getMaxHealth());
        loser.setFireTicks(0);

        PlayerDataListener.getStats(loser).addLosses();
        PlayerDataListener.getStats(winner).addWins();
        Random random = new Random();
        byte elo = (byte) (random.nextInt(11) + 10);

        PlayerDataListener.getStats(winner).addELO(elo);
        PlayerDataListener.getStats(loser).removeELO(elo);

        for (String msg : ConfigManager.messagesConfig.getStringList("match.kill-message")) {
            loser.sendMessage(CC.translate(msg)
                    .replace("{winner}", winner.getName())
                    .replace("{loser}", loser.getName())
                    .replace("{winner-elo}", "" + PlayerDataListener.getStats(winner).getELO())
                    .replace("{loser-elo}", "" + PlayerDataListener.getStats(loser).getELO())
                    .replace("{elo}", "" + elo));
            winner.sendMessage(CC.translate(msg)
                    .replace("{winner}", winner.getName())
                    .replace("{loser}", loser.getName())
                    .replace("{loser-elo}", "" + PlayerDataListener.getStats(loser).getELO())
                    .replace("{winner-elo}", "" + PlayerDataListener.getStats(winner).getELO())
                    .replace("{elo}", "" + elo));
        }
        PlayerUtils.setState(winner, PlayerState.ENDED);
        PlayerUtils.setState(loser, PlayerState.ENDED);
        ArenaManager.resetArena(MatchManager.getMatch(winner));

        PlayerUtils.setState(winner, PlayerState.ENDED);
        PlayerUtils.setState(loser, PlayerState.ENDED);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Stellar.instance, () -> {
            if (ConfigManager.pluginConfig.getBoolean("discord.enable-webhook")) {
                DiscordUtils.sendMatchEnd(winner.getName(), loser.getName());
            }
            loser.teleport(PlayerUtils.getLobbyLocation());
            loser.setSaturation(20);
            loser.setFlying(false);
            loser.setFoodLevel(20);
            loser.setHealth(loser.getMaxHealth());
            loser.setFireTicks(0);
            loser.setGameMode(GameMode.SURVIVAL);
            loser.getActivePotionEffects().forEach(effect -> loser.removePotionEffect(effect.getType()));
            loser.getInventory().clear();
            loser.getInventory().setArmorContents(null);
            InventoryManager.createSpawnItems(loser);
            loser.updateInventory();
            PlayerUtils.setState(loser, PlayerState.LOBBY);

            winner.teleport(PlayerUtils.getLobbyLocation());
            winner.setSaturation(20);
            winner.setFlying(false);
            winner.setFoodLevel(20);
            winner.setHealth(winner.getMaxHealth());
            winner.setFireTicks(0);
            winner.setGameMode(GameMode.SURVIVAL);
            winner.getActivePotionEffects().forEach(effect -> winner.removePotionEffect(effect.getType()));
            winner.getInventory().clear();
            winner.getInventory().setArmorContents(null);
            InventoryManager.createSpawnItems(winner);
            winner.updateInventory();
            PlayerUtils.setState(winner, PlayerState.LOBBY);
            winner.showPlayer(loser);

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
            if(KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("feezeonspawn")){
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
            }.runTaskTimer(Stellar.instance, 0, 20);
        }
    }
}