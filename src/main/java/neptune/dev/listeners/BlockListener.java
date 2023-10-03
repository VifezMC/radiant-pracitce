package neptune.dev.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockListener implements Listener {
    private static Map<Player, Map<Block, Material>> placedBlocks = new ConcurrentHashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        placedBlocks.computeIfAbsent(player, k -> new ConcurrentHashMap<>()).put(block, block.getType());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        placedBlocks.computeIfPresent(player, (key, value) -> {
            for (Block block : value.keySet()) {
                block.setType(Material.AIR);
            }
            return null;
        });
    }


    public static void removeBlocks(Player player) {
        if (placedBlocks.containsKey(player)) {
            Map<Block, Material> playerPlacedBlocks = placedBlocks.get(player);

            for (Block block : playerPlacedBlocks.keySet()) {
                block.setType(Material.WATER);
            }
            placedBlocks.remove(player);
        }
    }

}
