package neptune.dev.listeners;



import neptune.dev.utils.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // SET PLAYER VALUES TO NEW tbh I have no idea what to set this to
        PlayerUtils.resetPlayer(player);

        // REMOVE JOIN MESSAGE
        event.setJoinMessage(null);
    }

}
