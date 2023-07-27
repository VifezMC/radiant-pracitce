package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.managers.QueueProcessor;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (QueueProcessor.isPlayerInQueue(Bukkit.getPlayer(commandSender.getName()))) {
            QueueProcessor.removePlayerFromQueue(Bukkit.getPlayer(commandSender.getName()));
            String formattingString = Neptune.messagesConfig.getString("queue.leave-queue");
            commandSender.sendMessage(CC.translate(formattingString));
            Player player = (Player) commandSender;
            player.getInventory().clear();
            PlayerUtils.createSpawnItems(player);
            player.updateInventory();
        } else {
            String formattingString = Neptune.messagesConfig.getString("queue.not-in-queue");
            commandSender.sendMessage(CC.translate(formattingString));
        }
        return false;
    }
}
