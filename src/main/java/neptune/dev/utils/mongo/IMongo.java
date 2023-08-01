package neptune.dev.utils.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import neptune.dev.utils.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

public interface IMongo extends Terminable {

    /**
     * Gets the client instance backing the datasource
     *
     * @return the client instance
     */
    @NotNull
    MongoClient getClient();

    /**
     * Gets the main storage in use by the instance.
     *
     * @return the main storage
     */
    @NotNull
    MongoDatabase getDatabase();

    /**
     * Gets a specific storage instance
     *
     * @param name the name of the storage
     * @return the storage
     */
    MongoDatabase getDatabase(String name);
}