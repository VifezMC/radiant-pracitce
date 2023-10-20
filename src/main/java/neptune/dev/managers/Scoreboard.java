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
    private final List<String> scoreboardInQueue;
    private final List<String> scoreboardGameEnd;

    private int playingPlayers;

    public Scoreboard() {
        lobbyTitle = ConfigManager.scoreboardConfig.getString("scoreboard.title");
        scoreboardLobby = ConfigManager.scoreboardConfig.getStringList("scoreboard.lobby");
        scoreboardMatch = ConfigManager.scoreboardConfig.getStringList("scoreboard.match");
        scoreboardInQueue = ConfigManager.scoreboardConfig.getStringList("scoreboard.in-queue");
        scoreboardGameEnd = ConfigManager.scoreboardConfig.getStringList("scoreboard.game-ended");

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
            return getGameEnd();
        }

        return Collections.emptyList();
    }

    private List<String> getLobbyLines() {
        List<String> lobbyLines = new ArrayList<>();

        for (String line : scoreboardLobby) {
            line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
            line = line.replace("{queueing_players}", String.valueOf(QueueManager.Queue.size()));
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

            // TODO: FIX THIS SHIT CODE

            if (opponent == player) {
                Player temp = user;
                user = opponent;
                opponent = temp;
            }

            int userPing = PlayerUtils.getPing(user);
            int opponentPing = PlayerUtils.getPing(opponent);

            for (String line : scoreboardMatch) {
                line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
                line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
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
            line = line.replace("{kit}", QueueManager.Queue.get(p));
            line = line.replace("{online_players}", String.valueOf(Neptune.instance.getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(playingPlayers));
            line = line.replace("{queueing_players}", String.valueOf(QueueManager.Queue.size()));
            inQueueLines.add(line);
        }
        return inQueueLines;
    }

    private List<String> getGameEnd() {
        return new ArrayList<>(scoreboardGameEnd);
    }

}