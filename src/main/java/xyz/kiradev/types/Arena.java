package xyz.kiradev.types;

import org.bukkit.Location;

public class Arena {

    private final String name;
    private final Location spawn1;
    private final Location spawn2;
    private final Location min;
    private final Location max;

    private boolean available;

    public Arena(String name, Location spawn1, Location spawn2, Location min, Location max) {
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.available = true;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
