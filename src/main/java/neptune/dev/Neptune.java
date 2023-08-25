package neptune.dev;

import lombok.Getter;
import neptune.dev.commands.admin.*;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.Scoreboard;
import neptune.dev.ui.StatsInventory;
import neptune.dev.ui.unranked.*;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.render.Console;
import neptune.dev.utils.assemble.Assemble;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

@Getter
public class Neptune extends JavaPlugin {

  @Getter
  public static Neptune instance;

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
  public static File menus;
  public static FileConfiguration menusConfig;
  public static File divisions;
  public static FileConfiguration divisionsConfig;

  @Override
  public void onEnable() {
    instance = this;
    arenaManager = new ArenaManager();

    // PEARL COOLDOWN
    Cooldowns.createCooldown("enderpearl");

    // CONFIG
    registerConfigs();

    // SCOREBOARD
    Assemble assemble = new Assemble(this, new Scoreboard());

    // LISTENERS
    registerEventListeners();
    Console.sendMessage("&7[&9" + Constants.PlName + "&7] &aLoaded listeners!");

    // COMMANDS
    registerCommands();
    Console.sendMessage("&7[&9" + Constants.PlName + "&7] &aLoaded commands!");

    // START MESSSAGE
    Console.sendMessage("&9" + Constants.PlName + " Loaded successfully");
    Console.sendMessage("&9Author: &f" + Constants.Autor);
    Console.sendMessage("&9Version: &f" + Constants.Ver);
    Console.sendMessage("&9Discord: &f" + Constants.Discord);
  }

  public void registerConfigs() {

    // ARENAS
    saveResourceIfNotExists("cache/arenas.yml", false);
    arena = new File(this.getDataFolder(), "cache/arenas.yml");
    arenaConfig = YamlConfiguration.loadConfiguration(arena);
    arenaManager.loadArenas();

    // MAIN CONFIG
    saveResourceIfNotExists("config.yml", false);
    config = new File(this.getDataFolder(), "config.yml");
    pluginConfig = YamlConfiguration.loadConfiguration(config);

    // MESSAGES CONFIG
    saveResourceIfNotExists("features/messages.yml", false);
    messages = new File(this.getDataFolder(), "features/messages.yml");
    messagesConfig = YamlConfiguration.loadConfiguration(messages);

    // SPAWN ITEMS
    saveResourceIfNotExists("features/spawn-items.yml", false);
    spawnItems = new File(this.getDataFolder(), "features/spawn-items.yml");
    spawnItemsConfig = YamlConfiguration.loadConfiguration(spawnItems);

    // KITS CONFIG
    saveResourceIfNotExists("cache/kits.yml", false);
    kits = new File(this.getDataFolder(), "cache/kits.yml");
    kitsConfig = YamlConfiguration.loadConfiguration(kits);

    // SCOREBOARD CONFIG
    saveResourceIfNotExists("ui/scoreboard.yml", false);
    scoreboard = new File(this.getDataFolder(), "ui/scoreboard.yml");
    scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboard);

    // Menus CONFIG
    saveResourceIfNotExists("ui/menus.yml", false);
    menus = new File(this.getDataFolder(), "ui/menus.yml");
    menusConfig = YamlConfiguration.loadConfiguration(menus);

    // Menus CONFIG
    saveResourceIfNotExists("features/divisions.yml", false);
    divisions = new File(this.getDataFolder(), "features/divisions.yml");
    divisionsConfig = YamlConfiguration.loadConfiguration(divisions);
  }

  private void registerEventListeners() {
    Arrays.asList(
            new PlayerJoin(),
            new SpawnListeners(),
            new WorldListener(),
            new GameListener(),
            new StatsInventory(),
            new UnrankedInventoryModern()
    ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
  }

  private void registerCommands() {
    createCMD("setspawn", new SetSpawnCMD());
    createCMD("arena", new ArenaCMD());
    createCMD("neptune", new MainCMD());
    createCMD("kit", new KitsCMD());
    createCMD("queue", new QueueCMD());
    createCMD("ping", new PingCMD());
    createCMD("leavequeue", new LeaveQueueCMD());
    createCMD("stats", new StatsCMD());
    createCMD("unranked", new UnrankedCMD());
  }


  private void saveResourceIfNotExists(String resourcePath, boolean replace) {
    File file = new File(this.getDataFolder(), resourcePath);
    if (!file.exists()) {
      saveResource(resourcePath, replace);
    }
  }

  private void createCMD(String command, CommandExecutor classFile){
    getCommand(command).setExecutor(classFile);
  }

  @Override
  public void onDisable() {
    getServer().getPluginManager().disablePlugin(this);
  }
}
