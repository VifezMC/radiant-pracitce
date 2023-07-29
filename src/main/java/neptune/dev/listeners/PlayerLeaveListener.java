package neptune.dev.listeners;

import neptune.dev.managers.QueueProcessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        if (QueueProcessor.isPlayerInQueue(event.getPlayer())) {
            QueueProcessor.removePlayerFromQueue(event.getPlayer());
        }
    }
}
