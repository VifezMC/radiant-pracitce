package neptune.dev.managers;

import neptune.dev.Neptune;
import neptune.dev.player.PlayerState;
import neptune.dev.player.PlayerUtils;
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
    private final List<String> scoreboardInQueue;
    private final List<String> scoreboardGameEnd;

    private int playingPlayers;

    public Scoreboard() {
        lobbyTitle = Neptune.scoreboardConfig.getString("scoreboard.title");
        scoreboardLobby = Neptune.scoreboardConfig.getStringList("scoreboard.lobby");
        scoreboardMatch = Neptune.scoreboardConfig.getStringList("scoreboard.match");
        scoreboardInQueue = Neptune.scoreboardConfig.getStringList("scoreboard.in-queue");
        scoreboardGameEnd = Neptune.scoreboardConfig.getStringList("scoreboard.game-ended");

    }


    @Override
    public String getTitle(Player player) {
        return lobbyTitle;
    }

    @Override
    public List<String> getLines(Player p) {
        PlayerState playerState = PlayerUtils.getState(p);

        if (playerState == PlayerState.LOBBY) {
            return getLobbyLines();
        } else if (playerState == PlayerState.PLAYING) {
            return getMatchLines(p);
        }else if (playerState == PlayerState.INQUEUE) {
            return getInQueueLines(p);
        }else if (playerState == PlayerState.ENDED) {
            return getGameEnd(p);
        }

        return Collections.emptyList();
    }

    private List<String> getLobbyLines() {
        List<String> lobbyLines = new ArrayList<>();

        for (String line : scoreboardLobby) {
            line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueProcessor.playing));
            line = line.replace("{queueing_players}", String.valueOf(QueueProcessor.Queue.size()));
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

            int userPing = PlayerUtils.getPing(user);
            int opponentPing = PlayerUtils.getPing(opponent);

            for (String line : scoreboardMatch) {
                line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
                line = line.replace("{playing_players}", String.valueOf(QueueProcessor.playing));
                line = line.replace("{user_ping}", String.valueOf(userPing));
                line = line.replace("{opponent_ping}", String.valueOf(opponentPing));
                line = line.replace("{opponent}", opponent.getName());
                matchLines.add(line);
            }
        }

        return matchLines;
    }

    private List<String> getInQueueLines(Player p) {
        List<String> inQueueLines = new ArrayList<>();
            for (String line : scoreboardInQueue) {
                line = line.replace("{kit}", QueueProcessor.Queue.get(p));
                line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
                line = line.replace("{playing_players}", String.valueOf(playingPlayers));
                line = line.replace("{queueing_players}", String.valueOf(QueueProcessor.Queue.size()));
                inQueueLines.add(line);
            }
        return inQueueLines;
    }

    private List<String> getGameEnd(Player p) {
        List<String> gameEnded = new ArrayList<>();
        for (String line : scoreboardGameEnd) {
            gameEnded.add(line);
        }
        return gameEnded;
    }

}
