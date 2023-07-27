package neptune.dev.commands.user;

import neptune.dev.Neptune;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            String formattingString = Neptune.messagesConfig.getString("ping-message");
            String formattedMessage = formattingString.replace("{player}", player.getName()).replace("{ping}", PlayerUtils.getPing(player));
            player.sendMessage(CC.translate(formattedMessage));
        } else if (args.length == 1) {
            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player not found or not online.");
                return true;
            }
            String formattingString2 = Neptune.messagesConfig.getString("other-player-ping-message");
            String formattedMessage2 = formattingString2.replace("{player}", target.getName()).replace("{ping}", PlayerUtils.getPing(target));
            player.sendMessage(CC.translate(formattedMessage2));
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /ping [<username>]");
        }

        return true;
    }
}
