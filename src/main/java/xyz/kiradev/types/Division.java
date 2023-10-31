package xyz.kiradev.types;

import org.bukkit.Location;

public class Division {

    private String name;
    private String displayname;
    private int elo;

    public Division(String name, String displayname, int elo) {
        this.name = name;
        this.displayname = displayname;
        this.elo = elo;
    }

    public String getName() {
        return name;
    }
    public String getDisplayname(){
        return displayname;
    }
    public int getElo(){
        return elo;
    }
}
