package neptune.dev;

import lombok.Getter;
import neptune.dev.commands.admin.*;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.Scoreboard; 
import neptune.dev.ui.StatsInventory;
import neptune.dev.ui.UnrankedInventoryModern;
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
  @Getter
  public static ArenaManager arenaManager;
  public static File menus;
  public static FileConfiguration menusConfig;
  public static File divisions;
  public static FileConfiguration divisionsConfig;

  @Override
  public void onEnable() {
    instance = this;
    arenaManager = new ArenaManager();

    // PEARL COOL-DOWN
    Cooldowns.createCooldown("enderpearl");

    // CONFIG
    registerConfigs();

    // SCOREBOARD
    Assemble assemble = new Assemble(this, new Scoreboard());

    // LISTENERS
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

    // CONFIGS
    createResource(arenaConfig, arena, "cache/arenas.yml");
    createResource(pluginConfig, config, "config.yml");
    createResource(messagesConfig, messages, "features/messages.yml");
    createResource(spawnItemsConfig, spawnItems, "features/spawn-items.yml");
    createResource(kitsConfig, kits, "cache/kits.yml");
    createResource(scoreboardConfig, scoreboard, "ui/scoreboard.yml");
    createResource(menusConfig, menus, "ui/menus.yml");
    createResource(divisionsConfig, divisions, "features/divisions.yml");
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
    cmdCreate("setspawn", new SetSpawnCMD());
    cmdCreate("arena", new ArenaCMD());
    cmdCreate("neptune", new MainCMD());
    cmdCreate("kit", new KitsCMD());
    cmdCreate("queue", new QueueCMD());
    cmdCreate("ping", new PingCMD());
    cmdCreate("leavequeue", new LeaveQueueCMD());
    cmdCreate("stats", new StatsCMD());
    cmdCreate("unranked", new UnrankedCMD());
  }


  private void saveResourceIfNotExists(String resourcePath) {
    File file = new File(this.getDataFolder(), resourcePath);
    if (!file.exists()) {
      saveResource(resourcePath, false);
    }
  }

  public void cmdCreate(String command, CommandExecutor commandFile){
    getCommand(command).setExecutor(commandFile);
  }

  public void createResource(FileConfiguration  fileConfig, File file, String path){
    saveResourceIfNotExists(path);
    file = new File(this.getDataFolder(), path);
    fileConfig = YamlConfiguration.loadConfiguration(file);
  }

  @Override
  public void onDisable() {
    getServer().getPluginManager().disablePlugin(this);
  }
}