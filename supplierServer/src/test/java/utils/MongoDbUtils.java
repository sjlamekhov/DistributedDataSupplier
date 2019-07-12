package utils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import configuration.DaoConfiguration;

import static configuration.ConfigurationConstants.DAO_CONFIG_HOST;
import static configuration.ConfigurationConstants.DAO_DB_NAME;

public class MongoDbUtils {

    public static void dropCollection(String tenantId, String tableName, DaoConfiguration daoConfiguration) {
        String[] hostAndPortFromConfig = daoConfiguration
                .getValueByKeyOrDefault(DAO_CONFIG_HOST, "localhost:27017").split(":");
        String host = hostAndPortFromConfig[0];
        int port = Integer.parseInt(hostAndPortFromConfig[1]);
        MongoClient mongoClient = new MongoClient(host, port);
        DB database = mongoClient.getDB(daoConfiguration.getValueByKeyOrDefault(DAO_DB_NAME, "dbname"));
        String collectionName = tableName + "_" + tenantId;
        if (database.getCollectionNames().contains(collectionName)) {
            DBCollection collection = database.getCollection(collectionName);
            collection.drop();
        }
    }

}