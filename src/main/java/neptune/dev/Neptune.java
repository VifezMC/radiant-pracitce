package neptune.dev;

import lombok.Getter;
import neptune.dev.commands.admin.*;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.managers.ArenaManager;
import neptune.dev.managers.Scoreboard;
import neptune.dev.player.Profile;
import neptune.dev.player.ProfileManager;
import neptune.dev.storage.MongoManager;
import neptune.dev.ui.StatsInventory;
import neptune.dev.ui.UnrankedInventoryModern;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.render.CC;
import neptune.dev.utils.render.Console;
import neptune.dev.utils.assemble.Assemble;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

@Getter
public class Neptune extends JavaPlugin {

  @Getter
  public static Neptune instance;
  private MongoManager mongoManager;
  private ProfileManager profileManager;

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

    // LIST LISTENERS
    registerEventListeners();
    logMessage("&7[&9Neptune&7] &aLoaded listeners!");

    // COMMANDS
    registerCommands();
    logMessage("&7[&9Neptune&7] &aLoaded commands!");


    loadManagers();
    logMessage("&7[&9Neptune&7] &aLoaded managers!");

    // START MESSSAGE
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

    for (Profile profile : this.getProfileManager().getAllProfiles()) {
      this.getProfileManager().save(profile);
    }

    if (mongoManager != null) {
      mongoManager.close();
    }
    getServer().getPluginManager().disablePlugin(this);
  }


  public static ArenaManager getArenaManager() {
    return arenaManager;
  }

  private void loadManagers() {
    if(pluginConfig.getString("save-type").contains("Mongo")){
      mongoManager = new MongoManager(this);
      profileManager = new ProfileManager();
    }
  }

  public static void logMessage(String string) {
    Bukkit.getConsoleSender().sendMessage(CC.translate(string));
  }
}
