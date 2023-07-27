package neptune.dev;

import neptune.dev.commands.admin.*;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.utils.Console;
import org.bukkit.configuration.file.*;
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

  @Override
  public void onEnable() {
    instance = this;

    // CONFIG
    registerConfigs();

    // LIST LISTENERS
    Arrays.asList(
            new PlayerJoin(),
            new SpawnListeners(),
            new WorldListener(),
            new GameListener()
    ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));



    // COMMANDS
    getCommand("setspawn").setExecutor(new SetSpawnCMD());
    getCommand("arena").setExecutor(new ArenaCMD());
    getCommand("neptune").setExecutor(new MainCMD());
    getCommand("kit").setExecutor(new KitsCMD());
    getCommand("queue").setExecutor(new QueueCMD());
    getCommand("ping").setExecutor(new PingCMD());
    getCommand("leavequeue").setExecutor(new LeaveQueueCMD());

    // START MESSSAGE
    Console.sendMessage("&bNeptune Loaded successfully");
    Console.sendMessage("&bAuthor: &f" + Constants.Autor);
    Console.sendMessage("&bVersion: &f" + Constants.Ver);
    Console.sendMessage("&bDiscord: &f" + Constants.Discord);
  }

  public void registerConfigs() {

    // ARENAS
    saveResource("arenas.yml", false);
    arena = new File(this.getDataFolder(), "arenas.yml");
    arenaConfig = YamlConfiguration.loadConfiguration(arena);

    // MAIN CONFIG
    saveResource("config.yml", false);
    config = new File(this.getDataFolder(), "config.yml");
    pluginConfig = YamlConfiguration.loadConfiguration(config);

    // MESSAGES CONFIG
    saveResource("messages.yml", false);
    messages = new File(this.getDataFolder(), "messages.yml");
    messagesConfig = YamlConfiguration.loadConfiguration(messages);

    // SPAWN ITEMS
    saveResource("spawn-items.yml", false);
    spawnItems = new File(this.getDataFolder(), "spawn-items.yml");
    spawnItemsConfig = YamlConfiguration.loadConfiguration(spawnItems);
    // SPAWN ITEMS
    saveResource("kits.yml", false);
    kits = new File(this.getDataFolder(), "kits.yml");
    kitsConfig = YamlConfiguration.loadConfiguration(kits);
  }

  @Override
  public void onDisable() {}
}
