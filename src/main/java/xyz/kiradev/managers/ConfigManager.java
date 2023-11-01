package xyz.kiradev.managers;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.kiradev.Stellar;
import xyz.kiradev.utils.render.CC;
import xyz.kiradev.utils.render.Console;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    public static File arena;
    public static FileConfiguration arenaConfig;
    public static File config;
    public static FileConfiguration pluginConfig;
    public static File messages;
    public static FileConfiguration messagesConfig;
    public static File spawnItems;
    public static FileConfiguration spawnItemsConfig;
    public static File kits;
    public static FileConfiguration kitsConfig;
    public static File scoreboard;
    public static FileConfiguration scoreboardConfig;
    public static ArenaManager arenaManager;
    public static KitManager kitManager;
    public static DivisionsManager divisionsManager;
    public static File menus;
    public static FileConfiguration menusConfig;
    public static File database;
    public static FileConfiguration databaseConfig;
    public static File division;
    public static FileConfiguration divisionConfig;

    public static void registerConfigs() {
        // ARENAS
        saveResourceIfNotExists("cache/arenas.yml");
        arena = new File(Stellar.instance.getDataFolder(), "cache/arenas.yml");
        arenaConfig = YamlConfiguration.loadConfiguration(arena);
        arenaManager.loadArenas();

        // MAIN CONFIG
        saveResourceIfNotExists("config.yml");
        config = new File(Stellar.instance.getDataFolder(), "config.yml");
        pluginConfig = YamlConfiguration.loadConfiguration(config);

        // MESSAGES CONFIG
        saveResourceIfNotExists("features/messages.yml");
        messages = new File(Stellar.instance.getDataFolder(), "features/messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messages);

        // SPAWN ITEMS
        saveResourceIfNotExists("features/spawn-items.yml");
        spawnItems = new File(Stellar.instance.getDataFolder(), "features/spawn-items.yml");
        spawnItemsConfig = YamlConfiguration.loadConfiguration(spawnItems);

        // KITS CONFIG
        saveResourceIfNotExists("cache/kits.yml");
        kits = new File(Stellar.instance.getDataFolder(), "cache/kits.yml");
        kitsConfig = YamlConfiguration.loadConfiguration(kits);
        kitManager.loadKits();

        // SCOREBOARD CONFIG
        saveResourceIfNotExists("ui/scoreboard.yml");
        scoreboard = new File(Stellar.instance.getDataFolder(), "ui/scoreboard.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboard);

        // Menus CONFIG
        saveResourceIfNotExists("ui/menus.yml");
        menus = new File(Stellar.instance.getDataFolder(), "ui/menus.yml");
        menusConfig = YamlConfiguration.loadConfiguration(menus);

        // STATS CONFIG
        saveResourceIfNotExists("cache/data.yml");
        database = new File(Stellar.instance.getDataFolder(), "cache/data.yml");
        databaseConfig = YamlConfiguration.loadConfiguration(database);

        // DIVISION CONFIG
        saveResourceIfNotExists("features/divisions.yml");
        division = new File(Stellar.instance.getDataFolder(), "features/divisions.yml");
        divisionConfig = YamlConfiguration.loadConfiguration(division);
        divisionsManager.loadDivisions();
    }


    private static void saveResourceIfNotExists(String resourcePath) {
        File file = new File(Stellar.instance.getDataFolder(), resourcePath);
        if (!file.exists()) {
            Stellar.instance.saveResource(resourcePath, false);
        }
    }

    public static void saveConfig(File config, FileConfiguration fileConfiguration) {
        try {
            fileConfiguration.save(config);
            fileConfiguration.load(config);
        } catch (IOException | InvalidConfigurationException e) {
            Console.sendMessage(CC.RED + "Error occurred while saving config!");
        }
    }
}
