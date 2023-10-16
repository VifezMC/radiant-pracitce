package neptune.dev.managers;

import neptune.dev.types.Arena;
import neptune.dev.types.Division;
import neptune.dev.types.Kit;
import neptune.dev.utils.LocationUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class DivisionsManager {

    private static List<Division> divisions;

    public DivisionsManager() {
        divisions = new ArrayList<>();
    }

    public void loadDivisions() {
        if (ConfigManager.divisionConfig.get("divisions") == null) {
            return;
        }

        for (String divisionsName : ConfigManager.divisionConfig.getConfigurationSection("divisions").getKeys(false)) {
            String displayname = ConfigManager.divisionConfig.getString("divisions." + divisionsName + ".display-name");
            int elo = ConfigManager.divisionConfig.getInt("divisions." + divisionsName + ".elo");

            Division division = new Division(divisionsName, displayname, elo);
            divisions.add(division);
        }
    }

    public static Division getDivision(String Div) {
        for (Division divs : divisions) {
            if (divs.getName().equals(Div)) {
                return divs;
            }
        }
        return null;
    }

    public String getPlayerDivision(int playerElo) {
        Division playerDivision = null;

        for (Division division : divisions) {
            if (playerElo >= division.getElo()) {
                playerDivision = division;
            }
        }

        return (playerDivision != null) ? playerDivision.getDisplayname() : "No Division";
    }


    public static List<Division> getDivisions() {
        return divisions;
    }
}
