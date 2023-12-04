package xyz.kiradev.commands.admin;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.kiradev.Constants;
import xyz.kiradev.Radiant;
import xyz.kiradev.utils.render.CC;


public class MainCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".reload")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            Radiant.reloadManagers();
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            player.sendMessage(CC.GREEN + "Configs have been reloaded!");
            return true;
        }
        showPluginInfo(player);
        return true;
    }

    private void showPluginInfo(Player player) {
        player.sendMessage(CC.translate("&7&m--------------------------------------------"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c&lCurrently Running Radiant Practice"));
        player.sendMessage(CC.translate(""));
        player.sendMessage((CC.translate("&cAuthor: &3" + Constants.Author)));
        player.sendMessage((CC.translate("&cVersion: &3" + Constants.Ver)));
        player.sendMessage((CC.translate("&cDiscord: &3" + Constants.Discord)));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7&oFor any additional support join our discord server"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7&m--------------------------------------------"));
    }
}
