package neptune.dev.listeners;


import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.InventoryManager;
import neptune.dev.player.GameState;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.render.CC;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;

import static neptune.dev.player.PlayerUtils.*;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        // ON PLAY JOIN
        setState(p, PlayerState.LOBBY);
        setGState(p, GameState.DEFAULT);
        if (ConfigManager.pluginConfig.getBoolean("general.enable-join-message")) {
            for (String msg : ConfigManager.messagesConfig.getStringList("general.join-message")) {
                p.sendMessage(CC.translate(msg));
            }
        }
        if(!ConfigManager.arenaConfig.getString("lobby").equals("none")){
            p.teleport(getLobbyLocation());
        }else{
            p.sendMessage(CC.translate("&bNeptune &8| &cMake sure to set server spawn /setspawn"));
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
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        InventoryManager.createSpawnItems(p);
        p.updateInventory();

        // REMOVE JOIN MESSAGE
        event.setJoinMessage(null);

        // TABLIST
        if (ConfigManager.pluginConfig.getBoolean("tablist.enable") ){
            sendTabHeader(p, ConfigManager.pluginConfig.getString("tablist.header"), ConfigManager.pluginConfig.getString("tablist.footer"));
        }
    }

    private void sendTabHeader(Player player, String header, String footer) {
        CraftPlayer craftPlayer = (CraftPlayer)player;
        PlayerConnection playerConnection = (craftPlayer.getHandle()).playerConnection;
        IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + CC.translate(header) + "\"}");
        IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + CC.translate(footer) + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(tabHeader);
        try {
            Field field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(packet, tabFooter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playerConnection.sendPacket(packet);
    }
}
