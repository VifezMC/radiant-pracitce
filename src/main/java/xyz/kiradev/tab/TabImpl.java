package xyz.kiradev.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.tab.entry.TabElement;
import xyz.kiradev.tab.entry.TabElementHandler;
import xyz.kiradev.utils.render.CC;

import java.util.function.BiConsumer;

public final class TabImpl implements TabElementHandler {

    public final BiConsumer<Player, TabElement> lobbyLayoutProvider = new LobbyLayoutProvider();

    @Override
    public TabElement getElement(Player player) {
        TabElement tabElement = new TabElement();
        tabElement.setHeader(CC.translate(ConfigManager.tabConfig.getString("tablist.header")
                .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))));
        tabElement.setFooter(CC.translate(ConfigManager.tabConfig.getString("tablist.footer")
                .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))));
        lobbyLayoutProvider.accept(player, tabElement);

        return tabElement;
    }
}