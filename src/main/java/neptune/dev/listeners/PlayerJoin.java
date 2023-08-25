package neptune.dev.listeners;



import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.render.CC;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import static neptune.dev.player.PlayerUtils.*;

public class PlayerJoin implements Listener {



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        // ON PLAY JOIN
        setState(p, PlayerState.LOBBY);
        p.teleport(getLobbyLocation());
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);

        if (Neptune.pluginConfig.getBoolean("general.enable-join-message")) {
            for (String msg : Neptune.messagesConfig.getStringList("general.join-message")) {
                p.sendMessage(CC.translate(msg));
            }
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        createSpawnItems(p);
        p.updateInventory();


        // REMOVE JOIN MESSAGE
        event.setJoinMessage(null);
    }
}
