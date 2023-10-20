package neptune.dev.commands.user;

import neptune.dev.managers.ConfigManager;
import neptune.dev.managers.InventoryManager;
import neptune.dev.managers.KitManager;
import neptune.dev.managers.QueueManager;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.render.CC;
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

        Player p = (Player) sender;
        if (QueueManager.isPlayerInQueue(p)) {
            KitManager.getKit(QueueManager.Queue.get(p)).removeQueue(1);
            String formattingString = ConfigManager.messagesConfig.getString("queue.leave-queue").replace("{kit}", QueueManager.Queue.get(p));
            QueueManager.removePlayerFromQueue(p);
            p.sendMessage(CC.translate(formattingString));
            p.getInventory().clear();
            InventoryManager.createSpawnItems(p);
            PlayerUtils.setState(p, PlayerState.LOBBY);
            p.updateInventory();
        } else {
            String formattingString = ConfigManager.messagesConfig.getString("queue.not-in-queue");
            p.sendMessage(CC.translate(formattingString));
        }

        return true;
    }
}