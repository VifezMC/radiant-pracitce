package xyz.kiradev;

import lombok.Getter;
import org.bukkit.Bukkit;
import xyz.kiradev.commands.user.*;
import xyz.kiradev.listeners.*;
import xyz.kiradev.managers.*;
import xyz.kiradev.commands.admin.ArenaCMD;
import xyz.kiradev.commands.admin.KitsCMD;
import xyz.kiradev.commands.admin.MainCMD;
import xyz.kiradev.commands.admin.SetSpawnCMD;
import xyz.kiradev.ui.unranked.UnrankedInventoryModern;
import xyz.kiradev.utils.Cooldowns;
import xyz.kiradev.utils.assemble.Assemble;
import xyz.kiradev.utils.render.Console;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Getter
public class Stellar extends JavaPlugin {

  public static Stellar instance;

  @Override
  public void onEnable() {
    instance = this;
    Plugin luckPermsPlugin = getServer().getPluginManager().getPlugin("PlaceholderAPI");
    if (luckPermsPlugin != null && luckPermsPlugin.isEnabled()) {
      new Placeholder(this).register();
    }
    loadManagers();
    Console.sendMessage(" ");

    // PEARL COOLDOWN
    Cooldowns.createCooldown("enderpearl");

    // CONFIG
    ConfigManager.registerConfigs();

    // SCOREBOARD
    Assemble assemble = new Assemble(this, new ScoreboardManager());

    // LIST LISTENERS
    registerEventListeners();
    Console.sendMessage(" ");

    // COMMANDS
    registerCommands();
    Console.sendMessage(" ");

// Define the plugin version
    String pluginVersion = "1.0.0";

// Generate the current date and time when the plugin loaded
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String loadedTime = LocalDateTime.now().format(dateFormat);

// Define the plugin build number and server version
    int pluginBuild = 01/11/23;

    String serverVersion = Bukkit.getVersion();
// Extracting a simplified version string
    String simplifiedServerVersion = serverVersion.split("MC: ")[1].split("\\)")[0];

    long startTime = System.currentTimeMillis();

// Calculate the time taken for plugin loading
    long endTime = System.currentTimeMillis();
    long timeTaken = endTime - startTime;


    // START MESSAGE
    Console.sendMessage("&b $$$$$$\\    $$\\               $$\\ $$\\                     ");
    Console.sendMessage("&b$$  __$$\\   $$ |              $$ |$$ |                    ");
    Console.sendMessage("&b$$ /  \\__|$$$$$$\\    $$$$$$\\  $$ |$$ | $$$$$$\\   $$$$$$\\  ");
    Console.sendMessage("&b\\$$$$$$\\  \\_$$  _|  $$  __$$\\ $$ |$$ | \\____$$\\ $$  __$$\\ ");
    Console.sendMessage("&b \\____$$\\   $$ |    $$$$$$$$ |$$ |$$ | $$$$$$$ |$$ |  \\__|");
    Console.sendMessage("&b$$\\   $$ |  $$ |$$\\ $$   ____|$$ |$$ |$$  __$$ |$$ |      ");
    Console.sendMessage("&b\\$$$$$$  |  \\$$$$  |\\$$$$$$$\\ $$ |$$ |\\$$$$$$$ |$$ |      ");
    Console.sendMessage("&b \\______/    \\____/  \\_______|\\__|\\__| \\_______|\\__|      ");
    Console.sendMessage("&3Stellar Practice has Loaded successfully\n");
    Console.sendMessage("&3&lInformation");
    Console.sendMessage(" | &bVersion: &f" + Constants.Ver);
    Console.sendMessage(" | &bLicense: &cNo-License"); // Just a placeholder for when it starts up
    Console.sendMessage(" | &bFinished loading in: " + timeTaken + "ms");
    Console.sendMessage(" | &bBuild: " + pluginBuild);
    Console.sendMessage(" | &bServer Version: " + simplifiedServerVersion);
    Console.sendMessage(" | &bPlugin loaded on: " + loadedTime + "\n");
    Console.sendMessage("&3&lLoaded");
    Console.sendMessage(" | &bSuccessfully Loaded Configurations!");
    Console.sendMessage(" | &bSuccessfully Loaded Managers!");
    Console.sendMessage(" | &bSuccessfully Loaded Listeners!");
    Console.sendMessage(" | &bSuccessfully Loaded Commands!\n");
    Console.sendMessage("&3&lCredits");
    Console.sendMessage(" | &bAuthor: &f" + Constants.Author);
    Console.sendMessage(" | &bThanks for using &9Stellar&3!");
    Console.sendMessage(" | &bDiscord: &b" + Constants.Discord);
  }


  public static void loadManagers() {
    ConfigManager.arenaManager = new ArenaManager();
    ConfigManager.kitManager = new KitManager();
    ConfigManager.divisionsManager = new DivisionsManager();
  }

  public static void redloadManagers() {
    loadManagers();
    ConfigManager.registerConfigs();
  }

  private void registerEventListeners() {
    Arrays.asList(
            new PlayerJoin(),
            new SpawnListeners(),
            new WorldListener(),
            new GameListener(),
            new UnrankedInventoryModern(ConfigManager.kitManager),
            new BlockListener(),
            new PlayerDataListener(),
            new MenuListener(),
            new StatsListener(),
            new KitEditorListener()
    ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
  }

  private void registerCommands() {
    createCMD("setspawn", new SetSpawnCMD());
    createCMD("arena", new ArenaCMD());
    createCMD("Stellar", new MainCMD());
    createCMD("kit", new KitsCMD());
    createCMD("queue", new QueueCMD());
    createCMD("ping", new PingCMD());
    createCMD("leavequeue", new LeaveQueueCMD());
    createCMD("stats", new StatsCMD());
    createCMD("unranked", new UnrankedCMD());
    createCMD("ranked", new RankedCMD());
    createCMD("settings", new SettingsCMD());
    createCMD("kiteditor", new KitEditorCMD());
  }

  private void createCMD(String cmd, CommandExecutor commandfile){
    getCommand(cmd).setExecutor(commandfile);
  }
  @Override
  public void onDisable() {
    getServer().getPluginManager().disablePlugin(this);
  }
}