package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.ui.UnrankedInventoryLegacy;
import neptune.dev.ui.UnrankedInventoryModern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnrankedCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if(Neptune.menusConfig.getString("queue-gui-type.type").contains("modern")){
            UnrankedInventoryModern.openMenu(player, Neptune.kitsConfig);
        }else{
            UnrankedInventoryLegacy.openMenu(player, Neptune.kitsConfig);
        }
        return true;
    }
}