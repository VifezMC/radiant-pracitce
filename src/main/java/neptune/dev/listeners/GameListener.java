package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.GameManager;
import neptune.dev.managers.KitManager;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.GameState;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.types.Match;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.render.CC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import static neptune.dev.player.PlayerUtils.hasPlayerState;

public class GameListener implements Listener {

    private final String[] materials = new String[]{"SWORD", "_AXE"};

    //@EventHandler
    //public void onBlockBreak(BlockBreakEvent event) {
      //      if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE) || PlayerUtils.hasPlayerState(event.getPlayer(),PlayerState.ENDED)) {
        //        event.setCancelled(true);
       // }
    //}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
        Player p = event.getEntity();
        if (p.getInventory().getItemInHand() != null) {
            p.getInventory().setItemInHand(null);
        }

        PlayerUtils.animateDeath(p);
        MatchManager.getMatch(p).setLoser(p);
        MatchManager.getMatch(p).getWinner().hidePlayer(MatchManager.getMatch(p).getLoser());
        GameManager.EndGame(MatchManager.getMatch(MatchManager.getMatch(p).getLoser()), MatchManager.getMatch(MatchManager.getMatch(p).getLoser()).getKitName());
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL || !event.getAction().name().contains("RIGHT_")) {
            return;
        }
        if (Cooldowns.isOnCooldown("enderpearl", event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            event.getPlayer().sendMessage(CC.translate(ConfigManager.messagesConfig.getString("match.cooldown-message").replace("{cooldown}", Cooldowns.getCooldownForPlayerString("enderpearl", event.getPlayer()))));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Player shooter = (Player) event.getEntity().getShooter();
        if (event.getEntityType() != EntityType.ENDER_PEARL) {
            return;
        }
        Cooldowns.addCooldown("enderpearl", shooter, ConfigManager.pluginConfig.getInt("general.ender-pearl-cooldown"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        MatchManager.getMatch(p).setLoser(p);
        event.setQuitMessage(null);
        GameManager.EndGame(MatchManager.getMatch(MatchManager.getMatch(p).getLoser()), MatchManager.getMatch(MatchManager.getMatch(p).getLoser()).getKitName());
    }


    @EventHandler
    public void onSumoDeath(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Match match = MatchManager.getMatch(p);
            String kitName = match.getKitName();
            if (KitManager.getKit(kitName).getRules().contains("sumo")) {
                if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                    MatchManager.getMatch(p).setLoser(p);
                    GameManager.EndGame(MatchManager.getMatch(MatchManager.getMatch(p).getLoser()), MatchManager.getMatch(MatchManager.getMatch(p).getLoser()).getKitName());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (hasPlayerState(p, PlayerState.PLAYING)) {
                if (KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("sumo")) {
                    event.setDamage(0);
                }
            }
            if (hasPlayerState(p, PlayerState.ENDED)) {
                event.setCancelled(true);
            }

            if (hasPlayerState(p, PlayerState.LOBBY) || hasPlayerState(p, PlayerState.INQUEUE) || PlayerUtils.hasGPlayerState(p, GameState.SUMO)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING) && PlayerUtils.hasGPlayerState(p, GameState.SUMO) && (KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("sumo"))) {
                Location to = event.getTo();
                Location from = event.getFrom();
                if ((to.getX() != from.getX() || to.getZ() != from.getZ())) {
                    p.teleport(from);
            }
        }
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            if (KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("sumo")) {
                event.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onBoxingDeath(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if (hasPlayerState(damager, PlayerState.PLAYING) && hasPlayerState(damaged, PlayerState.PLAYING)) {
                if (KitManager.getKit(MatchManager.getMatch(damager).getKitName()).getRules().contains("boxing")) {

                    //handleBoxingDeath(damager);
                }
            }
        }
    }

    @EventHandler
    public void onBoxingDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (hasPlayerState(p, PlayerState.PLAYING)) {
                if (KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("nodamage")) {

                    event.setDamage(0);
                }
            }
        }
    }

    @EventHandler
    public void onBoxingFoodLevel(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            if (KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("nohunger")) {
                event.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            if (!KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("build")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Match match = MatchManager.getMatch(player);

        if (match == null) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();
        Material itemType = itemStack.getType();

        Material type = event.getItemDrop().getItemStack().getType();
        String material = type.toString();
        for (String name : this.materials) {
            if (!material.contains(name)) continue;
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You may want to keep on this!");
        }

        // glass bottles and bowls are removed from inventories but
        // don't spawn items on the ground
        if (itemType == Material.GLASS_BOTTLE || itemType == Material.BOWL) {
            event.getItemDrop().remove();
        }
    }
}
