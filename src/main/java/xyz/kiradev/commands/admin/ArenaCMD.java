package xyz.kiradev.commands.admin;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.kiradev.Constants;
import xyz.kiradev.managers.ArenaManager;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.Console;


public class ArenaCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission(Constants.PlName + ".arena")) {
            p.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length >= 2) {
            String arenaName = args[1];
            String subcommand = args[0].toLowerCase();
            switch (subcommand) {
                case "create":
                    createArena(arenaName);
                    p.sendMessage(CC.translate("&aSuccessfully created the arena &d" + arenaName + " &a!"));
                    p.sendMessage(CC.translate("&4&lIMPORTANT &cYou need to set position a and position b."));
                    p.sendMessage(CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
                    break;
                case "save":
                    saveArenas();
                    p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    p.sendMessage(CC.GREEN + "Arenas have been saved!");
                    break;
                case "a":
                    setSpawn(arenaName, 1, PlayerUtils.toString(p.getLocation()));
                    p.sendMessage(CC.translate("&aSuccessfully set the first spawn of &d" + arenaName + " &a!"));
                    p.sendMessage(CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
                    break;
                case "b":
                    setSpawn(arenaName, 2, PlayerUtils.toString(p.getLocation()));
                    p.sendMessage(CC.translate("&aSuccessfully set the second spawn of &d" + arenaName + " &a!"));
                    p.sendMessage(CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
                    break;
                case "min":
                    setEdges(arenaName, "min", PlayerUtils.toString(p.getLocation()));
                    p.sendMessage(CC.translate("&aSuccessfully set the min edge of &d" + arenaName + " &a!"));
                    p.sendMessage(CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
                    break;
                case "max":
                    setEdges(arenaName, "max", PlayerUtils.toString(p.getLocation()));
                    p.sendMessage(CC.translate("&aSuccessfully set the max edge of &d" + arenaName + " &a!"));
                    p.sendMessage(CC.translate("&4&lIMPORTANT &cYou might need to restart or reload your server to see changes!"));
                    break;
                default:
                    showArenaCommands(p);
                    break;
            }
            return true;
        } else {
            showArenaCommands(p);
        }

        return true;
    }

    private void createArena(String name) {
        ConfigManager.arenaConfig.set("arenas." + name + ".spawn1", "None");
        ConfigManager.arenaConfig.set("arenas." + name + ".spawn2", "None");
        ConfigManager.arenaConfig.set("arenas." + name + ".min", "None");
        ConfigManager.arenaConfig.set("arenas." + name + ".max", "None");

        ConfigManager.saveConfig(ConfigManager.arena, ConfigManager.arenaConfig);
    }

    private void saveArenas() {
        ConfigManager.arenaManager = new ArenaManager();
        ConfigManager.registerConfigs();
        Console.sendMessage("&aReloaded arenas");
    }

    private void setSpawn(String name, int number, String loc) {
        ConfigManager.arenaConfig.set("arenas." + name + ".spawn" + number, loc);
        ConfigManager.saveConfig(ConfigManager.arena, ConfigManager.arenaConfig);
    }

    private void setEdges(String name, String edge, String loc) {
        ConfigManager.arenaConfig.set("arenas." + name + "." + edge, loc);
        ConfigManager.saveConfig(ConfigManager.arena, ConfigManager.arenaConfig);
    }

    private void showArenaCommands(Player player) {
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("==============================================================="));
        player.sendMessage(CC.translate("&d&lStellar Arena Setup:"));
        player.sendMessage(CC.translate("&d/arena create &8<&7name&8> &7- &8(&7Create an arena&8)"));
        player.sendMessage(CC.translate("&d/arena remove &8<&7name&8> &7- &8(&7Remove an arena&8)"));
        player.sendMessage(CC.translate("&d/arena save &8<&7name&8> &7- &8(&7Save arenas&8)"));
        player.sendMessage(CC.translate(" "));
        player.sendMessage(CC.translate("==============================================================="));
        player.sendMessage(CC.translate("&d&lStellar Arena Spawn commands:"));
        player.sendMessage(CC.translate("&d/arena a &8<&7name&8> &7- &8(&7Set the first player spawn&8)"));
        player.sendMessage(CC.translate("&d/arena b &8<&7name&8> &7- &8(&7Set the second player spawn&8)"));
        player.sendMessage(CC.translate("&d/arena min &8<&7name&8> &7- &8(&7Set the first player spawn&8)"));
        player.sendMessage(CC.translate("&d/arena max &8<&7name&8> &7- &8(&7Set the second player spawn&8)"));
        player.sendMessage(CC.translate("==============================================================="));
        player.sendMessage(CC.translate(""));
    }
}