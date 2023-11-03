package xyz.kiradev.commands.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.kiradev.managers.KitManager;
import xyz.kiradev.states.PlayerState;
import xyz.kiradev.types.Kit;
import xyz.kiradev.ui.KitEditorInventory;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;

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
            kiteditor.put(p, KitManager.getKit(args[0]));
            p.getInventory().setContents(KitManager.getKit(args[0]).getItems());
            PlayerUtils.setState(p, PlayerState.KITEDITOR);
            return true;
        }
        p.openInventory(KitEditorInventory.menu);
        return true;
    }
}
