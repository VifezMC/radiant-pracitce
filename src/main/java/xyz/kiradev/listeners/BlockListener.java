package xyz.kiradev.listeners;

import xyz.kiradev.managers.MatchManager;
import xyz.kiradev.player.PlayerState;
import xyz.kiradev.types.Match;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.kiradev.utils.PlayerUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockListener implements Listener {
    static Map<Match, Map<Block, Material>> placedBlocks = new ConcurrentHashMap<>();



    // TODO: REWRITE
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (PlayerUtils.hasPlayerState(p, PlayerState.PLAYING)) {
            Block block = event.getBlock();
            placedBlocks.computeIfAbsent(MatchManager.getMatch(p), k -> new ConcurrentHashMap<>()).put(block, block.getType());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if(PlayerUtils.hasPlayerState(p, PlayerState.PLAYING)) {
            placedBlocks.computeIfPresent(MatchManager.getMatch(p), (key, value) -> {
                for (Block block : value.keySet()) {
                    block.setType(Material.AIR);
                }
                return null;
            });
        }
    }


    public static void removeBlocks(Match match) {
        if (placedBlocks.containsKey(match)) {
            Map<Block, Material> playerPlacedBlocks = placedBlocks.get(match);

            for (Block block : playerPlacedBlocks.keySet()) {
                block.setType(Material.AIR);
            }
            placedBlocks.remove(match);
        }
    }
}