package xyz.kiradev;

import dev.demeng.sentinel.wrapper.model.License;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.kiradev.commands.admin.*;
import xyz.kiradev.commands.user.*;
import xyz.kiradev.listeners.*;
import xyz.kiradev.managers.*;
import xyz.kiradev.party.PartyManager;
import xyz.kiradev.tab.TabHandler;
import xyz.kiradev.tab.TabImpl;
import xyz.kiradev.tab.v1_8_r3.v1_8_R3TabAdapter;
import xyz.kiradev.utils.Cooldowns;
import xyz.kiradev.utils.assemble.Assemble;
import xyz.kiradev.utils.render.Console;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Getter
public class Radiant extends JavaPlugin {

    @Getter
    private static Radiant instance;

    @Getter
    private static License license;

    public static void loadManagers() {
        ConfigManager.arenaManager = new ArenaManager();
        ConfigManager.kitManager = new KitManager();
        ConfigManager.divisionsManager = new DivisionsManager();
        ConfigManager.partyManager = new PartyManager(instance);
        ConfigManager.arenaManager.loadArenas();
        ConfigManager.kitManager.loadKits();
        ConfigManager.divisionsManager.loadDivisions();
        new Assemble(instance, new ScoreboardManager());
        if (ConfigManager.tabConfig.getBoolean("tablist.enable")) {
            new TabHandler(new v1_8_R3TabAdapter(), new TabImpl(), 20L);
        }
    }

    public static void reloadManagers() {
        loadManagers();
        ConfigManager.registerConfigs();
    }

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.registerConfigs();
        loadManagers();
        ConfigManager.registerConfigs();

        Plugin luckPermsPlugin = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (luckPermsPlugin != null && luckPermsPlugin.isEnabled()) {
            new Placeholder(this).register();
        }
        Cooldowns.createCooldown("enderpearl");
        registerEventListeners();
        registerCommands();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        Console.sendMessage("&f---------------------------------------------");
        Console.sendMessage("&c&lRadiant Practice &7[&c" + getDescription().getVersion() + "&7]");
        Console.sendMessage(" ");
        Console.sendMessage("&cInformation");
        Console.sendMessage(" &cVersion &f" + getDescription().getVersion());
        Console.sendMessage(" ");
        Console.sendMessage("&cCredits: &fKira Development");
        Console.sendMessage("&cDiscord: &fdiscord.gg/kiradev");
        Console.sendMessage("&f---------------------------------------------");
    }

    private void registerEventListeners() {
        Arrays.asList(
                new PlayerJoin(),
                new SpawnListeners(),
                new WorldListener(),
                new GameListener(),
                new BlockListener(),
                new MenuListener(),
                new StatsListener(),
                new KitEditorListener(),
                new PlayerDataListener(),
                new StatsListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerCommands() {
        createCMD("setspawn", new SetSpawnCMD());
        createCMD("arena", new ArenaCMD());
        createCMD("radiant", new MainCMD());
        createCMD("kit", new KitsCMD());
        createCMD("queue", new QueueCMD());
        createCMD("ping", new PingCMD());
        createCMD("leavequeue", new LeaveQueueCMD());
        createCMD("stats", new StatsCMD());
        createCMD("unranked", new UnrankedCMD());
        createCMD("ranked", new RankedCMD());
        createCMD("settings", new SettingsCMD());
        createCMD("kiteditor", new KitEditorCMD());
        createCMD("party", new PartyCMD());
    }

    private void createCMD(String cmd, CommandExecutor commandline) {
        getCommand(cmd).setExecutor(commandline);
    }

    @Override
    public void onDisable() {
        getServer().getPluginManager().disablePlugin(this);
    }
}
