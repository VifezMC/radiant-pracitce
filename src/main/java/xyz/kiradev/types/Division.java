package xyz.kiradev.types;

public class Division {

    private final String name;
    private final String displayname;
    private final int elo;

    public Division(String name, String displayname, int elo) {
        this.name = name;
        this.displayname = displayname;
        this.elo = elo;
    }

    public String getName() {
        return name;
    }

    public String getDisplayname() {
        return displayname;
    }

    public int getElo() {
        return elo;
    }
}
