package xyz.kiradev.commands.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;

public class PingCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            String formattingString = ConfigManager.messagesConfig.getString("general.ping-message");
            String formattedMessage = formattingString.replace("{player}", player.getName()).replace("{ping}", PlayerUtils.getPing(player) + "");
            player.sendMessage(CC.translate(formattedMessage));
        } else if (args.length == 1) {
            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(CC.RED + "Player not found or not online.");
                return true;
            }
            String formattingString2 = ConfigManager.messagesConfig.getString("general.other-player-ping-message");
            String formattedMessage2 = formattingString2.replace("{player}", target.getName()).replace("{ping}", PlayerUtils.getPing(target) + "");
            player.sendMessage(CC.translate(formattedMessage2));
        } else {
            player.sendMessage(CC.RED + "Usage: /ping [<username>]");
        }

        return true;
    }
}
