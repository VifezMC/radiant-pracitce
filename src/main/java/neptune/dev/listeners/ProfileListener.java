package neptune.dev.listeners;

import neptune.dev.Neptune;
import neptune.dev.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

public class ProfileListener implements Listener {

    private final Neptune plugin = Neptune.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        final Player player = Bukkit.getPlayer(event.getUniqueId());
        if (player != null && player.isOnline()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("§cYou tried to login too quickly after disconnecting.\n§cTry again in a few seconds.");
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> player.kickPlayer("§cPlease wait a few seconds before joining the server."));
            return;
        }
        final UUID uuid = event.getUniqueId();
        final Profile profile = new Profile(uuid);
        try {
            this.plugin.getProfileManager().load(profile);
        }
        catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("§cAn error has occurred while loading your profile.\n§cIf this keeps happening please contact an Administrator.");
            return;
        }
        this.plugin.getProfileManager().getProfiles().put(uuid, profile);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLoginEvent(final PlayerLoginEvent event) {
        final Profile profile = this.plugin.getProfileManager().findOrCreate(event.getPlayer().getUniqueId());
        if (profile == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("§cAn error has occurred while loading your profile.\n§cIf this keeps happening please contact an Administrator.");
            return;
        }
        if (!profile.isLoaded()) {
            this.plugin.getProfileManager().save(profile);
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("§cAn error has occurred while loading your profile.\n§cIf this keeps happening please contact an Administrator.");
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Profile profile = Neptune.getInstance().getProfileManager().getByUuid(player.getUniqueId());

        Neptune.logMessage("&aProfile with UUID &e" + profile.getUniqueId() + " &ahas been loaded.");
        this.plugin.getProfileManager().save(profile);
        this.plugin.getProfileManager().load(profile);
    }

    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        this.handledSaveDate(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason() != null) {
            if (event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
            }
        }
        this.handledSaveDate(event.getPlayer());
    }

    private void handledSaveDate(final Player player) {
        final Profile user = this.plugin.getProfileManager().findOrCreate(player.getUniqueId());
        if (user != null) {
            this.plugin.getProfileManager().delete(player.getUniqueId());
        }
    }
}
