package neptune.dev.game;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class StartGame {
    public static void startGame(String kitName, List<Player> players) {
        ItemStack[] inventoryContents = getItemsFromConfig(kitName);
        ItemStack[] armorContents = getArmorFromConfig(kitName);

        for (Player p : players) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getInventory().setContents(inventoryContents);
            p.getInventory().setArmorContents(armorContents);
            p.updateInventory();
            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }
            PlayerUtils.removeState(p, PlayerState.LOBBY);
            PlayerUtils.setState(p, PlayerState.PLAYING);
        }
    }

    private static ItemStack[] getItemsFromConfig(String location) {
        return Neptune.kitsConfig.getList("kits." + location + ".items").toArray(new ItemStack[0]);
    }

    private static ItemStack[] getArmorFromConfig(String location) {
        return Neptune.kitsConfig.getList("kits." + location + ".armour").toArray(new ItemStack[0]);
    }
}
