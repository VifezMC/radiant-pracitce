package neptune.dev.commands.admin;

import neptune.dev.Constants;
import neptune.dev.Neptune;
import neptune.dev.utils.CC;
import neptune.dev.utils.Console;
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

        if (args.length == 2) {
            String kitName = args[1];

            if (args[0].equalsIgnoreCase("create")) {
                createKit(kitName);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Kit has been created!");
                return true;
            } else if (args[0].equalsIgnoreCase("set")) {
                ItemStack[] content = player.getInventory().getContents();
                ItemStack[] armour = player.getInventory().getArmorContents();

                setItems(kitName, content, player);
                setArmour(kitName, armour, player);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Kit inventory has been set!");
                return true;
            } else if (args[0].equalsIgnoreCase("give")) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                ItemStack[] inventoryContents = getItemsFromConfig(kitName);
                ItemStack[] armorContents = getArmorFromConfig(kitName);
                player.getInventory().setContents(inventoryContents);
                player.getInventory().setArmorContents(armorContents);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Kit has been given!");
                return true;
            } else if (args[0].equalsIgnoreCase("seticon")) {

                setIcon(kitName, player.getItemInHand());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                player.sendMessage(CC.GREEN + "Kit has been given!");
                return true;
            }
        }

        player.sendMessage(CC.translate("&7&m------------------------------------------------"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&8- &7Kit commands:"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&b/kit create &8<&7name&8> &7- &8(&7Create a kit&8)"));
        player.sendMessage(CC.translate("&b/kit set &8<&7name&8> &7- &8(&7Set a kit's inventory&8)"));
        player.sendMessage(CC.translate("&b/kit give &8<&7name&8> &7- &8(&7Give a kit's inventory&8)"));
        player.sendMessage(CC.translate("&b/kit seticon &8<&7name&8> &7- &8(&7Set a kit's icon&8)"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7&m------------------------------------------------"));

        return true;
    }

    private void createKit(String name) {
        Neptune.kitsConfig.set("kits." + name + ".items", "None");
        Neptune.kitsConfig.set("kits." + name + ".armour", "None");
        Neptune.kitsConfig.set("kits." + name + ".icon", "None");
        Neptune.kitsConfig.set("kits." + name + ".arenas", "None");

        saveConfig();
    }

    private void setItems(String location, ItemStack[] items, Player p) {
        try {
            if (Neptune.kitsConfig.get("kits." + location) == null) {
                throw new IllegalArgumentException("Location does not exist: " + location);
            }
            Neptune.kitsConfig.set("kits." + location + ".items", Arrays.asList(items));
            saveConfig();
        } catch (NullPointerException e) {
            p.sendMessage(CC.translate("&cLocation is null!"));
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Console.sendMessage("&cLocation doesn't exsit!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setIcon(String location, ItemStack items) {
        try {
            if (Neptune.kitsConfig.get("kits." + location) == null) {
                throw new IllegalArgumentException("Location does not exist: " + location);
            }
            Neptune.kitsConfig.set("kits." + location + ".icon", Arrays.asList(items));
            saveConfig();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Console.sendMessage("&cLocation doesn't exsit!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setArmour(String location, ItemStack[] items, Player p) {
        try {
            if (Neptune.kitsConfig.get("kits." + location) == null) {
                throw new IllegalArgumentException("Location does not exist: " + location);
            }
            Neptune.kitsConfig.set("kits." + location + ".armour", Arrays.asList(items));
            saveConfig();
        } catch (NullPointerException e) {
            p.sendMessage(CC.translate("&cLocation is null!"));
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Console.sendMessage("&cLocation doesn't exsit!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ItemStack[] getItemsFromConfig(String location) {
        return ((List<ItemStack>) Neptune.kitsConfig.get("kits." + location + ".items")).toArray(new ItemStack[0]);
    }

    private ItemStack[] getArmorFromConfig(String location) {
        return ((List<ItemStack>) Neptune.kitsConfig.get("kits." + location + ".armour")).toArray(new ItemStack[0]);
    }

    private void saveConfig() {
        try {
            Neptune.kitsConfig.save(Neptune.kits);
            Neptune.kitsConfig.load(Neptune.kits);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
