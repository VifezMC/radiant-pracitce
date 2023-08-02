package neptune.dev.utils;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class Cooldowns {

    public static HashMap<String, HashMap<UUID, Long>> cooldown;

    public static void createCooldown(final String string) {
        if (Cooldowns.cooldown.containsKey(string)) {
            throw new IllegalArgumentException("Cooldown already exists.");
        }
        Cooldowns.cooldown.put(string, new HashMap<>());
    }

    public static HashMap<UUID, Long> getCooldownMap(final String string) {
        if (Cooldowns.cooldown.containsKey(string)) {
            return Cooldowns.cooldown.get(string);
        }
        return null;
    }

    public static void addCooldown(final String string, final Player player, final int seconds) {
        if (!Cooldowns.cooldown.containsKey(string)) {
            throw new IllegalArgumentException(string + " does not exist");
        }
        final long next = System.currentTimeMillis() + seconds * 1000L;
        Cooldowns.cooldown.get(string).put(player.getUniqueId(), next);
    }

    public static boolean isOnCooldown(final String string, final Player player) {
        return Cooldowns.cooldown.containsKey(string) && Cooldowns.cooldown.get(string).containsKey(player.getUniqueId()) && System.currentTimeMillis() <= Cooldowns.cooldown.get(string).get(player.getUniqueId());
    }

    public static boolean isOnCooldown(final String string, final UUID uuid) {
        return Cooldowns.cooldown.containsKey(string) && Cooldowns.cooldown.get(string).containsKey(uuid) && System.currentTimeMillis() <= Cooldowns.cooldown.get(string).get(uuid);
    }


    public static int getCooldownForPlayerInt(final String string, final Player player) {
        return (int)(Cooldowns.cooldown.get(string).get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }

    public static String getCooldownForPlayerString(final String string, final Player player) {
        double cooldownMillis = Cooldowns.cooldown.get(string).get(player.getUniqueId()) - System.currentTimeMillis();
        double cooldownSeconds = cooldownMillis / 1000.0;
        String formattedCooldown = String.format("%.1f", cooldownSeconds);
        return formattedCooldown;
    }


    public static void removeCooldown(final String string, final Player player) {
        if (!Cooldowns.cooldown.containsKey(string)) {
            throw new IllegalArgumentException(string + " does not exist");
        }
        Cooldowns.cooldown.get(string).remove(player.getUniqueId());
    }

    public static void removeCooldown(final String string, final UUID uuid) {
        if (!Cooldowns.cooldown.containsKey(string)) {
            throw new IllegalArgumentException(string + " does not exist");
        }
        Cooldowns.cooldown.get(string).remove(uuid);
    }

    static {
        Cooldowns.cooldown = new HashMap<>();
    }

}