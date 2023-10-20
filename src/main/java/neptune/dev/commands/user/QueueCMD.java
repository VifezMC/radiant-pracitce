package neptune.dev.commands.user;

import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.QueueManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QueueCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(CC.translate("&cUsage: /queue <kit_name>"));
            return true;
        }

        String kitName = args[0];

        if (isKitConfigured(kitName)) {
            Player p = (Player) sender;
            for (String msg : ConfigManager.messagesConfig.getStringList("queue.added-to-queue")) {
                p.sendMessage(CC.translate(msg)
                        .replace("{gamemode}", args[0]));
            }
            PlayerUtils.setState(p, PlayerState.INQUEUE);
            QueueManager.addPlayerToQueue(p, kitName);
            return true;
        } else {
            sender.sendMessage(CC.translate("&cThe kit '" + kitName + "' doesn't exist!"));
            return true;
        }
    }

    private boolean isKitConfigured(String kitName) {
        return ConfigManager.kitsConfig.getConfigurationSection("kits").getKeys(false).contains(kitName);
    }
}
