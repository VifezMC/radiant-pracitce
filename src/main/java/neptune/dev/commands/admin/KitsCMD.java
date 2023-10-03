package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.Neptune;
import neptune.dev.managers.KitManager;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class KitsCMD implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Constants.PlName + ".kits")) {
            player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length >= 1) {
            String action = args[0].toLowerCase();
            switch (action) {
                case "create":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        createKit(kitName);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        player.sendMessage(CC.GREEN + "Kit has been created!");
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit create <name>.");
                    }
                    break;
                case "set":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        setItemsAndArmour(kitName, player);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit set <name>.");
                    }
                    break;
                case "give":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        giveKit(kitName, player);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit give <name>.");
                    }
                    break;
                case "seticon":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        setIcon(kitName, player.getItemInHand(), player);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit seticon <name>.");
                    }
                    break;
                case "setdescription":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        StringBuilder description = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            description.append(args[i]).append(" ");
                        }
                        setDesc(kitName, description.toString(), player);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit setdescription <name> <description>.");
                    }
                    break;
                case "arenas":
                    if (args.length >= 3) {
                        String kitName = args[1];
                        String arena = args[2];
                        addArena(kitName, arena, player);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit arenas <name> <arena>.");
                    }
                    break;
                case "rules":
                    if (args.length >= 3) {
                        String kitName = args[1];
                        String rule = args[2].toLowerCase();
                        addRule(kitName, rule, player);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit rules <name> <rule>.");
                    }
                    break;
                case "ranked":
                    if (args.length >= 3) {
                        String kitName = args[1];
                        String rankedValue = args[2].toLowerCase();
                        setRankedStatus(kitName, rankedValue, player);
                    } else {
                        player.sendMessage(CC.RED + "Invalid command usage. Use /kit ranked <name> <true/false>.");
                    }
                    break;
                default:
                    player.sendMessage(CC.RED + "Invalid command. Use /kit for available commands.");
                    break;
            }
        } else {
            showKitCommands(player);
        }
        return true;
    }

    private void createKit(String name) {
        Neptune.kitsConfig.set("kits." + name + ".items", "None");
        Neptune.kitsConfig.set("kits." + name + ".armour", "None");
        Neptune.kitsConfig.set("kits." + name + ".icon", "None");
        Neptune.kitsConfig.set("kits." + name + ".arenas", "None");
        Neptune.kitsConfig.set("kits." + name + ".rules", "None");
        Neptune.kitsConfig.set("kits." + name + ".description", "None");
        saveConfig();
    }

    private void setItemsAndArmour(String kitName, Player player) {
        if (kitExists(kitName)) {
            ItemStack[] content = player.getInventory().getContents();
            ItemStack[] armour = player.getInventory().getArmorContents();

            setItems(kitName, content);
            setArmour(kitName, armour);
            saveConfig();
            player.sendMessage(CC.GREEN + "Kit has been set to your inventory!");
        }else{
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void setItems(String location, ItemStack[] items) {
            Neptune.kitsConfig.set("kits." + location + ".items", Arrays.asList(items));
    }

    private void setDesc(String location, String desc, Player p) {
        Neptune.kitsConfig.set("kits." + location + ".description", desc);
        p.sendMessage(CC.GREEN + "Kit description has been set!");
    }


    private void setIcon(String location, ItemStack item, Player player) {
        if (kitExists(location)) {
            Neptune.kitsConfig.set("kits." + location + ".icon", item);
            player.sendMessage(CC.GREEN + "Kit icon has been set!");
            saveConfig();
        }else{
            player.sendMessage(CC.RED + "Kit with name '" + location + "' does not exist.");
        }
    }

    private void setArmour(String location, ItemStack[] items) {
        Neptune.kitsConfig.set("kits." + location + ".armour", Arrays.asList(items));
    }

    private void giveKit(String kitName, Player player) {
            if (kitExists(kitName)) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                ItemStack[] inventoryContents = KitManager.getKit(kitName).getItems();
                ItemStack[] armorContents = KitManager.getKit(kitName).getArmour();
                player.getInventory().setContents(inventoryContents);
                player.getInventory().setArmorContents(armorContents);
                player.updateInventory();
                player.sendMessage(CC.GREEN + "There you go!");
            }else{
                player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
            }
    }

    private void setRankedStatus(String kitName, String rankedValue, Player player) {
        if (kitExists(kitName)) {
            boolean isRanked = Boolean.parseBoolean(rankedValue);
            Neptune.kitsConfig.set("kits." + kitName + ".ranked", isRanked);
            saveConfig();
            player.sendMessage(CC.GREEN + "Kit ranked status has been updated!");
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void addRule(String kitName, String rule, Player player) {
        if (kitExists(kitName)) {
            List<String> validRules = Arrays.asList("boxing", "build", "sumo", "nodamage", "nohunger");
            if (validRules.contains(rule)) {
                List<String> rules = Neptune.kitsConfig.getStringList("kits." + kitName + ".rules");
                rules.add(rule);
                Neptune.kitsConfig.set("kits." + kitName + ".rules", rules);
                saveConfig();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Kit rule has been added!");
            } else {
                player.sendMessage(CC.RED + "Invalid rules");
                player.sendMessage(CC.RED + "Valid rules: boxing, build, sumo, noDamage, noHunger");
            }
        }else{
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void addArena(String kitName, String arena, Player player) {
        if (kitExists(kitName)) {
                List<String> arenas = Neptune.kitsConfig.getStringList("kits." + kitName + ".arenas");
                arenas.add(arena);
                Neptune.kitsConfig.set("kits." + kitName + ".arenas", arenas);
                saveConfig();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Arena has been added to kit!");
        }else{
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private boolean kitExists(String kitName) {
        return Neptune.kitsConfig.contains("kits." + kitName);
    }

    private void saveConfig() {
        try {
            Neptune.kitsConfig.save(Neptune.kits);
            Neptune.kitsConfig.load(Neptune.kits);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void showKitCommands(Player player) {
        player.sendMessage(CC.translate("&7&m------------------------------------------------"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&8- &7Kit commands:"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&b/kit create &8<&7name&8> &7- &8(&7Create a kit&8)"));
        player.sendMessage(CC.translate("&b/kit set &8<&7name&8> &7- &8(&7Set a kit's inventory&8)"));
        player.sendMessage(CC.translate("&b/kit give &8<&7name&8> &7- &8(&7Give a kit's inventory&8)"));
        player.sendMessage(CC.translate("&b/kit seticon &8<&7name&8> &7- &8(&7Set a kit's icon&8)"));
        player.sendMessage(CC.translate("&b/kit setdescription &8<&7name&8> &8<&7description&8> &7- &8(&7Set a kit's description&8)"));
        player.sendMessage(CC.translate("&b/kit rules &8<&7name&8> &8<&7rule&8> &7- &8(&7Set a kit's rule(s)&8)"));
        player.sendMessage(CC.translate("&b/kit arenas &8<&7name&8> &8<&7arena&8> &7- &8(&7Add an Arena to kit&8)"));
        player.sendMessage(CC.translate("&b/kit ranked &8<&7name&8> &8<&7true/false&8> &7- &8(&7Set a kit to ranke&8)"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7&m------------------------------------------------"));
    }
}
