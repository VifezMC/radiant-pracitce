package neptune.dev;

import neptune.dev.commands.admin.*;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.Scoreboard;
import neptune.dev.ui.StatsInventory;
import neptune.dev.ui.UnrankedInventory;
import neptune.dev.utils.Console;
import neptune.dev.utils.assemble.Assemble;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public class Neptune extends JavaPlugin {
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

  @Override
  public void onEnable() {
    instance = this;
    arenaManager = new ArenaManager();

    // CONFIG
    registerConfigs();

    // SCOREBOARD
    Assemble assemble = new Assemble(this, new Scoreboard());

    // LIST LISTENERS
    registerEventListeners();

    // COMMANDS
    registerCommands();

    // START MESSSAGE
    Console.sendMessage("&9Neptune Loaded successfully");
    Console.sendMessage("&9Author: &f" + Constants.Autor);
    Console.sendMessage("&9Version: &f" + Constants.Ver);
    Console.sendMessage("&9Discord: &f" + Constants.Discord);
  }

  public void registerConfigs() {
    // ARENAS
    saveResourceIfNotExists("arenas.yml", false);
    arena = new File(this.getDataFolder(), "arenas.yml");
    arenaConfig = YamlConfiguration.loadConfiguration(arena);
    arenaManager.loadArenas();

    // MAIN CONFIG
    saveResourceIfNotExists("config.yml", false);
    config = new File(this.getDataFolder(), "config.yml");
    pluginConfig = YamlConfiguration.loadConfiguration(config);

    // MESSAGES CONFIG
    saveResourceIfNotExists("messages.yml", false);
    messages = new File(this.getDataFolder(), "messages.yml");
    messagesConfig = YamlConfiguration.loadConfiguration(messages);

    // SPAWN ITEMS
    saveResourceIfNotExists("spawn-items.yml", false);
    spawnItems = new File(this.getDataFolder(), "spawn-items.yml");
    spawnItemsConfig = YamlConfiguration.loadConfiguration(spawnItems);

    // KITS CONFIG
    saveResourceIfNotExists("kits.yml", false);
    kits = new File(this.getDataFolder(), "kits.yml");
    kitsConfig = YamlConfiguration.loadConfiguration(kits);

    // SCOREBOARD CONFIG
    saveResourceIfNotExists("scoreboard.yml", false);
    scoreboard = new File(this.getDataFolder(), "scoreboard.yml");
    scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboard);
  }

  private void registerEventListeners() {
    Arrays.asList(
            new PlayerJoin(),
            new SpawnListeners(),
            new WorldListener(),
            new GameListener(),
            new StatsInventory(),
            new UnrankedInventory()
    ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
  }

  private void registerCommands() {
    getCommand("setspawn").setExecutor(new SetSpawnCMD());
    getCommand("arena").setExecutor(new ArenaCMD());
    getCommand("neptune").setExecutor(new MainCMD());
    getCommand("kit").setExecutor(new KitsCMD());
    getCommand("queue").setExecutor(new QueueCMD());
    getCommand("ping").setExecutor(new PingCMD());
    getCommand("leavequeue").setExecutor(new LeaveQueueCMD());
    getCommand("stats").setExecutor(new StatsCMD());
    getCommand("unranked").setExecutor(new UnrankedCMD());
  }

  private void saveResourceIfNotExists(String resourcePath, boolean replace) {
    File file = new File(this.getDataFolder(), resourcePath);
    if (!file.exists()) {
      saveResource(resourcePath, replace);
    }
  }

  @Override
  public void onDisable() {
    getServer().getPluginManager().disablePlugin(this);
  }
  public static ArenaManager getArenaManager() {
    return arenaManager;
  }
}
