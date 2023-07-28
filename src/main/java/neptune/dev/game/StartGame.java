package neptune.dev.game;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StartGame {
    public static void startGame(String kitName, List<Player> players) {
        ItemStack[] inventoryContents = getItemsFromConfig(kitName);
        ItemStack[] armorContents = getArmorFromConfig(kitName);

        for (Player player : players) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getInventory().setContents(inventoryContents);
            player.getInventory().setArmorContents(armorContents);
            player.updateInventory();
            PlayerUtils.removeState(player, PlayerState.LOBBY);
            PlayerUtils.setState(player, PlayerState.PLAYING);
        }
    }

    private static ItemStack[] getItemsFromConfig(String location) {
        return Neptune.kitsConfig.getList("kits." + location + ".items").toArray(new ItemStack[0]);
    }

    private static ItemStack[] getArmorFromConfig(String location) {
        return Neptune.kitsConfig.getList("kits." + location + ".armour").toArray(new ItemStack[0]);
    }
}
