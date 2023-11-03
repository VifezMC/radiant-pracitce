package xyz.kiradev.listeners;


import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.InventoryManager;
import xyz.kiradev.states.GameState;
import xyz.kiradev.states.PlayerState;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        // ON PLAY JOIN
        PlayerUtils.setState(p, PlayerState.LOBBY);
        PlayerUtils.setGState(p, GameState.DEFAULT);
        if (ConfigManager.pluginConfig.getBoolean("general.enable-join-message")) {
            for (String msg : ConfigManager.messagesConfig.getStringList("general.join-message")) {
                p.sendMessage(CC.translate(msg).replace("{player}", p.getName()));
            }
        }
        if (!ConfigManager.arenaConfig.getString("lobby").equals("none")) {
            p.teleport(PlayerUtils.getLobbyLocation());
        } else {
            p.sendMessage(CC.translate("&dStellar &8| &cMake sure to set server spawn /setspawn"));
        }
        p.setSaturation(20);
        p.setFlying(false);
        p.setFoodLevel(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.getInventory().setArmorContents(null);
        InventoryManager.createSpawnItems(p);
        p.updateInventory();

        // REMOVE JOIN MESSAGE
        event.setJoinMessage(null);
    }
}
