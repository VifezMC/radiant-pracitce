package xyz.kiradev.commands.user;

import xyz.kiradev.ui.StatsInventory;
import xyz.kiradev.utils.render.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }
        Player p = (Player) sender;
        StatsInventory.openStats(p);
        return true;
    }
}
