package neptune.dev.listeners;

import neptune.dev.Neptune;
import neptune.dev.game.EndGame;
import neptune.dev.game.Match;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
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

import static neptune.dev.utils.PlayerUtils.hasPlayerState;

public class GameListener implements Listener {

    private HashMap<String, Integer> boxingHits = new HashMap<>();

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
            Match match = MatchManager.getMatch(p);
            Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
            Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
            Player winner, loser;

            if (opponent2.getGameMode().equals(GameMode.CREATIVE)) {
                winner = opponent1;
                loser = opponent2;
            } else {
                winner = opponent2;
                loser = opponent1;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            opponent1.sendMessage(CC.translate(formattedMessage));
            opponent2.sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(winner, loser, p);
        }
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Match match = MatchManager.getMatch(p);
            Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
            Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
            Player winner, loser;

            if (p.getName().equals(opponent1.getName())) {
                winner = opponent2;
                loser = opponent1;
            } else {
                winner = opponent1;
                loser = opponent2;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            opponent1.sendMessage(CC.translate(formattedMessage));
            opponent2.sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(winner, loser, p);
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

    @EventHandler
    public void onSumoDeath(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(p).getKitName() + ".rules").contains("sumo")) {
                if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                    p.setGameMode(GameMode.CREATIVE);
                    PlayerUtils.removeState(p, PlayerState.PLAYING);
                    PlayerUtils.setState(p, PlayerState.LOBBY);
                    Location location = p.getLocation();
                    double x = location.getX();
                    double y = location.getY() + 6.0;
                    double z = location.getZ();
                    World world = location.getWorld();
                    Location lightningLocation = new Location(world, x, y, z);
                    LightningStrike lightning = world.strikeLightning(lightningLocation);
                    Match match = MatchManager.getMatch(p);
                    Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
                    Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
                    Player winner, loser;

                    if (opponent2.getGameMode().equals(GameMode.CREATIVE)) {
                        winner = opponent1;
                        loser = opponent2;
                    } else {
                        winner = opponent2;
                        loser = opponent1;
                    }

                    String formattingString = Neptune.messagesConfig.getString("match.kill-message");
                    String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
                    opponent1.sendMessage(CC.translate(formattedMessage));
                    opponent2.sendMessage(CC.translate(formattedMessage));
                    EndGame.EndGame(winner, loser, p);
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
                    boxingHits.putIfAbsent(damager.getName(), 0);
                    int hitCount = boxingHits.get(damager.getName()) + 1;
                    boxingHits.put(damager.getName(), hitCount);
                    damager.sendMessage("[DEBUG] " + hitCount);

                    if (hitCount >= 100) {
                        Match match = MatchManager.getMatch(damager);
                        Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
                        Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
                        Player winner, loser;

                        if (opponent2.getGameMode().equals(GameMode.CREATIVE)) {
                            winner = opponent1;
                            loser = opponent2;
                        } else {
                            winner = opponent2;
                            loser = opponent1;
                        }

                        String formattingString = Neptune.messagesConfig.getString("match.kill-message");
                        String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
                        opponent1.sendMessage(CC.translate(formattedMessage));
                        opponent2.sendMessage(CC.translate(formattedMessage));
                        EndGame.EndGame(winner, loser, damager);
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
                if (!Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(player).getKitName() + ".rules").contains("build")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
