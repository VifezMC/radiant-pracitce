package neptune.dev.managers;

import com.sun.org.apache.xpath.internal.operations.Div;
import neptune.dev.Neptune;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
    public static File stats;
    public static FileConfiguration statsConfig;
    public static File division;
    public static FileConfiguration divisionConfig;

    public static void registerConfigs() {
        // ARENAS
        saveResourceIfNotExists("cache/arenas.yml");
        arena = new File(Neptune.instance.getDataFolder(), "cache/arenas.yml");
        arenaConfig = YamlConfiguration.loadConfiguration(arena);
        arenaManager.loadArenas();

        // MAIN CONFIG
        saveResourceIfNotExists("config.yml");
        config = new File(Neptune.instance.getDataFolder(), "config.yml");
        pluginConfig = YamlConfiguration.loadConfiguration(config);

        // MESSAGES CONFIG
        saveResourceIfNotExists("features/messages.yml");
        messages = new File(Neptune.instance.getDataFolder(), "features/messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messages);

        // SPAWN ITEMS
        saveResourceIfNotExists("features/spawn-items.yml");
        spawnItems = new File(Neptune.instance.getDataFolder(), "features/spawn-items.yml");
        spawnItemsConfig = YamlConfiguration.loadConfiguration(spawnItems);

        // KITS CONFIG
        saveResourceIfNotExists("cache/kits.yml");
        kits = new File(Neptune.instance.getDataFolder(), "cache/kits.yml");
        kitsConfig = YamlConfiguration.loadConfiguration(kits);
        kitManager.loadKits();

        // SCOREBOARD CONFIG
        saveResourceIfNotExists("ui/scoreboard.yml");
        scoreboard = new File(Neptune.instance.getDataFolder(), "ui/scoreboard.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboard);

        // Menus CONFIG
        saveResourceIfNotExists("ui/menus.yml");
        menus = new File(Neptune.instance.getDataFolder(), "ui/menus.yml");
        menusConfig = YamlConfiguration.loadConfiguration(menus);

        // STATS CONFIG
        saveResourceIfNotExists("cache/stats.yml");
        stats = new File(Neptune.instance.getDataFolder(), "cache/stats.yml");
        statsConfig = YamlConfiguration.loadConfiguration(stats);

        // DIVISION CONFIG
        saveResourceIfNotExists("features/divisions.yml");
        division = new File(Neptune.instance.getDataFolder(), "features/divisions.yml");
        divisionConfig = YamlConfiguration.loadConfiguration(division);
        divisionsManager.loadDivisions();
    }


    private static void saveResourceIfNotExists(String resourcePath) {
        File file = new File(Neptune.instance.getDataFolder(), resourcePath);
        if (!file.exists()) {
            Neptune.instance.saveResource(resourcePath, false);
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
