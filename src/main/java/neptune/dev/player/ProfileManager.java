package neptune.dev.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import neptune.dev.Neptune;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;

public class ProfileManager {

    @Getter
    private final Map<UUID, Profile> profiles = new HashMap<>();
    private final Neptune plugin = Neptune.getInstance();

    public Profile findOrCreate(UUID uuid) {
        return profiles.computeIfAbsent(uuid, Profile::new);

    }

    public Profile getByUuid(UUID uuid) {
        return profiles.getOrDefault(uuid, new Profile(uuid));
    }

    public Collection<Profile> getAllProfiles() {
        return this.profiles.values();
    }

    public void load(Profile profile) {
        Document document = this.plugin.getMongoManager().getProfiles().find(Filters.eq("uniqueId", profile.getUniqueId().toString())).first();

        if (document != null) {

            profile.setName(document.getString("name"));
        }
        profile.setLoaded(true);
    }

    public void save(Profile profile) {
        Document document = new Document();

        document.put("uniqueId", profile.getUniqueId().toString());
        document.put("name", Bukkit.getPlayer(profile.getUniqueId()).getName());

        this.plugin.getMongoManager().getProfiles().replaceOne(Filters.eq("uniqueId", profile.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public void delete(UUID uuid) {
        this.save(this.getByUuid(uuid));
        this.getProfiles().remove(uuid);
    }
}
