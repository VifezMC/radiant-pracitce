package xyz.kiradev.types;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

    private String name;
    private ItemStack[] items;
    private ItemStack[] armour;
    private ItemStack icon;
    private List<String> arenas;
    private boolean ranked;
    private List<String> rules;
    private String description;
    private int playing;
    private int queue;

    public Kit(String name, ItemStack[] items, ItemStack[] armour, ItemStack icon, List<String> arenas, boolean ranked, List<String> rules, String description) {
        this.name = name;
        this.items = items;
        this.armour = armour;
        this.icon = icon;
        this.arenas = arenas;
        this.ranked = ranked;
        this.rules = rules;
        this.description = description;
        this.playing = 0;
        this.queue = 0;
    }


    public String getName() {
        return name;
    }
    public ItemStack[] getItems() {
        return items;
    }
    public ItemStack[] getArmour() {
        return armour;
    }
    public ItemStack getIcon() {
        return icon;
    }
    public List<String> getArenas() {
        return arenas;
    }
    public boolean getRanked() {
        return ranked;
    }
    public String getDescription() {
        return description;
    }
    public List<String> getRules(){
        return rules;
    }
    public int getPlaying(){
        return playing;
    }
    public void addPlaying(int playing) {
        this.playing = this.playing + playing;
    }
    public void removePlaying(int playing) {
        this.playing = this.playing - playing;
    }
    public int getQueue() {
        return queue;
    }
    public void addQueue(int queue) {
        this.queue = this.queue + queue;
    }
    public void removeQueue(int queue) {
        this.queue = this.queue - queue;
    }
}
