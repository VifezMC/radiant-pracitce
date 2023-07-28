package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.assemble.AssembleAdapter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameScoreboard implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        return Neptune.scoreboardConfig.getString("scoreboard.title");
    }

    @Override
    public List<String> getLines(Player player) {
        PlayerState playerState = PlayerUtils.getState(player);

        if (playerState == PlayerState.LOBBY) {
            return getLobbyLines();
        } else if (playerState == PlayerState.PLAYING) {
            return getMatchLines(player);
        }

        return new ArrayList<>();
    }

    private List<String> getLobbyLines() {
        List<String> lobbyLines = new ArrayList<>();
        List<String> scoreboardLobby = Neptune.scoreboardConfig.getStringList("scoreboard.lobby");

        int onlinePlayers = Neptune.instance.getServer().getOnlinePlayers().size();
        int playingPlayers = QueueProcessor.playing;

        for (String line : scoreboardLobby) {
            line = line.replace("{online_players}", String.valueOf(onlinePlayers));
            line = line.replace("{playing_players}", String.valueOf(playingPlayers));
            lobbyLines.add(line);
        }
        return lobbyLines;
    }

    private List<String> getMatchLines(Player player) {
        List<String> matchLines = new ArrayList<>();
        List<String> scoreboardMatch = Neptune.scoreboardConfig.getStringList("scoreboard.match");
        UUID matchID = MatchManager.getMatchID(player);
        List<Player> matchPlayers = MatchManager.getMatchPlayers(matchID);

        Player user = matchPlayers.get(0);
        Player opponent = matchPlayers.get(1);

        if (opponent == player) {
            // Swap players to ensure the user is always the current player
            Player temp = user;
            user = opponent;
            opponent = temp;
        }

        int onlinePlayers = Neptune.instance.getServer().getOnlinePlayers().size();
        int playingPlayers = QueueProcessor.playing;
        int userPing = getPing(user);
        int opponentPing = getPing(opponent);

        for (String line : scoreboardMatch) {
            line = line.replace("{online_players}", String.valueOf(onlinePlayers));
            line = line.replace("{playing_players}", String.valueOf(playingPlayers));
            line = line.replace("{user_ping}", String.valueOf(userPing));
            line = line.replace("{opponent_ping}", String.valueOf(opponentPing));
            line = line.replace("{opponent}", opponent.getName());
            matchLines.add(line);
        }
        return matchLines;
    }

    private int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }
}
