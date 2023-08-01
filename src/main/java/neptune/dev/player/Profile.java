package neptune.dev.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Profile {

    private final UUID uniqueId;
    private String name;

    private boolean loaded;

    private PlayerState profileState = PlayerState.IN_SPAWN;

    private UUID currentMatchId;

    //will be used in the future ig
    private int missedPots;
    private int longestCombo;
    private int combo;
    private int hits;
}
