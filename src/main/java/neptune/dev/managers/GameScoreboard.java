package neptune.dev.managers;


import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.assemble.AssembleAdapter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScoreboard implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        return Neptune.scoreboardConfig.getString("SCOREBOARD.TITLE");
    }

    @Override
    public List<String> getLines(Player player) {
        final List<String> toReturn = new ArrayList<>();
        if (PlayerUtils.getState(player) == PlayerState.LOBBY) {
            return getLobby(player);
        }
        if (PlayerUtils.getState(player) == PlayerState.PLAYING) {
            toReturn.addAll(Neptune.scoreboardConfig.getStringList("SCOREBOARD.MATCH"));
        }
        return toReturn;
    }

    public List<String> getLobby(Player player) {
        List<String> toReturn = new ArrayList<>();
        if (PlayerUtils.getState(player) == PlayerState.LOBBY) {
            for (String sb : Neptune.scoreboardConfig.getStringList("SCOREBOARD.LOBBY")) {
                String msg = sb;
                msg = msg.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
                msg = msg.replace("{playing_players}", String.valueOf(QueueProcessor.playing));
                toReturn.add(msg);
            }
        }
        return toReturn;
}

    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

}
