package xyz.kiradev.managers;

import org.bukkit.entity.Player;
import xyz.kiradev.Radiant;
import xyz.kiradev.commands.user.KitEditorCMD;
import xyz.kiradev.listeners.PlayerDataListener;
import xyz.kiradev.states.PlayerState;
import xyz.kiradev.types.Data;
import xyz.kiradev.utils.PlayerUtils;
import xyz.kiradev.utils.assemble.AssembleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ScoreboardManager implements AssembleAdapter {

    private final String lobbyTitle;
    private final List<String> scoreboardLobby;
    private final List<String> scoreboardMatch;
    private final List<String> scoreboardInQueue;
    private final List<String> scoreboardGameEnd;
    private final List<String> scoreboardKiteditor;
    private final List<String> animatedTexts;
    private final int updateInterval;

    public ScoreboardManager() {
        int updateInterval1;
        lobbyTitle = ConfigManager.scoreboardConfig.getString("scoreboard.title");
        scoreboardLobby = ConfigManager.scoreboardConfig.getStringList("scoreboard.lobby");
        scoreboardMatch = ConfigManager.scoreboardConfig.getStringList("scoreboard.match");
        scoreboardInQueue = ConfigManager.scoreboardConfig.getStringList("scoreboard.in-queue");
        scoreboardGameEnd = ConfigManager.scoreboardConfig.getStringList("scoreboard.game-ended");
        scoreboardKiteditor = ConfigManager.scoreboardConfig.getStringList("scoreboard.kit-editor");
        animatedTexts = ConfigManager.scoreboardConfig.getStringList("animation.animated_texts");
        updateInterval1 = ConfigManager.scoreboardConfig.getInt("animation.update_interval");

        if (updateInterval1 <= 0) {
            updateInterval1 = 3;
        }
        updateInterval = updateInterval1;
    }

    @Override
    public String getTitle(Player player) {
        String title = lobbyTitle;

        // Add animated text to the title if it's defined in the config
        if (title.contains("{animated_text}")) {
            title = title.replace("{animated_text}", getAnimatedText());
        }

        return title;
    }

    @Override
    public List<String> getLines(Player p) {
        PlayerState playerState = PlayerUtils.getState(p);

        switch (playerState) {
            case ENDED:
                return getGameEnd(p);
            case LOBBY:
                return getLobbyLines(p);
            case INQUEUE:
                return getInQueueLines(p);
            case PLAYING:
                return getMatchLines(p);
            case KITEDITOR:
                return getScoreboardKiteditor(p);
        }
        return Collections.emptyList();
    }

    private List<String> getLobbyLines(Player p) {
        List<String> lobbyLines = new ArrayList<>();
        Data stats = PlayerDataListener.getStats(p);

        for (String line : scoreboardLobby) {
            line = line.replace("{online_players}", String.valueOf(Radiant.getInstance().getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
            line = line.replace("{queueing_players}", String.valueOf(QueueManager.Queue.size()));
            line = line.replace("{elo}", String.valueOf(stats.getELO()));
            line = line.replace("{division}", String.valueOf(ConfigManager.divisionsManager.getPlayerDivision(stats.getELO())));

            if (line.contains("{animated_text}")) {
                line = line.replace("{animated_text}", getAnimatedText());
            }

            lobbyLines.add(line);
        }
        return lobbyLines;
    }

    private String getAnimatedText() {
        int index = (int) ((System.currentTimeMillis() / updateInterval) % animatedTexts.size());
        return animatedTexts.get(index);
    }

    private List<String> getMatchLines(Player player) {
        List<String> matchLines = new ArrayList<>();
        UUID matchID = MatchManager.getMatchID(player);
        List<Player> matchPlayers = MatchManager.getMatchPlayers(matchID);

        if (matchPlayers != null && matchPlayers.size() >= 2) {
            Player user = matchPlayers.get(0);
            Player opponent = matchPlayers.get(1);

            int userPing = PlayerUtils.getPing(user);
            int opponentPing = PlayerUtils.getPing(opponent);

            for (String line : scoreboardMatch) {
                line = line.replace("{online_players}", String.valueOf(Radiant.getInstance().getServer().getOnlinePlayers().size()));
                line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
                line = line.replace("{user_ping}", String.valueOf(userPing));
                line = line.replace("{opponent_ping}", String.valueOf(opponentPing));
                line = line.replace("{opponent}", opponent.getName());

                if (line.contains("{animated_text}")) {
                    line = line.replace("{animated_text}", getAnimatedText());
                }
                matchLines.add(line);
            }
        }

        return matchLines;
    }

    private List<String> getInQueueLines(Player p) {
        List<String> inQueueLines = new ArrayList<>();
        for (String line : scoreboardInQueue) {
            line = line.replace("{kit}", QueueManager.Queue.get(p));
            line = line.replace("{online_players}", String.valueOf(Radiant.getInstance().getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
            line = line.replace("{queueing_players}", String.valueOf(QueueManager.Queue.size()));

            if (line.contains("{animated_text}")) {
                line = line.replace("{animated_text}", getAnimatedText());
            }
            inQueueLines.add(line);
        }
        return inQueueLines;
    }

    private List<String> getScoreboardKiteditor(Player p) {
        List<String> inQueueLines = new ArrayList<>();
        for (String line : scoreboardKiteditor) {
            line = line.replace("{kit}", KitEditorCMD.kiteditor.get(p).getName());
            line = line.replace("{online_players}", String.valueOf(Radiant.getInstance().getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
            line = line.replace("{queueing_players}", String.valueOf(QueueManager.Queue.size()));

            if (line.contains("{animated_text}")) {
                line = line.replace("{animated_text}", getAnimatedText());
            }
            inQueueLines.add(line);
        }
        return inQueueLines;
    }

    private List<String> getGameEnd(Player p) {
        List<String> inGameEnd = new ArrayList<>();
        for (String line : scoreboardGameEnd) {
            line = line.replace("{kit}", KitEditorCMD.kiteditor.get(p).getName());
            line = line.replace("{online_players}", String.valueOf(Radiant.getInstance().getServer().getOnlinePlayers().size()));
            line = line.replace("{playing_players}", String.valueOf(QueueManager.playing));
            line = line.replace("{queueing_players}", String.valueOf(QueueManager.Queue.size()));

            if (line.contains("{animated_text}")) {
                line = line.replace("{animated_text}", getAnimatedText());
            }
            inGameEnd.add(line);
        }
        return inGameEnd;
    }
}