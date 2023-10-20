package neptune.dev.listeners;

import neptune.dev.player.PlayerState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static neptune.dev.utils.PlayerUtils.hasPlayerState;

public class BlockListener implements Listener {
    static Map<Player, Map<Block, Material>> placedBlocks = new ConcurrentHashMap<>();
    // TODO: REWRITE
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (hasPlayerState(p, PlayerState.PLAYING)) {
            Block block = event.getBlock();
            placedBlocks.computeIfAbsent(p, k -> new ConcurrentHashMap<>()).put(block, block.getType());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        placedBlocks.computeIfPresent(p, (key, value) -> {
            for (Block block : value.keySet()) {
                block.setType(Material.AIR);
            }
            return null;
        });
    }


    public static void removeBlocks(Player p) {
        if (placedBlocks.containsKey(p)) {
            Map<Block, Material> playerPlacedBlocks = placedBlocks.get(p);

            for (Block block : playerPlacedBlocks.keySet()) {
                block.setType(Material.AIR);
            }
            placedBlocks.remove(p);
        }
    }
}
