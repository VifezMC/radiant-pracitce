package xyz.kiradev.listeners;

import xyz.kiradev.player.PlayerState;
import xyz.kiradev.utils.render.Console;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.kiradev.utils.PlayerUtils;

import java.util.Map;

public class RulesListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (PlayerUtils.hasPlayerState(player, PlayerState.PLAYING)) {
            Block block = event.getBlock();

            if (!isBlockContained(player, block)) {
                event.setCancelled(true);
                Console.sendError("You can't break this block because it wasn't placed by you.");
            }
        }
    }

    private boolean isBlockContained(Player player, Block block) {
        Map<Block, Material> playerPlacedBlocks = BlockListener.placedBlocks.get(player);
        return playerPlacedBlocks != null && playerPlacedBlocks.containsKey(block);
    }
}
