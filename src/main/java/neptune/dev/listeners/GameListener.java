package neptune.dev.listeners;

import neptune.dev.Neptune;
import neptune.dev.game.EndGame;
import neptune.dev.game.Match;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.CC;
import org.bukkit.*;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static neptune.dev.utils.PlayerUtils.hasPlayerState;

public class GameListener implements Listener {

    private MatchManager matchManager = new MatchManager();
    public HashMap<String, Integer> boxingHits = new HashMap<>();
    private Set<Player> playersHandledForSumoDeath = new HashSet<>();


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            String player1 = MatchManager.getMatchPlayers(MatchManager.getMatchID(p)).get(0).getName();
            String player2 = MatchManager.getMatchPlayers(MatchManager.getMatchID(p)).get(1).getName();
            String winner, loser;

            if (Bukkit.getPlayer(player2).getGameMode().equals(GameMode.CREATIVE)) {
                winner = player1;
                loser = player2;
            } else {
                winner = player2;
                loser = player1;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner).replace("{loser}", loser);
            Bukkit.getPlayer(player1).sendMessage(CC.translate(formattedMessage));
            Bukkit.getPlayer(player2).sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(Bukkit.getPlayer(winner), Bukkit.getPlayer(loser), p);
        }
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            String player1 = MatchManager.getMatchPlayers(MatchManager.getMatchID(p)).get(0).getName();
            String player2 = MatchManager.getMatchPlayers(MatchManager.getMatchID(p)).get(1).getName();
            String winner, loser;

            if (p.getName() == Bukkit.getPlayer(player1).getName()) {
                winner = player2;
                loser = player1;
            } else {
                winner = player1;
                loser = player2;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner).replace("{loser}", loser);
            Bukkit.getPlayer(player1).sendMessage(CC.translate(formattedMessage));
            Bukkit.getPlayer(player2).sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(Bukkit.getPlayer(winner), Bukkit.getPlayer(loser), p);
        }
    }
    @EventHandler
    public void onPlayerDeath2(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
        Player p = event.getEntity();
        p.setGameMode(GameMode.CREATIVE);
        Location location = p.getLocation();
        double x = location.getX();
        double y = location.getY() + 6.0;
        double z = location.getZ();
        World world = location.getWorld();
        Location lightningLocation = new Location(world, x, y, z);
        LightningStrike lightning = world.strikeLightning(lightningLocation);
        Location newLocation = new Location(world, x, y + 1.0, z, location.getYaw(), location.getPitch());
        p.teleport(newLocation);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
    }

    // Game rule listeners
    @EventHandler
    public void onSumoDeath(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(player).getKitName() + ".rules").contains("sumo")) {
            if (player.getLocation().getBlock().getType() == Material.WATER ||
                    player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                if (hasPlayerState(player, PlayerState.PLAYING) && !playersHandledForSumoDeath.contains(player)) {
                    playersHandledForSumoDeath.add(player);

                    player.setGameMode(GameMode.CREATIVE);
                    String player1 = MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).get(0).getName();
                    String player2 = MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).get(1).getName();
                    String winner, loser;

                    if (Bukkit.getPlayer(player2).getGameMode().equals(GameMode.CREATIVE)) {
                        winner = player1;
                        loser = player2;
                    } else {
                        winner = player2;
                        loser = player1;
                    }

                    String formattingString = Neptune.messagesConfig.getString("match.kill-message");
                    String formattedMessage = formattingString.replace("{winner}", winner).replace("{loser}", loser);
                    Bukkit.getPlayer(player1).sendMessage(CC.translate(formattedMessage));
                    Bukkit.getPlayer(player2).sendMessage(CC.translate(formattedMessage));
                    EndGame.EndGame(Bukkit.getPlayer(winner), Bukkit.getPlayer(loser), player);
                }
            }
        }
    }

    @EventHandler
    public void onBoxingDeath(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if (hasPlayerState(damager, PlayerState.PLAYING) && hasPlayerState(damaged, PlayerState.PLAYING)) {
                if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(damager).getKitName() + ".rules").contains("boxing")) {
                    if (boxingHits.containsKey(damager.getName())) {
                        boxingHits.put(damager.getName(), boxingHits.get(damager.getName()) + 1);
                        damager.sendMessage("[DEBUG] " + boxingHits.get(damager.getName()));
                    } else {
                        boxingHits.put(damager.getName(), 1);
                    }
                    if (boxingHits.get(damager.getName()) >= 100) {
                        String player1 = MatchManager.getMatchPlayers(MatchManager.getMatchID(damager)).get(0).getName();
                        String player2 = MatchManager.getMatchPlayers(MatchManager.getMatchID(damager)).get(1).getName();
                        String winner, loser;

                        if (Bukkit.getPlayer(player2).getGameMode().equals(GameMode.CREATIVE)) {
                            winner = player1;
                            loser = player2;
                        } else {
                            winner = player2;
                            loser = player1;
                        }

                        String formattingString = Neptune.messagesConfig.getString("match.kill-message");
                        String formattedMessage = formattingString.replace("{winner}", winner).replace("{loser}", loser);
                        Bukkit.getPlayer(player1).sendMessage(CC.translate(formattedMessage));
                        Bukkit.getPlayer(player2).sendMessage(CC.translate(formattedMessage));
                        EndGame.EndGame(Bukkit.getPlayer(winner), Bukkit.getPlayer(loser), damager);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            if (hasPlayerState(player, PlayerState.PLAYING)) {
                if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(player).getKitName() + ".rules").contains("build")) {
                        event.setCancelled(false);
                } else {
                    event.setCancelled(true);
                }
            }

        }
    }
}