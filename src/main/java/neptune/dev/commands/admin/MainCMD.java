package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.utils.CC;
import neptune.dev.Neptune;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;


public class MainCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".reload")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                Neptune.instance.registerConfigs();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Configs have been reloaded!");
                return true;
            }
        }
        player.sendMessage(CC.translate("&7&m--------------------------------------------"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&bRunning Neptune Practice Core"));
        player.sendMessage(CC.translate(""));
        player.sendMessage((CC.translate("&bAuthor: &f" + Constants.Autor)));
        player.sendMessage((CC.translate("&bVersion: &f" + Constants.Ver)));
        player.sendMessage((CC.translate("&bDiscord: &f" + Constants.Discord)));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7&m--------------------------------------------"));

        return true;
    }
}
