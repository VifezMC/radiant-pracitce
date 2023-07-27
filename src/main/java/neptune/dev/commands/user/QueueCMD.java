package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QueueCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(CC.translate("&cUsage: /queue <kit_name>"));
            return true;
        }

        String kitName = args[0].toLowerCase();

        if (getConfiguredKits().contains(kitName)) {
            Player player = (Player) sender;
            player.sendMessage(CC.translate("&bYou were added to " + args[0]) + " queue");
            QueueProcessor.addPlayerToQueue(player, kitName);
        } else {
            sender.sendMessage(CC.translate("&cThe kit '" + kitName + "' doesn't exist!"));
        }
        return true;
    }

    private List<String> getConfiguredKits() {
        return new ArrayList<>(Neptune.kitsConfig.getConfigurationSection("kits").getKeys(false));
    }
}