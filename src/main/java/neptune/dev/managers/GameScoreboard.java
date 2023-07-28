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
        List<String> toReturn = new ArrayList<>();
        PlayerState state = PlayerUtils.getState(player);
        if (state == PlayerState.LOBBY) {
            toReturn = getLobbyLines();
        } else if (state == PlayerState.PLAYING) {
            toReturn.addAll(Neptune.scoreboardConfig.getStringList("SCOREBOARD.MATCH"));
        }
        return toReturn;
    }

    private List<String> getLobbyLines() {
        List<String> lobbyLines = new ArrayList<>();
        List<String> lobbyConfig = Neptune.scoreboardConfig.getStringList("SCOREBOARD.LOBBY");
        for (String line : lobbyConfig) {
            line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueProcessor.playing));
            lobbyLines.add(line);
        }
        return lobbyLines;
    }

    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

}
