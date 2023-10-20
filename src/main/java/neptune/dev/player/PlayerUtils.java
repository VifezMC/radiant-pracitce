package neptune.dev.player;

import neptune.dev.Neptune;
import neptune.dev.managers.ConfigManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerUtils {

    public static Map<Player, PlayerState> playerStates = new ConcurrentHashMap<>();
    public static Map<Player, GameState> gameStates = new ConcurrentHashMap<>();
    private static World lobbyWorld;
    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;
    private static Field SPAWN_PACKET_ID_FIELD;


    static {
        try {
            SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            SPAWN_PACKET_ID_FIELD.setAccessible(true);

            STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
            STATUS_PACKET_ID_FIELD.setAccessible(true);

            STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
            STATUS_PACKET_STATUS_FIELD.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static World getLobbyWorld() {
        if (lobbyWorld == null) {
            lobbyWorld = Neptune.instance.getServer().getWorld(ConfigManager.arenaConfig.getString("lobby").split(":")[0]);
        }
        return lobbyWorld;
    }

    public static Location getLobbyLocation() {
        String[] data = ConfigManager.arenaConfig.getString("lobby").split(":");
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        Location loc = new Location(getLobbyWorld(), x, y, z);
        loc.setYaw(Float.parseFloat(data[4]));
        loc.setPitch(Float.parseFloat(data[5]));
        return loc;
    }

    public static void setState(Player player, PlayerState state) {
        playerStates.remove(player);
        playerStates.put(player, state);
    }
    public static void setGState(Player player, GameState state) {
        gameStates.remove(player);
        gameStates.put(player, state);
    }

    public static PlayerState getState(Player player) {
        return playerStates.getOrDefault(player, PlayerState.LOBBY);
    }
    public static GameState getGState(Player player) {
        return gameStates.getOrDefault(player, GameState.DEFAULT);
    }
    public static boolean hasPlayerState(Player player, PlayerState state) {
        PlayerState currentPlayerState = getState(player);
        return currentPlayerState == state;
    }
    public static boolean hasGPlayerState(Player player, GameState state) {
        GameState currentPlayerState = getGState(player);
        return currentPlayerState == state;
    }


    public static String toString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
    }

    public static int getPing(Player player) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle", new Class[0]).invoke(player);
            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception e) {
            return -1;
        }
    }

    public static void animateDeath(Player player) {

        final int entityId = -1;
        final PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        final PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);

            final int radius = MinecraftServer.getServer().getPlayerList().d();
            final Set<Player> sentTo = new HashSet<>();

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {

                if (!(entity instanceof Player)) {
                    continue;
                }

                final Player watcher = (Player) entity;

                if (watcher.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }

                ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);

                sentTo.add(watcher);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
