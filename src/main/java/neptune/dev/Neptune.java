package neptune.dev;

import lombok.Getter;
import neptune.dev.commands.admin.ArenaCMD;
import neptune.dev.commands.admin.KitsCMD;
import neptune.dev.commands.admin.MainCMD;
import neptune.dev.commands.admin.SetSpawnCMD;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.KitManager;
import neptune.dev.managers.Scoreboard;
import neptune.dev.ui.StatsInventory;
import neptune.dev.ui.ranked.RankedModernUI;
import neptune.dev.ui.unranked.UnrankedInventoryModern;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.assemble.Assemble;
import neptune.dev.utils.render.Console;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.cert.CRLException;
import java.util.Arrays;

@Getter
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
  public static KitManager kitManager;
  public static File menus;
  public static FileConfiguration menusConfig;
  public static File divisions;
  public static FileConfiguration divisionsConfig;

  @Override
  public void onEnable() {
    instance = this;
    loadManagers();
    Console.sendMessage("&7[&9Neptune&7] &aLoaded managers!");

    // PEARL COOLDOWN
    Cooldowns.createCooldown("enderpearl");

    // CONFIG
    registerConfigs();

    // SCOREBOARD
    Assemble assemble = new Assemble(this, new Scoreboard());

    // LIST LISTENERS
    registerEventListeners();
    Console.sendMessage("&7[&9Neptune&7] &aLoaded listeners!");

    // COMMANDS
    registerCommands();
    Console.sendMessage("&7[&9Neptune&7] &aLoaded commands!");

    // START MESSAGE
    Console.sendMessage("&9Neptune Loaded successfully");
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
    kitManager.loadKits();

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

  public static void loadManagers() {
    arenaManager = new ArenaManager();
    kitManager = new KitManager();
  }

  public static void redloadManagers() {
    loadManagers();
    instance.registerConfigs();
  }

  private void registerEventListeners() {
    Arrays.asList(
            new PlayerJoin(),
            new SpawnListeners(),
            new WorldListener(),
            new GameListener(),
            new StatsInventory(),
            new UnrankedInventoryModern(kitManager),
            new RankedModernUI(),
            new BlockListener()
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
    createCMD("ranked", new RankedCMD());
  }

  private void saveResourceIfNotExists(String resourcePath, boolean replace) {
    File file = new File(this.getDataFolder(), resourcePath);
    if (!file.exists()) {
      saveResource(resourcePath, replace);
    }
  }

  private void createCMD(String cmd, CommandExecutor commandline){
    getCommand(cmd).setExecutor(commandline);
  }

  @Override
  public void onDisable() {
    getServer().getPluginManager().disablePlugin(this);
  }

  public static ArenaManager getArenaManager() {
    return arenaManager;
  }

  public static KitManager getKitManager() {
    return kitManager;
  }
}
