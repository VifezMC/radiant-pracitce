package neptune.dev.listeners;

import neptune.dev.game.EndGame;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.CC;
import neptune.dev.utils.Console;
import neptune.dev.utils.PlayerUtils;
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

    MatchManager matchManager = new MatchManager();

    @EventHandler // DISABLE PLAYER ITEM PLACE
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler // DISABLE PLAYER BLOCK BREAK
    public void blockBreak(BlockBreakEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(hasPlayerState(player, PlayerState.PLAYING)) {
          //  EndGame.EndGame(player.getKiller(), player);
            Console.sendMessage("null");
            String gameID = MatchManager.getMatchID(player).toString();
            String Player1 = MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).get(0).getName();
            String Player2 = MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).get(1).getName();
            String Winner = "";
            String Loser = "";
            if((Bukkit.getPlayer(Player2).getGameMode().equals(GameMode.CREATIVE))) {
                Winner = Player1;
                Loser = Player2;
            }else{
                Winner = Player2;
                Loser = Player1;
            }
            Bukkit.getPlayer(Player2).sendMessage(CC.translate("&c"+ Winner + "&7 killed " + "&a" + Loser));
            Bukkit.getPlayer(Player1).sendMessage(CC.translate("&c"+ Winner + "&7 killed " + "&a" + Loser));

            EndGame.EndGame(Bukkit.getPlayer(Winner), Bukkit.getPlayer(Loser), player);
            Console.sendMessage("" + MatchManager.getMatchID(player));
            Console.sendMessage(MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).toString());
            }
            event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerDeath2(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
        Player player = event.getEntity();
        player.setGameMode(GameMode.CREATIVE);
        Location location = player.getLocation();
        double x = location.getX();
        double y = location.getY() + 6.0;
        double z = location.getZ();
        World world = location.getWorld();
        Location lightningLocation = new Location(world, x, y, z);
        LightningStrike lightning = world.strikeLightning(lightningLocation);
        World world1 = location.getWorld();
        double x1 = location.getX();
        double y1 = location.getY() + 1.0;
        double z1 = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        Location newLocation = new Location(world1, x1, y1, z1, yaw, pitch);
        player.teleport(newLocation);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
    }
}