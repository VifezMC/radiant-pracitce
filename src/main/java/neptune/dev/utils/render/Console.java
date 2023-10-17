package neptune.dev.utils.render;

import static org.bukkit.Bukkit.getServer;

public class Console {
    public static void sendMessage(String m){
        getServer().getConsoleSender().sendMessage(CC.translate(m));
    }
    public static void sendError(String m){
        getServer().getConsoleSender().sendMessage(CC.translate("&c" + m));
    }

}
