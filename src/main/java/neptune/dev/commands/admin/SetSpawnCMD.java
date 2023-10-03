package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.utils.render.CC;
import neptune.dev.player.PlayerUtils;
import neptune.dev.Neptune;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.Sound;

import java.io.IOException;


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
        Neptune.arenaConfig.set("lobby", loc);
        saveConfig(player);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        player.sendMessage(CC.GREEN + "Spawn has been set!\n" + CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
    }

    private void saveConfig(Player player) {
        try {
            Neptune.arenaConfig.save(Neptune.arena);
            Neptune.arenaConfig.load(Neptune.arena);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            player.sendMessage(CC.translate("&cAn error occurred while saving the config. Please check the server logs for more details."));
        }
    }
}
