package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.ui.ranked.RankedInventoryLegacy;
import neptune.dev.ui.ranked.RankedModernUI;
import neptune.dev.ui.unranked.UnrankedInventoryLegacy;
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
        if(Neptune.menusConfig.getString("queue-gui-type.ranked.type").contains("modern")){
            RankedModernUI.openMenu(player, Neptune.kitsConfig);
        }else{
            RankedInventoryLegacy.openMenu(player, Neptune.kitsConfig);
        }
        return true;
    }
}