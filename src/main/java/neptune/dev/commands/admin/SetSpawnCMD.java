package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
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
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".setspawn")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        setSpawn(PlayerUtils.toString(player.getLocation()));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        player.sendMessage(CC.GREEN + "Spawn has been set!" + "\n" + CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!" ));
        return true;
    }
    public void setSpawn(String loc) {
        Neptune.arenaConfig.set("lobby", loc);
        try {
            Neptune.arenaConfig.save(Neptune.arena);
            Neptune.arenaConfig.load(Neptune.arena);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
