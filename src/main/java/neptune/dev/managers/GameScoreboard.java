package neptune.dev.managers;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScoreboard implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        return Neptune.scoreboardConfig.getString("scoreboard.title");
    }

    @Override
    public List<String> getLines(Player player) {
        final List<String> toReturn = new ArrayList<>();
        if (PlayerUtils.getState(player) == PlayerState.LOBBY) {
            return getLobby(player);
        }
        if (PlayerUtils.getState(player) == PlayerState.PLAYING) {
            for (String sb : Neptune.scoreboardConfig.getStringList("scoreboard.match")) {
                String msg = sb;
                String player1 = MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).get(0).getName();
                String player2 = MatchManager.getMatchPlayers(MatchManager.getMatchID(player)).get(1).getName();
                Player user;
                Player opponent;
                if(Bukkit.getPlayer(player1) == player){
                    user = Bukkit.getPlayer(player1);
                    opponent = Bukkit.getPlayer(player2);
                }
                else{
                    user = Bukkit.getPlayer(player2);
                    opponent = Bukkit.getPlayer(player1);
                }
                msg = msg.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
                msg = msg.replace("{playing_players}", String.valueOf(QueueProcessor.playing));
                msg = msg.replace("{user_ping}", String.valueOf(getPing(user)));
                msg = msg.replace("{opponent_ping}", String.valueOf(getPing(opponent)));
                msg = msg.replace("{opponent}", String.valueOf(opponent.getName()));

                toReturn.add(msg);
            }
        }
        return toReturn;
    }

    public List<String> getLobby(Player player) {
        List<String> toReturn = new ArrayList<>();
        if (PlayerUtils.getState(player) == PlayerState.LOBBY) {
            for (String sb : Neptune.scoreboardConfig.getStringList("scoreboard.lobby")) {
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
