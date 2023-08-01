package neptune.dev.storage;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import neptune.dev.Neptune;
import neptune.dev.utils.mongo.Mongo;
import neptune.dev.utils.mongo.MongoCredentials;
import org.bson.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoManager extends Mongo {

    private static final boolean DISABLE_LOGGING = false;

    @Getter
    private final MongoCollection<Document> profiles;

    public MongoManager(Neptune oxygen) {
        super(MongoCredentials.of(oxygen.getConfig().getString("MONGO.URI"), oxygen.getConfig().getString("MONGO.DATABASE")));

        this.profiles = getDatabase().getCollection("profiles");

        if (DISABLE_LOGGING) {
            final Logger mongoLogger = Logger.getLogger("org.mongodb.driver.cluster");
            mongoLogger.setLevel(Level.SEVERE);
        }
    }
}
