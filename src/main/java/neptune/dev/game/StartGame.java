package neptune.dev.game;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartGame {
    private static Map<Player, PlayerState> playerStates = new HashMap<>();

    public static void StartGame(String kitName, List<Player> players) {
        for (Player player : players) {
            for (Player otherPlayer : players) {
                if (player != otherPlayer) {
                    String formattingString = Neptune.messagesConfig.getString("match.match-found");
                    String formattedMessage = formattingString.replace("{opponent}", otherPlayer.getName());
                    player.sendMessage(CC.translate(formattedMessage));
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    ItemStack[] inventoryContents = getItemsFromConfig(kitName);
                    ItemStack[] armorContents = getArmorFromConfig(kitName);
                    player.getInventory().setContents(inventoryContents);
                    player.getInventory().setArmorContents(armorContents);
                    player.updateInventory();
                    PlayerUtils.removeState(player, PlayerState.LOBBY);
                    PlayerUtils.setState(player, PlayerState.PLAYING);
                }
            }
        }
    }
    private static ItemStack[] getItemsFromConfig(String location) {
        return ((List<ItemStack>) Neptune.kitsConfig.get("kits." + location + ".items")).toArray(new ItemStack[0]);
    }

    private static ItemStack[] getArmorFromConfig(String location) {
        return ((List<ItemStack>) Neptune.kitsConfig.get("kits." + location + ".armour")).toArray(new ItemStack[0]);
    }
}
