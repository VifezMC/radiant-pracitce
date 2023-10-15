package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.managers.ConfigManager;
import neptune.dev.player.PlayerUtils;
import neptune.dev.utils.render.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ArenaCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".arena")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length >= 2) {
            String arenaName = args[1];

            if (args[0].equalsIgnoreCase("create")) {
                createArena(arenaName);
                player.sendMessage(CC.translate("&aSuccessfully created the arena &b" + arenaName + " &a!"));
                player.sendMessage(CC.translate("&4&lIMPORTANT &cYou need to set position a and position b."));
                player.sendMessage( CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));

            } else if (args[0].equalsIgnoreCase("a")) {
                setSpawn(arenaName, 1, PlayerUtils.toString(player.getLocation()));
                player.sendMessage(CC.translate("&aSuccessfully set the first spawn of &b" + arenaName + " &a!"));
                player.sendMessage( CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));

            } else if (args[0].equalsIgnoreCase("b")) {
                setSpawn(arenaName, 2, PlayerUtils.toString(player.getLocation()));
                player.sendMessage(CC.translate("&aSuccessfully set the second spawn of &b" + arenaName + " &a!"));
                player.sendMessage( CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));

            } else {
                showArenaCommands(player);
            }

            return true;
        } else {
            showArenaCommands(player);
        }

        return true;
    }

    private void createArena(String name) {
        ConfigManager.arenaConfig.set("arenas." + name + ".spawn1", "None");
        ConfigManager.arenaConfig.set("arenas." + name + ".spawn2", "None");
        ConfigManager.saveConfig(ConfigManager.arena, ConfigManager.arenaConfig);
    }

    private void setSpawn(String name, int number, String loc) {
        ConfigManager.arenaConfig.set("arenas." + name + ".spawn" + number, loc);
        ConfigManager.saveConfig(ConfigManager.arena, ConfigManager.arenaConfig);
    }

    private void showArenaCommands(Player player) {
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7- Arena Setup"));
        player.sendMessage(CC.translate("&b/arena create &8<&7name&8> &7- &8(&7Create an arena&8)"));
        player.sendMessage(CC.translate("&b/arena remove &8<&7name&8> &7- &8(&7Remove an arena&8)"));
        player.sendMessage(CC.translate(" "));
        player.sendMessage(CC.translate("&8- &7Arena Spawn commands:"));
        player.sendMessage(CC.translate("&b/arena a &8<&7name&8> &7- &8(&7Set the first player spawn&8)"));
        player.sendMessage(CC.translate("&b/arena b &8<&7name&8> &7- &8(&7Set the second player spawn&8)"));
        player.sendMessage(CC.translate(""));
    }
}
