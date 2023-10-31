package xyz.kiradev.commands.user;

import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.ui.unranked.UnrankedInventoryModern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankedCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if(ConfigManager.menusConfig.getString("kit-editor.ranked.type").contains("modern")){
            UnrankedInventoryModern.openMenu(player);
        }else{
            UnrankedInventoryModern.openMenu(player);
        }
        return true;
    }
}