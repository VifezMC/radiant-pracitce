package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.utils.PlayerUtils;
import neptune.dev.utils.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Scoreboard implements AssembleAdapter {

    private final String lobbyTitle;
    private final List<String> scoreboardLobby;
    private final List<String> scoreboardMatch;

    private int playingPlayers;

    public Scoreboard() {
        lobbyTitle = Neptune.scoreboardConfig.getString("scoreboard.title");
        scoreboardLobby = Neptune.scoreboardConfig.getStringList("scoreboard.lobby");
        scoreboardMatch = Neptune.scoreboardConfig.getStringList("scoreboard.match");
        updateServerStats();
    }

    /**
     * Updates the number of playing players from the QueueProcessor.
     */
    private void updateServerStats() {
        playingPlayers = QueueProcessor.playing;
    }

    @Override
    public String getTitle(Player player) {
        return lobbyTitle;
    }

    @Override
    public List<String> getLines(Player player) {
        PlayerState playerState = PlayerUtils.getState(player);

        if (playerState == PlayerState.LOBBY) {
            return getLobbyLines();
        } else if (playerState == PlayerState.PLAYING) {
            return getMatchLines(player);
        }

        return Collections.emptyList();
    }

    private List<String> getLobbyLines() {
        List<String> lobbyLines = new ArrayList<>();
        int onlinePlayers = Neptune.instance.getServer().getOnlinePlayers().size();
        for (String line : scoreboardLobby) {
            line = line.replace("{online_players}", String.valueOf(onlinePlayers));
            line = line.replace("{playing_players}", String.valueOf(playingPlayers));
            lobbyLines.add(line);
        }
        return lobbyLines;
    }

    private List<String> getMatchLines(Player player) {
        List<String> matchLines = new ArrayList<>();
        UUID matchID = MatchManager.getMatchID(player);
        List<Player> matchPlayers = MatchManager.getMatchPlayers(matchID);

        if (matchPlayers != null && matchPlayers.size() >= 2) {
            Player user = matchPlayers.get(0);
            Player opponent = matchPlayers.get(1);

            if (opponent == player) {
                Player temp = user;
                user = opponent;
                opponent = temp;
            }

            int userPing = getPing(user);
            int opponentPing = getPing(opponent);

            for (String line : scoreboardMatch) {
                line = line.replace("{online_players}", String.valueOf(playingPlayers));
                line = line.replace("{playing_players}", String.valueOf(playingPlayers));
                line = line.replace("{user_ping}", String.valueOf(userPing));
                line = line.replace("{opponent_ping}", String.valueOf(opponentPing));
                line = line.replace("{opponent}", opponent.getName());
                matchLines.add(line);
            }
        }

        return matchLines;
    }

    private int getPing(Player player) {
        return player.spigot().getPing();
    }
}
