package neptune.dev.listeners;

import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.KitManager;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.GameState;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.types.Game;
import neptune.dev.types.Match;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.render.CC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

import static neptune.dev.player.PlayerUtils.hasPlayerState;

public class GameListener implements Listener {

    private final HashMap<String, Integer> boxingHits = new HashMap<>();
    private final String[] materials = new String[]{"SWORD", "_AXE"};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE) || PlayerUtils.hasPlayerState(event.getPlayer(),PlayerState.ENDED)) {
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        event.getDrops().clear();
        event.setDeathMessage(null);
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
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Player loser = event.getEntity(), winner = MatchManager.getOpponent(loser);
            String formattingString = ConfigManager.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            winner.sendMessage(CC.translate(formattedMessage));
            loser.sendMessage(CC.translate(formattedMessage));
            PlayerUtils.setState(winner, PlayerState.ENDED);
            PlayerUtils.setState(loser, PlayerState.ENDED);
            PlayerDataListener.getStats(loser).addLosses();
            PlayerDataListener.getStats(winner).addWins();
            Random random = new Random();
            byte randomNumber = (byte) (random.nextInt(11) + 10);

            PlayerDataListener.getStats(winner).addELO(randomNumber);
            PlayerDataListener.getStats(loser).removeELO(randomNumber);

            Game.EndGame(winner, loser);
        }
        event.setDeathMessage(null);
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
        event.setQuitMessage(null);
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Player loser = event.getPlayer(), winner = MatchManager.getOpponent(loser);
            String formattingString = ConfigManager.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            winner.sendMessage(CC.translate(formattedMessage));
            loser.sendMessage(CC.translate(formattedMessage));
            Game.EndGame(winner, loser);
        }
    }


    @EventHandler
    public void onSumoDeath(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Match match = MatchManager.getMatch(p);
            String kitName = match.getKitName();
            if (KitManager.getKit(kitName).getRules().contains("sumo")) {
                if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                    handleSumoDeath(p);
                }
            }
        }
    }

    private void handleSumoDeath(Player p) {
        p.setGameMode(GameMode.CREATIVE);
//        PlayerUtils.removeState(p, PlayerState.PLAYING);
        PlayerUtils.setState(p, PlayerState.LOBBY);
        Location location = p.getLocation();
        double x = location.getX();
        double y = location.getY() + 6.0;
        double z = location.getZ();
        World world = location.getWorld();
        Location lightningLocation = new Location(world, x, y, z);
        LightningStrike lightning = world.strikeLightning(lightningLocation);
        Match match = MatchManager.getMatch(p);
        Player winner = MatchManager.getOpponent(p), loser = p;
        String formattingString = ConfigManager.messagesConfig.getString("match.kill-message");
        String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
        winner.sendMessage(CC.translate(formattedMessage));
        loser.sendMessage(CC.translate(formattedMessage));
        Game.EndGame(winner, loser);
        PlayerDataListener.getStats(loser).addLosses();
        PlayerDataListener.getStats(winner).addWins();
        Random random = new Random();
        byte randomNumber = (byte) (random.nextInt(11) + 10);

        PlayerDataListener.getStats(winner).addELO(randomNumber);
        PlayerDataListener.getStats(loser).removeELO(randomNumber);
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

                    handleBoxingDeath(damager);
                }
            }
        }
    }

    private void handleBoxingDeath(Player damager) {
        boxingHits.putIfAbsent(damager.getName(), 0);
        int hitCount = boxingHits.get(damager.getName()) + 1;
        boxingHits.put(damager.getName(), hitCount);
        damager.sendMessage("[DEBUG] " + hitCount);
        if (hitCount >= 100) {
            Match match = MatchManager.getMatch(damager);
            Player winner = MatchManager.getMatchWinner(match.getMatchID()), loser = damager;
            String formattingString = ConfigManager.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            winner.sendMessage(CC.translate(formattedMessage));
            loser.sendMessage(CC.translate(formattedMessage));
            Game.EndGame(winner, loser);
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
