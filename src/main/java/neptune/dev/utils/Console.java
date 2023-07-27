package neptune.dev.utils;

import static org.bukkit.Bukkit.getServer;

public class Console {
    public static void sendMessage(String m){
        getServer().getConsoleSender().sendMessage(CC.translate(m));
    }
}
