package neptune.dev.listeners;

import neptune.dev.Neptune;
import neptune.dev.game.EndGame;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static neptune.dev.utils.PlayerUtils.hasPlayerState;

public class GameListener implements Listener {

    private MatchManager matchManager = new MatchManager();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

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
}