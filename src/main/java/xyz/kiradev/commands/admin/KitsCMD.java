package xyz.kiradev.commands.admin;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.kiradev.Constants;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.KitManager;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.Console;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class KitsCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."));
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission(Constants.PlName + ".kits")) {
            p.sendMessage(CC.translate("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length >= 1) {
            String action = args[0].toLowerCase();
            switch (action) {
                case "create":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        createKit(kitName);
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        p.sendMessage(CC.GREEN + "Kit has been created!");

                    } else {
                        p.sendMessage(CC.RED + "Invalid command usage. Use /kit create <name>.");
                    }
                    break;
                case "save":
                    saveKits();
                    p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    p.sendMessage(CC.GREEN + "Kits have been saved!");
                    break;
                case "set":
                    if (args.length >= 2) {
                        String subAction = args[1].toLowerCase();
                        switch (subAction) {
                            case "description":
                                if (args.length >= 3) {
                                    String kitName = args[2];
                                    StringBuilder description = new StringBuilder();
                                    for (int i = 3; i < args.length; i++) {
                                        description.append(args[i]).append(" ");
                                    }
                                    setDesc(kitName, description.toString(), p);
                                    p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

                                } else {
                                    p.sendMessage(CC.RED + "Invalid command usage. Use /kit set description <name> <description>.");
                                }
                                break;
                            case "icon":
                                if (args.length >= 3) {
                                    String kitName = args[2];
                                    setIcon(kitName, p.getItemInHand(), p);
                                    p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

                                } else {
                                    p.sendMessage(CC.RED + "Invalid command usage. Use /kit set icon <name>.");
                                }
                                break;
                            case "inv":
                                if (args.length >= 3) {
                                    String kitName = args[2];
                                    setItemsAndArmour(kitName, p);
                                    p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

                                } else {
                                    p.sendMessage(CC.RED + "Invalid command usage. Use /kit set inv <name>.");
                                }
                                break;
                            case "rule":
                                if (args.length >= 3) {
                                    String kitName = args[2];
                                    String rule = args[3].toLowerCase();
                                    if (rule.equals(null)) {
                                        p.sendMessage(CC.RED + "Invalid command usage. Use /kit set rule <name> <rule>.");
                                    }
                                    addRule(kitName, rule, p);

                                } else {
                                    p.sendMessage(CC.RED + "Invalid command usage. Use /kit set rule <name> <rule>.");
                                }
                                break;
                            case "ranked":
                                if (args.length >= 3) {
                                    String kitName = args[1];
                                    String rankedValue = args[2].toLowerCase();
                                    setRankedStatus(kitName, rankedValue, p);

                                } else {
                                    p.sendMessage(CC.RED + "Invalid command usage. Use /kit ranked <name> <true/false>.");
                                }
                                break;
                            default:
                                showSetKitCommands(p);
                                break;
                        }
                    } else {
                        showSetKitCommands(p);
                    }
                    break;
                case "get":
                    if (args.length >= 2) {
                        String kitName = args[1];
                        giveKit(kitName, p);
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

                    } else {
                        p.sendMessage(CC.RED + "Invalid command usage. Use /kit give <name>.");
                    }
                    break;
                case "whitelistarena":
                    if (args.length >= 3) {
                        String kitName = args[1];
                        String arena = args[2];
                        addArena(kitName, arena, p);

                    } else {
                        p.sendMessage(CC.RED + "Invalid command usage. Use /kit whitelistarena <name> <arena>.");
                    }
                    break;
                default:
                    p.sendMessage(CC.RED + "Invalid command. Use /kit for available commands.");
                    break;
            }
        } else {

            showKitCommands(p);
        }
        return true;
    }

    private void createKit(String name) {
        ConfigManager.kitsConfig.set("kits." + name + ".items", "None");
        ConfigManager.kitsConfig.set("kits." + name + ".armour", "None");
        ConfigManager.kitsConfig.set("kits." + name + ".icon", "None");
        ConfigManager.kitsConfig.set("kits." + name + ".arenas", "None");
        ConfigManager.kitsConfig.set("kits." + name + ".rules", "None");
        ConfigManager.kitsConfig.set("kits." + name + ".description", "None");
        ConfigManager.saveConfig(ConfigManager.kits, ConfigManager.kitsConfig);
    }

    private void saveKits() {
        ConfigManager.kitManager = new KitManager();
        ConfigManager.registerConfigs();
        Console.sendMessage("&aReloaded kits");
    }

    private void setItemsAndArmour(String kitName, Player player) {
        if (kitExists(kitName)) {
            ItemStack[] content = player.getInventory().getContents();
            ItemStack[] armour = player.getInventory().getArmorContents();

            setItems(kitName, content);
            setArmour(kitName, armour);
            ConfigManager.saveConfig(ConfigManager.kits, ConfigManager.kitsConfig);
            player.sendMessage(CC.GREEN + "Kit has been set to your inventory!");
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void setItems(String location, ItemStack[] items) {
        ConfigManager.kitsConfig.set("kits." + location + ".items", Arrays.asList(items));
    }

    private void setDesc(String location, String desc, Player p) {
        ConfigManager.kitsConfig.set("kits." + location + ".description", desc);
        p.sendMessage(CC.GREEN + "Kit description has been set!");
    }

    private void setIcon(String location, ItemStack item, Player player) {
        if (kitExists(location)) {
            ConfigManager.kitsConfig.set("kits." + location + ".icon", item);
            player.sendMessage(CC.GREEN + "Kit icon has been set!");
            ConfigManager.saveConfig(ConfigManager.kits, ConfigManager.kitsConfig);
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + location + "' does not exist.");
        }
    }

    private void setArmour(String location, ItemStack[] items) {
        ConfigManager.kitsConfig.set("kits." + location + ".armour", Arrays.asList(items));
    }

    private void giveKit(String kitName, Player player) {
        if (kitExists(kitName)) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            ItemStack[] inventoryContents = Objects.requireNonNull(KitManager.getKit(kitName)).getItems();
            ItemStack[] armorContents = Objects.requireNonNull(KitManager.getKit(kitName)).getArmour();
            player.getInventory().setContents(inventoryContents);
            player.getInventory().setArmorContents(armorContents);
            player.updateInventory();
            player.sendMessage(CC.GREEN + "There you go!");
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void setRankedStatus(String kitName, String rankedValue, Player player) {
        if (kitExists(kitName)) {
            boolean isRanked = Boolean.parseBoolean(rankedValue);
            ConfigManager.kitsConfig.set("kits." + kitName + ".ranked", isRanked);
            ConfigManager.saveConfig(ConfigManager.kits, ConfigManager.kitsConfig);
            player.sendMessage(CC.GREEN + "Kit ranked status has been updated!");
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void addRule(String kitName, String rule, Player player) {
        if (kitExists(kitName)) {
            List<String> validRules = Arrays.asList("boxing", "build", "sumo", "nodamage", "nohunger", "feezeonspawn");
            if (validRules.contains(rule)) {
                List<String> rules = ConfigManager.kitsConfig.getStringList("kits." + kitName + ".rules");
                rules.add(rule);
                ConfigManager.kitsConfig.set("kits." + kitName + ".rules", rules);
                ConfigManager.saveConfig(ConfigManager.kits, ConfigManager.kitsConfig);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Kit rule has been added!");
            } else {
                player.sendMessage(CC.RED + "Invalid rules");
                player.sendMessage(CC.RED + "Valid rules: boxing, build, sumo, nodamage, nohunger, feezeonspawn");
            }
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private void addArena(String kitName, String arena, Player player) {
        if (kitExists(kitName)) {
            List<String> arenas = ConfigManager.kitsConfig.getStringList("kits." + kitName + ".arenas");
            arenas.add(arena);
            ConfigManager.kitsConfig.set("kits." + kitName + ".arenas", arenas);
            ConfigManager.saveConfig(ConfigManager.kits, ConfigManager.kitsConfig);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            player.sendMessage(CC.GREEN + "Arena has been added to kit!");
        } else {
            player.sendMessage(CC.RED + "Kit with name '" + kitName + "' does not exist.");
        }
    }

    private boolean kitExists(String kitName) {
        return ConfigManager.kitsConfig.contains("kits." + kitName);
    }


    private void showKitCommands(Player player) {
        player.sendMessage(CC.translate("&c------------------------------------------------------"));
        player.sendMessage(CC.translate("&c&lRadiant Kit Setup"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c/kit create &8<&7name&8> &7- &8(&7Create a kit&8)"));
        player.sendMessage(CC.translate("&c/kit get &8<&7name&8> &7- &8(&7Get a kit's inventory&8)"));
        player.sendMessage(CC.translate("&c/kit set &7- &8(&7Opens set sub commands&8)"));
        player.sendMessage(CC.translate("&c/kit save &7- &8(&7Save kits&8)"));
        player.sendMessage(CC.translate("&c/kit whitelistarena &8<&7name&8> &8<&7arena&8> &7- &8(&7Whitelist an arena to kit&8)"));
        player.sendMessage(CC.translate("&c------------------------------------------------------"));
    }

    private void showSetKitCommands(Player player) {
        player.sendMessage(CC.translate("&c------------------------------------------------------"));
        player.sendMessage(CC.translate("&d&lRadiant Kit Sub Commnd &8(&7Set&8)"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c/kit set inv &8<&7name&8> &7- &8(&7Set a kit's inventory&8)"));
        player.sendMessage(CC.translate("&c/kit set icon &8<&7name&8> &7- &8(&7Set a kit's icon&8)"));
        player.sendMessage(CC.translate("&c/kit set description &8<&7name&8> &8<&7description&8> &7- &8(&7Set a kit's description&8)"));
        player.sendMessage(CC.translate("&c/kit set rule &8<&7name&8> &8<&7rule&8> &7- &8(&7Set a kit's rule(s)&8)"));
        player.sendMessage(CC.translate("&c------------------------------------------------------"));
    }
}