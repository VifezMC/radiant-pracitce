package neptune.dev.commands.user;

import neptune.dev.managers.QueueProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LeaveQueueCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // check if player is in a queue
        if (QueueProcessor.isPlayerInQueue(Bukkit.getPlayer(commandSender.getName()))) {
            QueueProcessor.removePlayerFromQueue(Bukkit.getPlayer(commandSender.getName()));
            commandSender.sendMessage("You have left the queue.");
        } else {
            commandSender.sendMessage("You are not in a queue!");
        }
        return false;
    }
}
