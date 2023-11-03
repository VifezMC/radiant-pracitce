package xyz.kiradev.tab;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.kiradev.listeners.PlayerDataListener;
import xyz.kiradev.managers.ConfigManager;
import xyz.kiradev.managers.QueueManager;
import xyz.kiradev.tab.entry.TabElement;
import xyz.kiradev.tab.skin.SkinType;
import xyz.kiradev.types.Data;
import xyz.kiradev.ui.StatsInventory;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.render.CC;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

final class LobbyLayoutProvider implements BiConsumer<Player, TabElement> {

    @Override
    public void accept(Player p, TabElement element) {
        List<String> list = Arrays.asList("LEFT", "MIDDLE", "RIGHT", "FAR-RIGHT");
        Data stats = PlayerDataListener.getStats(p);

        for (int i = 0; i < 4; ++i) {
            String s = list.get(i);
            for (int l = 0; l < 20; ++l) {
                String str = ConfigManager.tabConfig.getString("tablist." + s + "." + (l + 1))
                        .replace("{player}", p.getDisplayName())
                        .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("{ping}", String.valueOf(PlayerUtils.getPing(p)))
                        .replace("{playing}", String.valueOf(QueueManager.playing))
                        .replace("{elo}", String.valueOf(stats.getELO()))
                        .replace("{division}", ConfigManager.divisionsManager.getPlayerDivision(stats.getELO()))
                        .replace("{wins}", String.valueOf(stats.getWins()))
                        .replace("{losses}", String.valueOf(stats.getLosses()))
                        .replace("{kdr}", String.valueOf(StatsInventory.toKDR(stats.getWins(), stats.getLosses())))
                        .replace("{queueing}", String.valueOf(QueueManager.Queue.size()));


                SkinType skinType = SkinType.DARK_GRAY;
                if(str.toLowerCase(Locale.ROOT).contains("<skin=")) {
                    String skin = after(before(str, ">"), "<skin=");
                    String input = "<skin=" + skin + ">";
                    if(skin.equalsIgnoreCase("$self")) {
                        skinType = SkinType.fromUsername(p.getName());
                    } else {
                        skinType = SkinType.fromUsername(skin);
                    }
                    str = str.replace(input, "");
                }

                element.add(i, l, CC.translate(str), 0, skinType.getSkinData());
            }
        }
    }
    public static String after(String value, String a) {
        // Returns a substring containing all characters after a string.
        int posA = value.toLowerCase(Locale.ROOT).lastIndexOf(a.toLowerCase(Locale.ROOT));
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }
    public static String before(String value, String a) {
        // Return substring containing all characters before a string.
        int posA = value.toLowerCase(Locale.ROOT).indexOf(a.toLowerCase(Locale.ROOT));
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }
}