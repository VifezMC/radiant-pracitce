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
    Console.sendMessage("&3Stellar Loaded successfully\n");
    Console.sendMessage("&b&lInformation");
    Console.sendMessage(" | &3Version: &f" + Constants.Ver);
    Console.sendMessage(" | &3Finished loading in: " + timeTaken + "ms");
    Console.sendMessage(" | &3Build: " + pluginBuild);
    Console.sendMessage(" | &3Server Version: " + simplifiedServerVersion);
    Console.sendMessage(" | &3Plugin loaded on: " + loadedTime + "\n");
    Console.sendMessage("&b&lLoaded");
    Console.sendMessage(" | &3Successfully Loaded Managers!");
    Console.sendMessage(" | &3Successfully Loaded Listeners!");
    Console.sendMessage(" | &3Successfully Loaded Commands!\n");
    Console.sendMessage("&b&lCredits");
    Console.sendMessage(" | &3Author: &f" + Constants.Author);
    Console.sendMessage(" | &3Thanks for using &9Stellar&3!");
    Console.sendMessage(" | &3Discord: &b" + Constants.Discord);
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