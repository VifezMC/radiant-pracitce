package xyz.kiradev.managers;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.kiradev.Stellar;
import xyz.kiradev.party.PartyManager;
import xyz.kiradev.tab.TabHandler;
import xyz.kiradev.tab.TabImpl;
import xyz.kiradev.tab.v1_8_r3.v1_8_R3TabAdapter;
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
    public static PartyManager partyManager;
    public static File menus;
    public static FileConfiguration menusConfig;
    public static File flatfile;
    public static FileConfiguration flatfileConfig;
    public static File division;
    public static FileConfiguration divisionConfig;
    public static File tab;
    public static FileConfiguration tabConfig;
    public static File head;
    public static FileConfiguration headConfig;
    public static File database;
    public static FileConfiguration databaseconfig;


    public static void registerConfigs() {
        // ARENAS
        saveResourceIfNotExists("cache/arenas.yml");
        arena = new File(Stellar.getInstance().getDataFolder(), "cache/arenas.yml");
        arenaConfig = YamlConfiguration.loadConfiguration(arena);

        // MAIN CONFIG
        saveResourceIfNotExists("config.yml");
        config = new File(Stellar.getInstance().getDataFolder(), "config.yml");
        pluginConfig = YamlConfiguration.loadConfiguration(config);

        // MESSAGES CONFIG
        saveResourceIfNotExists("features/messages.yml");
        messages = new File(Stellar.getInstance().getDataFolder(), "features/messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messages);

        // SPAWN ITEMS
        saveResourceIfNotExists("features/items.yml");
        spawnItems = new File(Stellar.getInstance().getDataFolder(), "features/items.yml");
        spawnItemsConfig = YamlConfiguration.loadConfiguration(spawnItems);

        // KITS CONFIG
        saveResourceIfNotExists("cache/kits.yml");
        kits = new File(Stellar.getInstance().getDataFolder(), "cache/kits.yml");
        kitsConfig = YamlConfiguration.loadConfiguration(kits);

        // SCOREBOARD CONFIG
        saveResourceIfNotExists("ui/scoreboard.yml");
        scoreboard = new File(Stellar.getInstance().getDataFolder(), "ui/scoreboard.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboard);

        // Menus CONFIG
        saveResourceIfNotExists("ui/menus.yml");
        menus = new File(Stellar.getInstance().getDataFolder(), "ui/menus.yml");
        menusConfig = YamlConfiguration.loadConfiguration(menus);

        // DATA CONFIG
        saveResourceIfNotExists("cache/data.yml");
        flatfile = new File(Stellar.getInstance().getDataFolder(), "cache/data.yml");
        flatfileConfig = YamlConfiguration.loadConfiguration(flatfile);

        // DIVISION CONFIG
        saveResourceIfNotExists("features/divisions.yml");
        division = new File(Stellar.getInstance().getDataFolder(), "features/divisions.yml");
        divisionConfig = YamlConfiguration.loadConfiguration(division);

        // HEAD CONFIG
        saveResourceIfNotExists("cache/heads.yml");
        head = new File(Stellar.getInstance().getDataFolder(), "cache/heads.yml");
        headConfig = YamlConfiguration.loadConfiguration(head);

        // TABLIST CONFIG
        saveResourceIfNotExists("ui/tablist.yml");
        tab = new File(Stellar.getInstance().getDataFolder(), "ui/tablist.yml");
        tabConfig = YamlConfiguration.loadConfiguration(tab);

        // MONGO/DATABASE CONFIG
        saveResourceIfNotExists("features/database.yml");
        database = new File(Stellar.getInstance().getDataFolder(), "features/database.yml");
        databaseconfig = YamlConfiguration.loadConfiguration(database);
    }

    private static void saveResourceIfNotExists(String resourcePath) {
        File file = new File(Stellar.getInstance().getDataFolder(), resourcePath);
        if (!file.exists()) {
            Stellar.getInstance().saveResource(resourcePath, false);
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
