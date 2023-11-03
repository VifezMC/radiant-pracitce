package xyz.kiradev.utils;

import org.bukkit.Location;
import org.bukkit.World;
import xyz.kiradev.Stellar;

public class LocationUtil {

    public static Location toLoc(String location) {
        String[] data = location.split(":");
        World w = Stellar.getInstance().getServer().getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]) + 1;
        double z = Double.parseDouble(data[3]);
        Location loc = new Location(w, x, y, z);
        loc.setYaw(Float.parseFloat(data[4]));
        loc.setPitch(Float.parseFloat(data[5]));
        return loc;
    }

}
