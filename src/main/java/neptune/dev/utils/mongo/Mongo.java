package neptune.dev.utils.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class Mongo implements IMongo {

    @Getter
    @NotNull private final MongoClient client;
    @Getter @NotNull
    private final MongoDatabase database;

    public Mongo(@NotNull final MongoCredentials credentials) {
        this.client = MongoClients.create(credentials.getUri());
        this.database = this.client.getDatabase(credentials.getDatabase());
    }

    @Override
    public MongoDatabase getDatabase(final String name) {
        return this.client.getDatabase(name);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.close();
        }
    }
}