package neptune.dev.game;

import neptune.dev.Neptune;
import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.MatchManager;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class Game {
    public static void endGame(Player p) {
        resetPlayer(p);
        Match match = MatchManager.getMatch(p);
        if (match != null) {
            Arena arena = ArenaManager.getByName(match.getArenaNameAsString());
            if (arena != null) {
                arena.setAvailable(true);
            }
        }
        MatchManager.removeMatch(MatchManager.getMatchID(p));
        QueueProcessor.playing -= 2;
    }

    public static void startGame(String kitName, List<Player> players) {
        ItemStack[] inventoryContents = getItemsFromConfig(kitName);
        ItemStack[] armorContents = getArmorFromConfig(kitName);

        for (Player p : players) {
            resetPlayer(p);
            p.getInventory().setContents(inventoryContents);
            p.getInventory().setArmorContents(armorContents);
            p.closeInventory();
        }
    }

    public static void EndGame(Player winner, Player loser, Player p) {
        String formattingString = Neptune.messagesConfig.getString("match.kill-message");
        String formattedMessage = formattingString.replace("{winner}", winner.getName()).replace("{loser}", loser.getName());
        winner.sendMessage(CC.translate(formattedMessage));
        loser.sendMessage(CC.translate(formattedMessage));

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Neptune"), () -> {
            endGame(winner);
            endGame(loser);
        }, 60L);
    }

    private static void resetPlayer(Player p) {
        PlayerUtils.setState(p, PlayerState.LOBBY);
        p.teleport(PlayerUtils.getLobbyLocation());
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
        PlayerUtils.createSpawnItems(p);
        p.updateInventory();
    }

    private static ItemStack[] getItemsFromConfig(String location) {
        return Neptune.kitsConfig.getList("kits." + location + ".items").toArray(new ItemStack[0]);
    }

    private static ItemStack[] getArmorFromConfig(String location) {
        return Neptune.kitsConfig.getList("kits." + location + ".armour").toArray(new ItemStack[0]);
    }
}