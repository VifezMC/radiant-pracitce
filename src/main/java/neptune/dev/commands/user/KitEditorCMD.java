package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.KitManager;
import neptune.dev.player.PlayerState;
import neptune.dev.types.Kit;
import neptune.dev.ui.KitEditorInventory;
import neptune.dev.utils.LocationUtil;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitEditorCMD implements CommandExecutor {
    public static Map<Player, Kit> kiteditor = new ConcurrentHashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player p = (Player) sender;


        if (args.length == 1 && !(KitManager.getKit(args[0]).getItems().equals("None"))) {
            p.getInventory().clear();
            kiteditor.put(p, KitManager.getKit(args[0]));
            p.getInventory().setContents(KitManager.getKit(args[0]).getItems());
            PlayerUtils.setState(p, PlayerState.KITEDITOR);
            return true;
        }
        p.openInventory(KitEditorInventory.menu);
        return true;
    }
}
