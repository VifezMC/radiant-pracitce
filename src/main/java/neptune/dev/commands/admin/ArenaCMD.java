package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.utils.CC;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.Neptune;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;


public class ArenaCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".arena")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }


        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                createArena(args[1]);
                player.sendMessage(CC.translate("&aSuccessfully created the arena &b" + args[1] + " &a!"));
                player.sendMessage(CC.translate("&4&lIMPORTANT &cYou need set spawn1 and spawn2."));
            }
            if (args[0].equalsIgnoreCase("setspawn1")) {
                setSpawn(args[1],1, PlayerUtils.toString(player.getLocation()));
                player.sendMessage(CC.translate("&aSuccessfully set the first spawn of &b" + args[1] + " &a!"));
            }
            if (args[0].equalsIgnoreCase("setspawn2")) {
                setSpawn(args[1],2, PlayerUtils.toString(player.getLocation()));
                player.sendMessage(CC.translate("&aSuccessfully set the second spawn of &b" + args[1] + " &a!"));
            }
            return true;
        }
        player.sendMessage(CC.translate("&7&m------------------------------------------------"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&8- &7Arena commands:"));
        player.sendMessage(CC.translate("&b/arena create &8<&7name&8> &7- &8(&7Create an arena&8)"));
        player.sendMessage(CC.translate("&b/arena remove &8<&7name&8> &7- &8(&7Remove an arena&8)"));
        player.sendMessage(CC.translate(" "));
        player.sendMessage(CC.translate("&8- &7Arena Spawn commands:"));
        player.sendMessage(CC.translate("&b/arena setspawn1 &8<&7name&8> &7- &8(&7Set the first player spawn&8)"));
        player.sendMessage(CC.translate("&b/arena setspawn2 &8<&7name&8> &7- &8(&7Set the second player spawn&8)"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7&m------------------------------------------------"));
        return true;
    }

    public void createArena(String name) {
        Neptune.arenaConfig.set("arenas." + name + ".spawn1", "None");
        Neptune.arenaConfig.set("arenas." + name + ".spawn2", "None");
        try {
            Neptune.arenaConfig.save(Neptune.arena);
            Neptune.arenaConfig.load(Neptune.arena);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setSpawn(String name, int number, String loc) {
        Neptune.arenaConfig.set("arenas." + name + ".spawn" + number, loc);
        try {
            Neptune.arenaConfig.save(Neptune.arena);
            Neptune.arenaConfig.load(Neptune.arena);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
