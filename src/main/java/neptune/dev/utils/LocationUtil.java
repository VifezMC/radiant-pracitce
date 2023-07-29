package neptune.dev.utils;

import neptune.dev.Neptune;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static Location toLoc(String location) {
        String[] data =  location.split(":");
        World w = Neptune.instance.getServer().getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        Location loc = new Location(w,x,y,z);
        loc.setYaw(Float.parseFloat(data[4]));
        loc.setPitch(Float.parseFloat(data[5]));
        return loc;
    }

}
