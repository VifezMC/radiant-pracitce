package neptune.dev;

import lombok.Getter;
import neptune.dev.commands.admin.ArenaCMD;
import neptune.dev.commands.admin.KitsCMD;
import neptune.dev.commands.admin.MainCMD;
import neptune.dev.commands.admin.SetSpawnCMD;
import neptune.dev.commands.user.*;
import neptune.dev.listeners.*;
import neptune.dev.managers.*;
import neptune.dev.ui.ranked.RankedModernUI;
import neptune.dev.ui.unranked.UnrankedInventoryModern;
import neptune.dev.utils.Cooldowns;
import neptune.dev.utils.assemble.Assemble;
import neptune.dev.utils.render.Console;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class Neptune extends JavaPlugin {

  public static Neptune instance;

  @Override
  public void onEnable() {
    instance = this;
    Plugin luckPermsPlugin = getServer().getPluginManager().getPlugin("PlaceholderAPI");
    if (luckPermsPlugin != null && luckPermsPlugin.isEnabled()) {
      new Placeholder(this).register();
    }
    loadManagers();
    Console.sendMessage("&7[&9Neptune&7] &aLoaded managers!");

    // PEARL COOLDOWN
    Cooldowns.createCooldown("enderpearl");

    // CONFIG
    ConfigManager.registerConfigs();

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
            new RankedModernUI(),
            new BlockListener(),
            new PlayerDataListener(),
            new MenuListener(),
            new StatsListener()
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

  private void createCMD(String cmd, CommandExecutor commandfile){
    getCommand(cmd).setExecutor(commandfile);
  }
  @Override
  public void onDisable() {
    getServer().getPluginManager().disablePlugin(this);
  }
}
