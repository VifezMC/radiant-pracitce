package xyz.kiradev.commands.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.ui.unranked.UnrankedInventoryLegacy;
import xyz.kiradev.ui.unranked.UnrankedInventoryModern;

public class UnrankedCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player p = (Player) sender;
        if (ConfigManager.menusConfig.getString("queue-gui-type.unranked.type").contains("modern")) {
            UnrankedInventoryModern.openMenu(p);
        } else {
            UnrankedInventoryLegacy.openMenu(p, ConfigManager.kitsConfig);
        }
        return true;
    }
}