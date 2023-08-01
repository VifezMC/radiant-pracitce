package neptune.dev.utils.mongo;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Data(staticConstructor = "of")
public final class MongoCredentials {

    /**
     * The connection URI for the storage.
     */
    @NotNull
    private final String uri;

    /**
     * The name of the storage.
     */
    @NotNull private final String database;

    /**
     * Gets storage credentials from a configuration section.
     *
     * @param section The configuration section
     * @return The storage credentials provided
     */
    @NotNull
    public static MongoCredentials of(@NotNull final ConfigurationSection section) {
        return new MongoCredentials(
                section.getString("uri", "mongodb://localhost:27017"),
                section.getString("storage", "minecraft"));
    }
}