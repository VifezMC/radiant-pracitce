package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.utils.render.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        String kitName = args[0];

        if (isKitConfigured(kitName)) {
            Player player = (Player) sender;
            String formattingString = Neptune.messagesConfig.getString("queue.added-to-queue");
            String formattedMessage = formattingString.replace("{gamemode}", args[0]);
            player.sendMessage(CC.translate(formattedMessage));

            QueueProcessor.addPlayerToQueue(player, kitName);
        } else {
            sender.sendMessage(CC.translate("&cThe kit '" + kitName + "' doesn't exist!"));
        }
        return true;
    }

    private boolean isKitConfigured(String kitName) {
        return Neptune.kitsConfig.getConfigurationSection("kits").getKeys(false).contains(kitName);
    }
}
