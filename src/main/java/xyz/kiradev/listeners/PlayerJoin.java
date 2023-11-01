package xyz.kiradev.listeners;


import xyz.kiradev.Stellar;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.InventoryManager;
import xyz.kiradev.player.GameState;
import xyz.kiradev.player.PlayerState;
import xyz.kiradev.utils.render.CC;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import xyz.kiradev.utils.PlayerUtils;

import java.lang.reflect.Field;

public class PlayerJoin implements Listener {

    @EventHandler
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
        if(!ConfigManager.arenaConfig.getString("lobby").equals("none")){
            p.teleport(PlayerUtils.getLobbyLocation());
        }else{
            p.sendMessage(CC.translate("&bStellar &8| &cMake sure to set server spawn /setspawn"));
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
            sendTabHeader(p, ConfigManager.pluginConfig.getString("tablist.header")
                    .replace("{online}", String.valueOf(Stellar.instance.getServer().getOnlinePlayers().size())), ConfigManager.pluginConfig.getString("tablist.footer")
                    .replace("{online}", String.valueOf(Stellar.instance.getServer().getOnlinePlayers().size())));
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
