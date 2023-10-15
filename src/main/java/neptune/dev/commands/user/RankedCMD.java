package neptune.dev.commands.user;

import neptune.dev.managers.ConfigManager;
import neptune.dev.ui.ranked.RankedInventoryLegacy;
import neptune.dev.ui.ranked.RankedModernUI;
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
        if(ConfigManager.menusConfig.getString("queue-gui-type.ranked.type").contains("modern")){
            RankedModernUI.openMenu(player, ConfigManager.kitsConfig);
        }else{
            RankedInventoryLegacy.openMenu(player, ConfigManager.kitsConfig);
        }
        return true;
    }
}