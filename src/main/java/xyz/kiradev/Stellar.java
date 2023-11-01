package xyz.kiradev;

import lombok.Getter;
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
    Console.sendMessage("&7[&9Stellar&7] &aSuccessfully Loaded managers!");

    // PEARL COOLDOWN
    Cooldowns.createCooldown("enderpearl");

    // CONFIG
    ConfigManager.registerConfigs();

    // SCOREBOARD
    Assemble assemble = new Assemble(this, new ScoreboardManager());

    // LIST LISTENERS
    registerEventListeners();
    Console.sendMessage("&7[&9Stellar&7] &aSuccessfully Loaded listeners!");

    // COMMANDS
    registerCommands();
    Console.sendMessage("&7[&9Stellar&7] &aSuccessfully Loaded commands!");

    // START MESSAGE
    Console.sendMessage("&9Stellar Loaded successfully" + Constants.Ver);
    Console.sendMessage("&9Author: &f" + Constants.Author);
    Console.sendMessage("&9Version: &f" + Constants.Ver);
    Console.sendMessage("&9Discord: &f" + Constants.Discord);
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