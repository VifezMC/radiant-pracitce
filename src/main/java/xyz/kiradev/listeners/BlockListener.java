package xyz.kiradev.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import xyz.kiradev.managers.KitManager;
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
        if (PlayerUtils.hasPlayerState(p, PlayerState.PLAYING) && KitManager.getKit(MatchManager.getMatch(p).getKitName()).getRules().contains("build")) {
            Block block = event.getBlock();
            placedBlocks.computeIfAbsent(MatchManager.getMatch(p), k -> new ConcurrentHashMap<>()).put(block, block.getType());
        }else {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Match match = MatchManager.getMatch(player);
        if (match != null) {
            Map<Block, Material> blockMap = placedBlocks.get(match);
            if (blockMap != null && blockMap.containsKey(block)) {
                blockMap.remove(block);
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
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
