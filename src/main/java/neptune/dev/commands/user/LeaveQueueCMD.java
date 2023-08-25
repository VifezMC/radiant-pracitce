package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        if (QueueProcessor.isPlayerInQueue(player)) {
            QueueProcessor.removePlayerFromQueue(player);
            String formattingString = Neptune.messagesConfig.getString("queue.leave-queue");
            player.sendMessage(CC.translate(formattingString));
            player.getInventory().clear();
            PlayerUtils.createSpawnItems(player);
            player.updateInventory();
        } else {
            String formattingString = Neptune.messagesConfig.getString("queue.not-in-queue");
            player.sendMessage(CC.translate(formattingString));
        }

        return true;
    }
}
