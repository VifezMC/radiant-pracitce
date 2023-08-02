package neptune.dev.listeners;

import neptune.dev.Constants;
import neptune.dev.Neptune;
import neptune.dev.game.EndGame;
import neptune.dev.game.Match;
import neptune.dev.managers.MatchManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.*;
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
import java.util.List;

import static neptune.dev.utils.PlayerUtils.hasPlayerState;

public class GameListener implements Listener {

    private final HashMap<String, Integer> boxingHits = new HashMap<>();
    private final String[] materials = new String[]{"SWORD", "_AXE"};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Match match = MatchManager.getMatch(player);

        if (hasPlayerState(player, PlayerState.LOBBY)) {
            if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission(Constants.PlName + ".build")) {
                event.setCancelled(false);
            }
        } else if (hasPlayerState(player, PlayerState.PLAYING)) {
            String kitName = match.getKitName();
            List<String> rules = Neptune.kitsConfig.getStringList("kits." + kitName + ".rules");

            if (!rules.contains("build")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Match match = MatchManager.getMatch(p);

        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
            Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
            Player winner, loser;

            if (opponent2.getGameMode().equals(GameMode.CREATIVE)) {
                winner = opponent1;
                loser = opponent2;
            } else {
                winner = opponent2;
                loser = opponent1;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            opponent1.sendMessage(CC.translate(formattedMessage));
            opponent2.sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(winner, loser, p);
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
            event.getPlayer().sendMessage(CC.translate(Neptune.messagesConfig.getString("match.cooldown-message").replace("{cooldown}", Cooldowns.getCooldownForPlayerInt("enderpearl", event.getPlayer()) + "")));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Player shooter = (Player) event.getEntity().getShooter();
        if (event.getEntityType() != EntityType.ENDER_PEARL) {
            return;
        }
        Cooldowns.addCooldown("enderpearl", shooter, 15);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player p = event.getPlayer();
        Match match = MatchManager.getMatch(p);

        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
            Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
            Player winner, loser;

            if (p.getName().equals(opponent1.getName())) {
                winner = opponent2;
                loser = opponent1;
            } else {
                winner = opponent1;
                loser = opponent2;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            opponent1.sendMessage(CC.translate(formattedMessage));
            opponent2.sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(winner, loser, p);
        }
    }

    @EventHandler
    public void onPlayerDeath2(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
        Player p = event.getEntity();
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
    }

    @EventHandler
    public void onSumoDeath(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Match match = MatchManager.getMatch(p);
            String kitName = match.getKitName();

            if (Neptune.kitsConfig.getStringList("kits." + kitName + ".rules").contains("sumo")) {
                if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                    handleSumoDeath(p);
                }
            }
        }
    }

    private void handleSumoDeath(Player p) {
        p.setGameMode(GameMode.CREATIVE);
        PlayerUtils.removeState(p, PlayerState.PLAYING);
        PlayerUtils.setState(p, PlayerState.LOBBY);
        Location location = p.getLocation();
        double x = location.getX();
        double y = location.getY() + 6.0;
        double z = location.getZ();
        World world = location.getWorld();
        Location lightningLocation = new Location(world, x, y, z);
        LightningStrike lightning = world.strikeLightning(lightningLocation);
        Match match = MatchManager.getMatch(p);
        Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
        Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
        Player winner, loser;

        if (opponent2.getGameMode().equals(GameMode.CREATIVE)) {
            winner = opponent1;
            loser = opponent2;
        } else {
            winner = opponent2;
            loser = opponent1;
        }

        String formattingString = Neptune.messagesConfig.getString("match.kill-message");
        String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
        opponent1.sendMessage(CC.translate(formattedMessage));
        opponent2.sendMessage(CC.translate(formattedMessage));
        EndGame.EndGame(winner, loser, p);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (hasPlayerState(p, PlayerState.PLAYING)) {
                if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(p).getKitName() + ".rules").contains("sumo")) {
                    event.setDamage(0);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(p).getKitName() + ".rules").contains("sumo")) {
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
                if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(damager).getKitName() + ".rules").contains("boxing")) {
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
            Player opponent1 = MatchManager.getMatchPlayers(match.getMatchID()).get(0);
            Player opponent2 = MatchManager.getMatchPlayers(match.getMatchID()).get(1);
            Player winner, loser;

            if (opponent2.getGameMode().equals(GameMode.CREATIVE)) {
                winner = opponent1;
                loser = opponent2;
            } else {
                winner = opponent2;
                loser = opponent1;
            }

            String formattingString = Neptune.messagesConfig.getString("match.kill-message");
            String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
            opponent1.sendMessage(CC.translate(formattedMessage));
            opponent2.sendMessage(CC.translate(formattedMessage));
            EndGame.EndGame(winner, loser, damager);
        }
    }

    @EventHandler
    public void onBoxingDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (hasPlayerState(p, PlayerState.PLAYING)) {
                if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(p).getKitName() + ".rules").contains("nodamage")) {
                    event.setDamage(0);
                }
            }
        }
    }

    @EventHandler
    public void onBoxingFoodLevel(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            if (Neptune.kitsConfig.getStringList("kits." + MatchManager.getMatch(p).getKitName() + ".rules").contains("nohunger")) {
                event.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (hasPlayerState(player, PlayerState.PLAYING)) {
            String kitName = MatchManager.getMatch(player).getKitName();
            List<String> rules = Neptune.kitsConfig.getStringList("kits." + kitName + ".rules");

            if (!rules.contains("build")) {
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
