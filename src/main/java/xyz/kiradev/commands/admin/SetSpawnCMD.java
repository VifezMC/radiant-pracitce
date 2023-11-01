package xyz.kiradev.commands.admin;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.kiradev.Constants;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;


public class SetSpawnCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".setspawn")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        String loc = PlayerUtils.toString(player.getLocation());
        setSpawn(loc, player);
        return true;
    }

    public void setSpawn(String loc, Player player) {
        ConfigManager.arenaConfig.set("lobby", loc);
        ConfigManager.saveConfig(ConfigManager.arena, ConfigManager.arenaConfig);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        player.sendMessage(CC.GREEN + "Spawn has been set!\n" + CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
    }
}
